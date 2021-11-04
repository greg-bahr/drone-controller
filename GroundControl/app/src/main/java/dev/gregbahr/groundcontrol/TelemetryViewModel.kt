package dev.gregbahr.groundcontrol

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.gregbahr.groundcontrol.mavlink.common.*
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_CMD
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_CMD_ACK
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_FRAME
import dev.gregbahr.groundcontrol.mavlink.minimal.msg_heartbeat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val telemetryRepository: TelemetryRepository
) : ViewModel() {
    val heartbeatFlow = telemetryRepository.packetFlow.transform {
        if (it.msgid == msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT) {
            emit(msg_heartbeat(it))
        }
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, msg_heartbeat())

    val packetFlow = telemetryRepository.packetFlow

    suspend fun getGlobalPositionFlow(): StateFlow<msg_global_position_int> {
        return telemetryRepository.requestMessageInterval(msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT, Duration.milliseconds(250)) {
            msg_global_position_int(it)
        }
    }

    suspend fun getAttitudeFlow(): StateFlow<msg_attitude> {
        return telemetryRepository.requestMessageInterval(msg_attitude.MAVLINK_MSG_ID_ATTITUDE, Duration.milliseconds(250)) {
            msg_attitude(it)
        }
    }

    suspend fun getBatteryFlow(): StateFlow<msg_battery_status> {
        return telemetryRepository.requestMessageInterval(msg_battery_status.MAVLINK_MSG_ID_BATTERY_STATUS, Duration.milliseconds(250)) {
            msg_battery_status(it)
        }
    }

    private suspend fun setModeGuided(): msg_command_ack? {
        val command = msg_command_long(
            89f,
            4f,
            0f,
            0f,
            0f,
            0f,
            0f,
            MAV_CMD.MAV_CMD_DO_SET_MODE,
            1,
            0,
            0
        )

        return telemetryRepository.sendCommand(command)
    }

    private suspend fun setHomeCurrentLocation(): msg_command_ack? {
        val command = msg_command_long(
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            MAV_CMD.MAV_CMD_DO_SET_HOME,
            1,
            0,
            0
        )

        return telemetryRepository.sendCommand(command)
    }

    suspend fun setArmed(armed: Boolean): msg_command_ack? = coroutineScope {
        launch(Dispatchers.IO) {
            setModeGuided()
        }

        launch(Dispatchers.IO) {
            setHomeCurrentLocation()
        }

        val param = if (armed) 1f else 0f
        val command = msg_command_long(
            param,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM,
            1,
            0,
            0
        )

        return@coroutineScope telemetryRepository.sendCommand(command)
    }

    suspend fun takeoff(altitude: Float): msg_command_ack? {
        val command = msg_command_long(
            0f,
            0f,
            0f,
            Float.NaN,
            0f,
            0f,
            altitude,
            MAV_CMD.MAV_CMD_NAV_TAKEOFF,
            1,
            0,
            0
        )

        return telemetryRepository.sendCommand(command)
    }

    suspend fun goto(latitude: Float, longitude: Float, altitude: Float): msg_command_ack? {
        val command = msg_command_int(
            -1f,
            0f,
            0f,
            Float.NaN,
            BigDecimal(latitude.toDouble()).multiply(BigDecimal.TEN.pow(7)).toInt(),
            BigDecimal(longitude.toDouble()).multiply(BigDecimal.TEN.pow(7)).toInt(),
            altitude,
            MAV_CMD.MAV_CMD_DO_REPOSITION,
            1,
            0,
            MAV_FRAME.MAV_FRAME_GLOBAL_TERRAIN_ALT.toShort(),
            0,
            0
        )

        return telemetryRepository.sendCommand(command)
    }

    suspend fun land(): msg_command_ack? {
        val command = msg_command_long(
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            MAV_CMD.MAV_CMD_NAV_LAND,
            1,
            0,
            0
        )

        return telemetryRepository.sendCommand(command)
    }
}