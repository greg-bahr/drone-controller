package dev.gregbahr.groundcontrol

import android.util.Log
import dev.gregbahr.groundcontrol.bluetooth.BluetoothConnectionManager
import dev.gregbahr.groundcontrol.bluetooth.RSSIPacketParser
import dev.gregbahr.groundcontrol.mavlink.MAVLinkPacket
import dev.gregbahr.groundcontrol.mavlink.Parser
import dev.gregbahr.groundcontrol.mavlink.minimal.msg_heartbeat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryRepository @Inject constructor(private val bluetoothConnectionManager: BluetoothConnectionManager) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var mavlinkParser = Parser()
    private var mavLinkPacket: MAVLinkPacket? = null

    private var rssiParser = RSSIPacketParser()

    private val _heartbeat: MutableStateFlow<msg_heartbeat> = MutableStateFlow(msg_heartbeat())
    val heartbeat: StateFlow<msg_heartbeat> get() = _heartbeat

    private val _rssi = MutableStateFlow(0)
    val rssi: StateFlow<Int> get() = _rssi

    private val _snr = MutableStateFlow(0)
    val snr: StateFlow<Int> get() = _snr

    init {
        scope.launch {
            bluetoothConnectionManager.data.collect {
                Log.d("TelemetryRepository", it.toUByteArray().toString())
                parseBluetoothPacket(it)
            }
        }
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
                    _rssi.value = rssiPacket.rssi.toInt()
                    _snr.value = rssiPacket.snr.toInt()
                }
            } else if (mavLinkPacket == null) {
                mavLinkPacket = mavlinkParser.mavlink_parse_char(byte.toInt())
                if (mavLinkPacket != null) {
                    parseMavlinkPacket()
                }
            }
        }

        if (rssiParser.complete && mavLinkPacket != null) {
            rssiParser = RSSIPacketParser()
            mavlinkParser = Parser()
            mavLinkPacket = null
        }
    }

    private fun parseMavlinkPacket() {
        when(mavLinkPacket?.msgid) {
            msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT -> _heartbeat.value = msg_heartbeat(mavLinkPacket)
        }
    }
}