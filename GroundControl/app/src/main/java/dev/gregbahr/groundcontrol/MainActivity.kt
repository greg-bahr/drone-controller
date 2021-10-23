package dev.gregbahr.groundcontrol

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.gregbahr.groundcontrol.bluetooth.BluetoothConnectionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import javax.inject.Inject

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var bluetoothConnectionManager: BluetoothConnectionManager

    companion object {
        val BLUETOOTH_SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter

        if (!adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_CANCELED) {
                    finish()
                }
            }
            .launch(enableBtIntent)
        }
        
        setContent {
            MaterialTheme {
                BondedDeviceList(adapter.bondedDevices.toList())
            }
        }
    }
    
    @Composable
    private fun BondedDeviceList(devices: List<BluetoothDevice>) {
        LazyColumn {
            items(devices) { device ->
                BondedDeviceRow(device) {
                    lifecycleScope.launch { connectToDevice(device) }
                }
            }
        }
    }

    @Composable
    private fun BondedDeviceRow(device: BluetoothDevice, onClick: () -> Unit) {
        Column(modifier = Modifier.clickable(onClick = onClick)) {
            Row(modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth()
            ) {
                Text(text = device.name, fontSize = 30.sp)
            }
            Divider()
        }
    }

    private suspend fun connectToDevice(device: BluetoothDevice) {
        try {
            Log.i(TAG, "Connecting to bluetooth device: ${device.name}")
            bluetoothConnectionManager.connectAsClient(device, BLUETOOTH_SERIAL_UUID)
            Log.i(TAG, "Starting ground control...")
            Toast.makeText(this@MainActivity, "Connected!", Toast.LENGTH_LONG).show()
            val intent = Intent(this@MainActivity, GroundControlActivity::class.java)
            startActivity(intent)
        } catch (e: IOException) {
            Toast.makeText(this@MainActivity, "Failed to connect to device!", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "Failed to connect to device: ${device.name}", e)
        }
    }
}