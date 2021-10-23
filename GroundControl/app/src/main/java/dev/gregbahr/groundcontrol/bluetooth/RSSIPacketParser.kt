package dev.gregbahr.groundcontrol.bluetooth

class RSSIPacketParser {
    private val packet = RSSIPacket(0, 0)
    private var state = State.INITIALIZED

    val complete: Boolean get() = state == State.FOUND_SNR

    fun parse(num: Byte): RSSIPacket? {
        when (state) {
            State.INITIALIZED -> {
                if (num == 0xAB.toByte()) {
                    state = State.FOUND_HEADER
                }
            }
            State.FOUND_HEADER -> {
                packet.rssi = num
                state = State.FOUND_RSSI
            }
            State.FOUND_RSSI -> {
                packet.snr = num
                state = State.FOUND_SNR
                return packet
            }
            else -> return packet
        }
        return null
    }

    data class RSSIPacket(var rssi: Byte, var snr: Byte)

    private enum class State {
        INITIALIZED,
        FOUND_HEADER,
        FOUND_RSSI,
        FOUND_SNR
    }
}