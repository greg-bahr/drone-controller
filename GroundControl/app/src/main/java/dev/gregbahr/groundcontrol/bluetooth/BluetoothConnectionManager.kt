package dev.gregbahr.groundcontrol.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

@Singleton
class BluetoothConnectionManager @Inject constructor() {

    companion object {
        const val TAG = "BluetoothConnectionManager"
    }

    private var socket: BluetoothSocket? = null
    private val isConnecting = AtomicBoolean(false)
    private val isReading = AtomicBoolean(false)

    private var job: Thread? = null
    private val _data = MutableStateFlow(ByteArray(1024))
    val data: StateFlow<ByteArray> get() = _data

    suspend fun connectAsClient(bluetoothDevice: BluetoothDevice, uuid: UUID) = coroutineScope {
        if ((socket == null || !socket!!.isConnected) && isConnecting.compareAndSet(false, true)) {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
            try {
                withContext(Dispatchers.IO) {
                    socket!!.connect()
                }

                job = thread(start = true) {
                    isReading.compareAndSet(false, true)
                    read()
                }
            } catch (e: Exception) {
                isConnecting.compareAndSet(true, false)
                throw e
            }
            isConnecting.compareAndSet(true, false)
        }
    }

    suspend fun write(bytes: ByteArray) = coroutineScope {
        if (socket == null) {
            throw IllegalStateException("Attempted to write without a bluetooth socket")
        } else if (!socket!!.isConnected) {
            Log.d(TAG, "Attempted to write while bluetooth not connected. Reconnecting...")

            withContext(Dispatchers.IO) {
                socket?.connect()
            }
        }

        try {
            withContext(Dispatchers.IO) {
                socket?.outputStream?.write(bytes)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when writing data", e)
            throw e
        }
    }

    private fun read() {
        while (isReading.get()) {
            if (socket == null) {
                throw IllegalStateException("Attempted to read without a bluetooth socket!")
            } else if (!socket!!.isConnected) {
                Log.d(TAG, "Attempted to read while bluetooth not connected. Reconnecting...")
                socket?.connect()
            }

            try {
                val bytes = ByteArray(1024)
                socket?.inputStream?.read(bytes)

                _data.value = bytes
            } catch (e: Exception) {
                Log.e(TAG, "Error reading from bluetooth input stream", e)
            }
        }
    }

    fun close() {
        isReading.compareAndSet(true, false)
        job = null

        socket?.close()
    }
}