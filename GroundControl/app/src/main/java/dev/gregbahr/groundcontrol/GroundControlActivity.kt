package dev.gregbahr.groundcontrol

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.LocationProvider
import com.mapbox.maps.plugin.locationcomponent.location
import dagger.hilt.android.AndroidEntryPoint
import dev.gregbahr.groundcontrol.mavlink.common.msg_attitude
import dev.gregbahr.groundcontrol.mavlink.common.msg_global_position_int
import dev.gregbahr.groundcontrol.mavlink.common.msg_gps_raw_int
import dev.gregbahr.groundcontrol.mavlink.enums.MAV_STATE
import dev.gregbahr.groundcontrol.mavlink.minimal.msg_heartbeat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class GroundControlActivity : ComponentActivity() {

    companion object {
        const val TAG = "GroundControlActivity"
    }

    private val model: TelemetryViewModel by viewModels()
    private var savedInstanceState: Bundle? = null
    private var gpsUpdateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                    finish()
                }
            }
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        setContent {
            MaterialTheme {
                val showAltitudeDialog = remember { mutableStateOf(false) }
                val takingOff = remember { mutableStateOf(false) }
                var selectedCoords by remember { mutableStateOf(Point.fromLngLat(0.0, 0.0)) }
                val scope = rememberCoroutineScope()

                Column {
                    SelectAltitudeDialog(
                        showDialogState = showAltitudeDialog,
                        takingOff = takingOff,
                        onAltitudeSelect = {
                            scope.launch {
                                if (takingOff.value) {
                                    val result = model.takeoff(it)
                                    Log.i(TAG, "Takeoff result: ${result?.result}")
                                } else {
                                    val result = model.goto(selectedCoords.latitude().toFloat(), selectedCoords.longitude().toFloat(), it)
                                    Log.i(TAG, "Goto result: ${result?.result}")
                                }
                                takingOff.value = false
                            }
                        }
                    )

                    HeartbeatCounter()
                    GpsComponent()

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        ArmButtons()
                        TakeoffLandButtons {
                            showAltitudeDialog.value = true
                            takingOff.value = true
                        }
                    }

                    Row {
                        MapComposable(getLastKnownLocation()) {
                            selectedCoords = it
                            showAltitudeDialog.value = true;
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Point? {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        for (provider in locationManager.getProviders(true)) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                return Point.fromLngLat(location.longitude, location.latitude)
            }
        }

        return null
    }

    @Composable
    fun GpsComponent() {
        var gpsMessage by remember { mutableStateOf(msg_global_position_int()) }
        LaunchedEffect(true) {
            model.packetFlow.transform {
                if (it.msgid == msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT) {
                    emit(msg_global_position_int(it))
                }
            }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, msg_global_position_int()).collect {
                gpsMessage = it
            }
        }

        Row(modifier = Modifier.padding(12.dp)) {
            Text("Lat: ${convertMavlinkDegToDecimal(gpsMessage.lat)}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Lon: ${convertMavlinkDegToDecimal(gpsMessage.lon)}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Alt: ${gpsMessage.alt / 1000.0}", fontSize = 16.sp)
        }
    }

    @Composable
    fun SelectAltitudeDialog(showDialogState: MutableState<Boolean>, takingOff: MutableState<Boolean>, onAltitudeSelect: (Float) -> Unit) {
        var altitude by remember { mutableStateOf("") }

        if (showDialogState.value) {
            AlertDialog(
                onDismissRequest = { showDialogState.value = false; takingOff.value = false; altitude = "" },
                title = { Text("Select Altitude") },
                confirmButton = {
                    Button(onClick = {
                        onAltitudeSelect(altitude.toFloat());
                        altitude = ""
                        showDialogState.value = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialogState.value = false; takingOff.value = false; altitude = "" }) {
                        Text("Cancel")
                    }
                },
                text = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        OutlinedTextField(
                            value = altitude,
                            onValueChange = {
                                if (it.isDigitsOnly()) {
                                    altitude = it
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Altitude in Meters") }
                        )
                    }
                }
            )
        }
    }

    @Composable
    fun TakeoffLandButtons(onTakeoff: () -> Unit) {
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Button(onClick = {
                    scope.launch {
                        onTakeoff()
                    }
                }) {
                    Text("Takeoff")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    scope.launch {
                        model.land()
                    }
                }) {
                    Text(text = "Land")
                }
            }
        }
    }

    @Composable
    fun ArmButtons() {
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Button(onClick = {
                    scope.launch {
                        model.setArmed(true)
                    }
                }) {
                    Text("Arm")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    scope.launch {
                        model.setArmed(false)
                    }
                }) {
                    Text(text = "Disarm")
                }
            }
        }
    }

    private fun parseMavlinkMode(mode: Int, customMode: Int): String {
        var isArmed = false
        var result = ""

        if ((mode shr 7) and 1 != 0) {
            isArmed = true
        }

        result = when(customMode) {
            0 -> "STABILIZE"
            1 -> "ACRO"
            2 -> "ALT_HOLD"
            3 -> "AUTO"
            4 -> "GUIDED"
            5 -> "LOITER"
            6 -> "RTL"
            7 -> "CIRCLE"
            9 -> "LAND"
            else -> "UNKNOWN"
        }

        result += if (isArmed) { " ARMED" } else { " DISARMED" }

        return result
    }

    private fun parseMavlinkState(state: Int): String {
        return when (state) {
            MAV_STATE.MAV_STATE_UNINIT -> "UNINIT"
            MAV_STATE.MAV_STATE_BOOT -> "BOOT"
            MAV_STATE.MAV_STATE_CALIBRATING -> "CALIBRATING"
            MAV_STATE.MAV_STATE_STANDBY -> "STANDBY"
            MAV_STATE.MAV_STATE_ACTIVE -> "ACTIVE"
            MAV_STATE.MAV_STATE_CRITICAL -> "CRITICAL"
            MAV_STATE.MAV_STATE_EMERGENCY -> "EMERGENCY"
            MAV_STATE.MAV_STATE_POWEROFF -> "POWEROFF"
            MAV_STATE.MAV_STATE_FLIGHT_TERMINATION -> "FLIGHT_TERMINATION"
            else -> ""
        }
    }

    @Composable
    fun HeartbeatCounter() {
        var heartbeatCount by remember { mutableStateOf(0) }
        var currentMode by remember { mutableStateOf(0) }
        var currentCustomMode by remember { mutableStateOf(0) }
        var currentState by remember { mutableStateOf(0) }

        LaunchedEffect(true) {
            model.heartbeatFlow.collect {
                heartbeatCount += 1
                currentMode = it.base_mode.toInt()
                currentCustomMode = it.custom_mode.toInt()
                currentState = it.system_status.toInt()
            }
        }

        Row(modifier = Modifier.padding(12.dp)) {
            Text(text = "Heartbeats Received: $heartbeatCount,")
            Text("Mode: ${parseMavlinkMode(currentMode, currentCustomMode)},", modifier = Modifier.padding(horizontal = 12.dp))
            Text("State: ${parseMavlinkState(currentState)},")
            BatteryVoltage()
        }
    }

    @Composable
    fun BatteryVoltage() {
        var voltage by remember { mutableStateOf(0) }

        LaunchedEffect(true) {
            model.getBatteryFlow().collect {
                voltage = it.voltages[0]
            }
        }

        Text("Voltage: ${voltage / 1000.0}v", modifier = Modifier.padding(horizontal = 12.dp))
    }

    private fun convertMavlinkDegToDecimal(num: Int): BigDecimal {
        return BigDecimal(num).apply { setScale(7, RoundingMode.DOWN) }.divide(BigDecimal.TEN.pow(7))
    }

    @Composable
    fun MapComposable(startLocation: Point?, onMapClickListener: (Point) -> Unit) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    val map = getMapboxMap()

                    map.loadStyleUri(Style.SATELLITE_STREETS) {
                        startLocation?.let {
                            map.cameraAnimationsPlugin {
                                easeTo(CameraOptions.Builder().center(it).zoom(17.0).build())
                            }
                        }

                        this.location.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }

                        this.location.setLocationProvider(object : LocationProvider {
                            override fun registerLocationConsumer(locationConsumer: LocationConsumer) {
                                gpsUpdateJob?.cancel()

                                gpsUpdateJob = lifecycleScope.launch {
                                    model.getGlobalPositionFlow().collect {
                                        val point = Point.fromLngLat(
                                            convertMavlinkDegToDecimal(it.lon).toDouble(),
                                            convertMavlinkDegToDecimal(it.lat).toDouble()
                                        )

                                        if (point.latitude() >= -90.0
                                            && point.latitude() <= 90.0
                                            && point.longitude() >= -180.0
                                            && point.longitude() <= 180.0
                                        ) {
                                            locationConsumer.onLocationUpdated(point)
                                        }
                                    }
                                }
                            }

                            override fun unRegisterLocationConsumer(locationConsumer: LocationConsumer) {
                                gpsUpdateJob?.cancel()
                                gpsUpdateJob = null
                            }
                        })
                    }

                    map.addOnMapClickListener {
                        onMapClickListener(it)
                        true
                    }
                }
        })
    }
}