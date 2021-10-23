package dev.gregbahr.groundcontrol

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class TelemetryViewModel @Inject constructor(
    telemetryRepository: TelemetryRepository
) : ViewModel() {
    val rssi = telemetryRepository.rssi
    val snr = telemetryRepository.snr
    val heartbeat = telemetryRepository.heartbeat
}