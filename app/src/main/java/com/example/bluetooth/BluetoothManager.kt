package com.example.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager as AndroidBluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.model.BluetoothDeviceInfo
import com.example.data.model.ConnectionStatus
import com.example.data.model.GateState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.UUID

class BluetoothManager(private val context: Context) {

    companion object {
        private const val TAG = "HC05_Bluetooth"
        // Standard Serial Port Profile (SPP) UUID for HC-05 / HC-06
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? AndroidBluetoothManager
        manager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }

    private var socket: BluetoothSocket? = null
    private var readJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _incomingTelemetryLine = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val incomingTelemetryLine: SharedFlow<String> = _incomingTelemetryLine.asSharedFlow()

    fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDeviceInfo> {
        if (!hasBluetoothPermission()) return emptyList()
        val adapter = bluetoothAdapter ?: return emptyList()
        return try {
            adapter.bondedDevices?.map { device ->
                BluetoothDeviceInfo(
                    name = device.name ?: "Unknown Device",
                    address = device.address,
                    isPaired = true
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting paired devices", e)
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(address: String, deviceName: String = "HC-05") {
        if (!hasBluetoothPermission()) {
            _connectionStatus.value = ConnectionStatus.Error("Bluetooth permission not granted")
            return
        }

        disconnect()
        _connectionStatus.value = ConnectionStatus.Connecting(deviceName)

        scope.launch {
            try {
                val adapter = bluetoothAdapter
                if (adapter == null || !adapter.isEnabled) {
                    _connectionStatus.value = ConnectionStatus.Error("Bluetooth is turned off")
                    return@launch
                }

                val device = adapter.getRemoteDevice(address)
                // Cancel discovery before connecting as recommended by Android Docs
                try {
                    adapter.cancelDiscovery()
                } catch (_: Exception) {}

                val newSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                newSocket.connect()
                socket = newSocket

                _connectionStatus.value = ConnectionStatus.Connected(
                    deviceName = device.name ?: deviceName,
                    deviceAddress = address
                )

                startReading(newSocket)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect to $address", e)
                _connectionStatus.value = ConnectionStatus.Error(
                    "Connection failed: ${e.localizedMessage ?: "Unknown error"}. Check HC-05 power (5V) & pairing code (1234 or 0000)."
                )
                disconnect()
            }
        }
    }

    private fun startReading(activeSocket: BluetoothSocket) {
        readJob?.cancel()
        readJob = scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(activeSocket.inputStream))
                while (isActive && activeSocket.isConnected) {
                    val line = reader.readLine() ?: break
                    val trimmed = line.trim()
                    if (trimmed.isNotEmpty()) {
                        _incomingTelemetryLine.emit(trimmed)
                    }
                }
            } catch (e: Exception) {
                if (isActive) {
                    Log.w(TAG, "Bluetooth read error: ${e.message}")
                    _connectionStatus.value = ConnectionStatus.Error("Connection lost: ${e.localizedMessage}")
                }
            } finally {
                if (_connectionStatus.value is ConnectionStatus.Connected) {
                    _connectionStatus.value = ConnectionStatus.Disconnected
                }
            }
        }
    }

    fun sendCommand(command: String) {
        scope.launch {
            try {
                val currentSocket = socket
                if (currentSocket != null && currentSocket.isConnected) {
                    val out: OutputStream = currentSocket.outputStream
                    out.write(command.toByteArray())
                    out.flush()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write command", e)
            }
        }
    }

    fun disconnect() {
        readJob?.cancel()
        readJob = null
        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null
        _connectionStatus.value = ConnectionStatus.Disconnected
    }
}
