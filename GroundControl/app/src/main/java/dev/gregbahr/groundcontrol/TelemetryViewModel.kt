package dev.gregbahr.groundcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.gregbahr.groundcontrol.mavlink.common.msg_attitude
import dev.gregbahr.groundcontrol.mavlink.common.msg_global_position_int
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val telemetryRepository: TelemetryRepository
) : ViewModel() {
    val rssi = telemetryRepository.rssi

    suspend fun getGlobalPositionFlow(): StateFlow<msg_global_position_int> {
        return telemetryRepository.requestMessageInterval(msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT) {
            msg_global_position_int(it)
        }
    }

    suspend fun getAttitudeFlow(): StateFlow<msg_attitude> {
        return telemetryRepository.requestMessageInterval(msg_attitude.MAVLINK_MSG_ID_ATTITUDE, Duration.milliseconds(250)) {
            msg_attitude(it)
        }
    }
}