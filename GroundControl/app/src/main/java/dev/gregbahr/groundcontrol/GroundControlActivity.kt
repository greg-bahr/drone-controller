package dev.gregbahr.groundcontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroundControlActivity : AppCompatActivity() {

    private val model: TelemetryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            rssiComponent(rssiFlow = model.rssi, snrFlow = model.snr)
        }
    }

    @Composable
    fun rssiComponent(rssiFlow: StateFlow<Int>, snrFlow: StateFlow<Int>) {
        val rssi by rssiFlow.collectAsState()
        val snr by snrFlow.collectAsState()

        Row(modifier = Modifier.padding(12.dp)) {
            Text("RSSI: $rssi", fontSize = 16.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Text("SNR: $snr", fontSize = 16.sp)
        }

    }
}