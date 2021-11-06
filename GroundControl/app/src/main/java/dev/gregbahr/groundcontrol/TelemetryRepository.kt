package dev.gregbahr.groundcontrol

import android.util.Log
import dev.gregbahr.groundcontrol.bluetooth.BluetoothConnectionManager
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket
import dev.gregbahr.groundcontrol.mavlink.Parser
import dev.gregbahr.groundcontrol.mavlink.common.*
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_CMD
import dev.gregbahr.groundcontrol.mavlink.messages.MAVLinkMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread
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

    private val _packetFlow: MutableStateFlow<MAVLinkPacket> = MutableStateFlow(MAVLinkPacket(0, true))
    val packetFlow: StateFlow<MAVLinkPacket> get() = _packetFlow

    // Ignore heartbeats, timesyncs, and message intervals
    private var handledMessageIds = mutableSetOf(0, 77, 111, 244)

    init {
        thread {
            runBlocking {
                bluetoothConnectionManager.data.collect {
                    parseBluetoothPacket(it)
                }
            }
        }
    }

    suspend fun sendCommand(command: MAVLinkMessage, timeout: Duration = Duration.seconds(10)): msg_command_ack? = coroutineScope {
        command.isMavlink2 = true

        val completable: CompletableDeferred<msg_command_ack?> = CompletableDeferred()
        val requestJob = launch(Dispatchers.IO) {
            try {
                withTimeout(if (timeout.inWholeMilliseconds != 0L) timeout.inWholeMilliseconds else 60000) {
                    var count = 0
                    while(isActive) {
                        if (command is msg_command_long) {
                            command.confirmation = count.toShort()
                            count++
                        }

                        writePacket(command.pack())
                        delay(1000)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Command timed out without response.")
                completable.complete(null)
            }
        }

        val responseJob = launch(Dispatchers.IO) {
            packetFlow.collect { packet ->
                if (packet.msgid == msg_command_ack.MAVLINK_MSG_ID_COMMAND_ACK) {
                    Log.i(TAG, "ACK")
                    val ack = msg_command_ack(packet)
                    var originalCommand: Int? = null
                    if (command is msg_command_int) {
                        originalCommand = command.command
                    } else if (command is msg_command_long) {
                        originalCommand = command.command
                    }

                    if (originalCommand != null && ack.command == originalCommand) {
                        completable.complete(ack)
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

        launch(Dispatchers.IO) {
            sendCommand(command, timeout = Duration.seconds(0))
        }

        handledMessageIds.add(messageId)
        packetFlow.transform {
            if (it.msgid == messageId) {
                emit(messageTransform.invoke(it))
            }
        }.stateIn(scope, SharingStarted.Lazily, messageTransform.invoke(MAVLinkPacket(0, true)))
    }

    private suspend fun writePacket(packet: MAVLinkPacket) {
        bluetoothConnectionManager.write(packet.encodePacket())
    }

    private fun parseBluetoothPacket(packet: ByteArray) {
        for (i in packet.indices) {
            val byte = packet[i]
            if (mavLinkPacket == null) {
                mavLinkPacket = mavlinkParser.mavlink_parse_char(byte.toInt())
                if (mavLinkPacket != null) {
                    if (!handledMessageIds.contains(mavLinkPacket!!.msgid)) {
                        Log.w(TAG, "Unhandled mavlink message id: ${mavLinkPacket!!.msgid}")
                    }
                    Log.d(TAG, "Message received. ID: ${mavLinkPacket!!.msgid}")

                    _packetFlow.value = mavLinkPacket!!
                    mavLinkPacket = null
                    return
                }
            }
        }
    }
}