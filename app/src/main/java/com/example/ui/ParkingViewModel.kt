package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BluetoothManager
import com.example.data.model.BluetoothDeviceInfo
import com.example.data.model.ConnectionStatus
import com.example.data.model.GateState
import com.example.data.model.GatewayConfig
import com.example.data.model.ParkingLotState
import com.example.gateway.GatewaySyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    val bluetoothManager = BluetoothManager(application)
    val gatewaySyncManager = GatewaySyncManager()

    // Pure initial state: NO DEMO READINGS. Has not received data yet.
    private val _parkingState = MutableStateFlow(
        ParkingLotState(
            hasReceivedData = false,
            isConnected = false,
            slot1 = false,
            slot2 = false,
            slot3 = false,
            totalOccupied = 0,
            gateState = GateState.OPEN,
            buzzerAlert = false,
            lastUpdated = 0L
        )
    )
    val parkingState: StateFlow<ParkingLotState> = _parkingState.asStateFlow()

    val connectionStatus: StateFlow<ConnectionStatus> = bluetoothManager.connectionStatus
    val gatewayConfig: StateFlow<GatewayConfig> = gatewaySyncManager.config

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDeviceInfo>> = _pairedDevices.asStateFlow()

    init {
        refreshPairedDevices()
        observeBluetoothConnection()
        observeBluetoothTelemetry()
    }

    fun refreshPairedDevices() {
        _pairedDevices.value = bluetoothManager.getPairedDevices()
    }

    private fun observeBluetoothConnection() {
        viewModelScope.launch {
            bluetoothManager.connectionStatus.collect { status ->
                val connected = status is ConnectionStatus.Connected
                _parkingState.value = _parkingState.value.copy(
                    isConnected = connected,
                    connectionStatus = status
                )
            }
        }
    }

    private fun observeBluetoothTelemetry() {
        viewModelScope.launch {
            bluetoothManager.incomingTelemetryLine.collect { rawLine ->
                parseArduinoTelemetryLine(rawLine)
            }
        }
    }

    fun parseArduinoTelemetryLine(line: String) {
        val trimmed = line.trim()
        val currentLogs = _parkingState.value.rawTelemetryLog.toMutableList()
        if (currentLogs.size > 50) currentLogs.removeAt(0)
        currentLogs.add("${System.currentTimeMillis() % 100000}: $trimmed")

        var s1 = _parkingState.value.slot1
        var s2 = _parkingState.value.slot2
        var s3 = _parkingState.value.slot3
        var total = _parkingState.value.totalOccupied
        var gate = _parkingState.value.gateState
        var buzzer = _parkingState.value.buzzerAlert

        when {
            trimmed.startsWith("S1:", ignoreCase = true) -> {
                s1 = trimmed.substring(3).trim().equals("OCCUPIED", ignoreCase = true)
            }
            trimmed.startsWith("S2:", ignoreCase = true) -> {
                s2 = trimmed.substring(3).trim().equals("OCCUPIED", ignoreCase = true)
            }
            trimmed.startsWith("S3:", ignoreCase = true) -> {
                s3 = trimmed.substring(3).trim().equals("OCCUPIED", ignoreCase = true)
            }
            trimmed.startsWith("TOTAL:", ignoreCase = true) -> {
                trimmed.substring(6).trim().toIntOrNull()?.let { total = it }
            }
            trimmed.startsWith("GATE:", ignoreCase = true) -> {
                val stateStr = trimmed.substring(5).trim()
                gate = if (stateStr.equals("CLOSED", ignoreCase = true)) GateState.CLOSED else GateState.OPEN
            }
        }

        val calculatedTotal = (if (s1) 1 else 0) + (if (s2) 1 else 0) + (if (s3) 1 else 0)
        val finalTotal = if (total in 0..3) total else calculatedTotal
        val allFull = calculatedTotal >= 3 || finalTotal >= 3
        buzzer = allFull
        if (allFull) {
            gate = GateState.CLOSED
        }

        // Updated with real Arduino reading!
        val newState = _parkingState.value.copy(
            hasReceivedData = true,
            isConnected = true,
            slot1 = s1,
            slot2 = s2,
            slot3 = s3,
            totalOccupied = calculatedTotal,
            gateState = gate,
            buzzerAlert = buzzer,
            lastUpdated = System.currentTimeMillis(),
            rawTelemetryLog = currentLogs
        )

        _parkingState.value = newState
        gatewaySyncManager.syncParkingState(newState)
    }

    fun connectToDevice(address: String, name: String) {
        bluetoothManager.connectToDevice(address, name)
    }

    fun disconnectBluetooth() {
        bluetoothManager.disconnect()
        _parkingState.value = _parkingState.value.copy(
            isConnected = false
        )
    }

    fun updateGatewayEndpoint(url: String) {
        gatewaySyncManager.updateEndpoint(url)
    }

    fun toggleGateway(enabled: Boolean) {
        gatewaySyncManager.toggleGateway(enabled)
    }

    fun testCloudSync() {
        gatewaySyncManager.syncParkingState(_parkingState.value, force = true)
    }

    fun toggleSlot(slotIndex: Int) {
        val s1 = if (slotIndex == 1) !_parkingState.value.slot1 else _parkingState.value.slot1
        val s2 = if (slotIndex == 2) !_parkingState.value.slot2 else _parkingState.value.slot2
        val s3 = if (slotIndex == 3) !_parkingState.value.slot3 else _parkingState.value.slot3
        val total = (if (s1) 1 else 0) + (if (s2) 1 else 0) + (if (s3) 1 else 0)
        val gate = if (total >= 3) GateState.CLOSED else GateState.OPEN
        val buzzer = total >= 3

        val newState = _parkingState.value.copy(
            hasReceivedData = true,
            slot1 = s1,
            slot2 = s2,
            slot3 = s3,
            totalOccupied = total,
            gateState = gate,
            buzzerAlert = buzzer,
            lastUpdated = System.currentTimeMillis()
        )
        _parkingState.value = newState
        gatewaySyncManager.syncParkingState(newState)
    }
}
