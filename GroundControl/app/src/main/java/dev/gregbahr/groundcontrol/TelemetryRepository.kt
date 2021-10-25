package dev.gregbahr.groundcontrol

import android.util.Log
import dev.gregbahr.groundcontrol.bluetooth.BluetoothConnectionManager
import dev.gregbahr.groundcontrol.bluetooth.RSSIPacket
import dev.gregbahr.groundcontrol.bluetooth.RSSIPacketParser
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket
import dev.gregbahr.groundcontrol.mavlink.Parser
import dev.gregbahr.groundcontrol.mavlink.common.msg_command_ack
import dev.gregbahr.groundcontrol.mavlink.common.msg_command_long
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_CMD
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_CMD_ACK
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Singleton
class TelemetryRepository @Inject constructor(private val bluetoothConnectionManager: BluetoothConnectionManager) {

    companion object {
        const val TAG = "TelemetryRepository"
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    private var mavlinkParser = Parser()
    private var mavLinkPacket: MAVLinkPacket? = null

    private var rssiParser = RSSIPacketParser()

    private val _packetFlow: MutableStateFlow<MAVLinkPacket> = MutableStateFlow(MAVLinkPacket(0, true))
    val packetFlow: StateFlow<MAVLinkPacket> get() = _packetFlow

    private val _rssi = MutableStateFlow(RSSIPacket(0, 0))
    val rssi: StateFlow<RSSIPacket> get() = _rssi

    init {
        scope.launch {
            bluetoothConnectionManager.data.collect {
                parseBluetoothPacket(it)
            }
        }
    }

    suspend fun <T : MAVLinkMessage> requestMessageInterval(
        messageId: Int,
        interval: Duration = Duration.seconds(1),
        messageTransform: (MAVLinkPacket) -> T
    ): StateFlow<T> = coroutineScope {
        val command = msg_command_long(
            messageId.toFloat(),
            interval.inWholeMicroseconds.toFloat(),
            0f,
            0f,
            0f,
            0f,
            0f,
            MAV_CMD.MAV_CMD_SET_MESSAGE_INTERVAL,
            1,
            0,
            0
        )
        command.isMavlink2 = true

        val completable: CompletableDeferred<StateFlow<T>> = CompletableDeferred()
        val requestJob = launch(Dispatchers.IO) {
            try {
                withTimeout(10000) {
                    while(isActive) {
                        writePacket(command.pack())
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                completable.completeExceptionally(e)
            }
        }

        val responseJob = launch(Dispatchers.IO) {
            packetFlow.collect { packet ->
                if (packet.msgid == msg_command_ack.MAVLINK_MSG_ID_COMMAND_ACK) {
                    val message = msg_command_ack(packet)

                    if (message.command == MAV_CMD.MAV_CMD_SET_MESSAGE_INTERVAL && message.result.toInt() == MAV_CMD_ACK.MAV_CMD_ACK_OK) {
                        val transformedFlow = packetFlow.transform {
                            if (it.msgid == messageId) {
                                emit(messageTransform.invoke(it))
                            }
                        }

                        completable.complete(transformedFlow.stateIn(scope, SharingStarted.Lazily, messageTransform.invoke(MAVLinkPacket(0, true))))
                    } else if (message.command == MAV_CMD.MAV_CMD_SET_MESSAGE_INTERVAL) {
                        completable.completeExceptionally(IllegalArgumentException("Request for message interval failed with result code: ${message.result}"))
                    }
                }
            }
        }

        completable.invokeOnCompletion {
            requestJob.cancel()
            responseJob.cancel()
        }
        return@coroutineScope completable.await()
    }

    private suspend fun writePacket(packet: MAVLinkPacket) {
        bluetoothConnectionManager.write(packet.encodePacket())
    }

    private fun parseBluetoothPacket(packet: ByteArray) {
        for (i in packet.indices) {
            if (packet[i] == 0.toByte()) {
                var numFound = false
                for (j in 1..9) {
                    if (packet[j] != 0.toByte()) {
                        numFound = true
                        break
                    }
                }
                if (!numFound) {
                    return
                }
            }

            val byte = packet[i]
            if (!rssiParser.complete) {
                val rssiPacket = rssiParser.parse(byte)
                if (rssiPacket != null) {
                    _rssi.value = rssiPacket
                }
            } else if (mavLinkPacket == null) {
                mavLinkPacket = mavlinkParser.mavlink_parse_char(byte.toInt())
                if (mavLinkPacket != null) {
                    _packetFlow.value = mavLinkPacket!!
                }
            }
        }

        if (rssiParser.complete && mavLinkPacket != null) {
            rssiParser = RSSIPacketParser()
            mavlinkParser = Parser()
            mavLinkPacket = null
        }
    }
}