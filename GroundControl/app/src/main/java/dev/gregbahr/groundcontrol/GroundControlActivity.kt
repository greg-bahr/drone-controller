package dev.gregbahr.groundcontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.gregbahr.groundcontrol.bluetooth.RSSIPacket
import dev.gregbahr.groundcontrol.mavlink.common.msg_attitude
import dev.gregbahr.groundcontrol.mavlink.common.msg_global_position_int
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class GroundControlActivity : AppCompatActivity() {

    companion object {
        const val TAG = "GroundControlActivity"
    }

    private val model: TelemetryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            val globalPositionFlow = model.getGlobalPositionFlow()
            val attitudeFlow = model.getAttitudeFlow()

            setContent {
                Column {
                    rssiComponent(rssiFlow = model.rssi)
                    gpsComponent(gpsFlow = globalPositionFlow)
                    attitudeComponent(attitudeFlow = attitudeFlow)
                }
            }
        }
    }

    @Composable
    fun rssiComponent(rssiFlow: StateFlow<RSSIPacket>) {
        val rssiPacket by rssiFlow.collectAsState()

        Row(modifier = Modifier.padding(12.dp)) {
            Text("RSSI: ${rssiPacket.rssi}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("SNR: ${rssiPacket.snr}", fontSize = 16.sp)
        }

    }

    @Composable
    fun gpsComponent(gpsFlow: StateFlow<msg_global_position_int>) {
        val gpsUpdate by gpsFlow.collectAsState()

        Row(modifier = Modifier.padding(12.dp)) {
            Text("Lat: ${gpsUpdate.lat}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Lon: ${gpsUpdate.lon}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Alt: ${gpsUpdate.alt}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Vx: ${gpsUpdate.vx}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Vy: ${gpsUpdate.vy}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Vz: ${gpsUpdate.vz}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Hdg: ${gpsUpdate.hdg}", fontSize = 16.sp)
        }
    }

    @Composable
    fun attitudeComponent(attitudeFlow: StateFlow<msg_attitude>) {
        val attitudeUpdate by attitudeFlow.collectAsState()

        Row(modifier = Modifier.padding(12.dp)) {
            Text("Roll: ${attitudeUpdate.roll}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Pitch: ${attitudeUpdate.pitch}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Yaw: ${attitudeUpdate.yaw}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Rollspeed: ${attitudeUpdate.rollspeed}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Pitchspeed: ${attitudeUpdate.pitchspeed}", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("Yawspeed: ${attitudeUpdate.yawspeed}", fontSize = 16.sp)
        }
    }
}