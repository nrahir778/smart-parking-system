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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    val bluetoothManager = BluetoothManager(application)
    val gatewaySyncManager = GatewaySyncManager()

    private val _parkingState = MutableStateFlow(
        ParkingLotState(
            slot1 = true,
            slot2 = false,
            slot3 = false,
            totalOccupied = 1,
            gateState = GateState.OPEN,
            buzzerAlert = false
        )
    )
    val parkingState: StateFlow<ParkingLotState> = _parkingState.asStateFlow()

    val connectionStatus: StateFlow<ConnectionStatus> = bluetoothManager.connectionStatus
    val gatewayConfig: StateFlow<GatewayConfig> = gatewaySyncManager.config

    private val _isSimulationActive = MutableStateFlow(false)
    val isSimulationActive: StateFlow<Boolean> = _isSimulationActive.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDeviceInfo>> = _pairedDevices.asStateFlow()

    private var simulationJob: Job? = null

    init {
        refreshPairedDevices()
        observeBluetoothTelemetry()
        // Sync initial default state to gateway
        gatewaySyncManager.syncParkingState(_parkingState.value, force = true)
    }

    fun refreshPairedDevices() {
        _pairedDevices.value = bluetoothManager.getPairedDevices()
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
        if (currentLogs.size > 40) currentLogs.removeAt(0)
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

        // Compute total and buzzer logic aligned with Arduino code
        val calculatedTotal = (if (s1) 1 else 0) + (if (s2) 1 else 0) + (if (s3) 1 else 0)
        val finalTotal = if (total in 0..3) total else calculatedTotal
        val allFull = calculatedTotal >= 3 || finalTotal >= 3
        buzzer = allFull
        if (allFull) {
            gate = GateState.CLOSED
        }

        val newState = _parkingState.value.copy(
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

    fun toggleSlotManually(slotId: Int) {
        val cur = _parkingState.value
        val newS1 = if (slotId == 1) !cur.slot1 else cur.slot1
        val newS2 = if (slotId == 2) !cur.slot2 else cur.slot2
        val newS3 = if (slotId == 3) !cur.slot3 else cur.slot3

        val total = (if (newS1) 1 else 0) + (if (newS2) 1 else 0) + (if (newS3) 1 else 0)
        val allOccupied = total >= 3
        val gate = if (allOccupied) GateState.CLOSED else GateState.OPEN

        val newState = cur.copy(
            slot1 = newS1,
            slot2 = newS2,
            slot3 = newS3,
            totalOccupied = total,
            gateState = gate,
            buzzerAlert = allOccupied,
            lastUpdated = System.currentTimeMillis()
        )
        _parkingState.value = newState
        gatewaySyncManager.syncParkingState(newState, force = true)
    }

    fun setAllSlotsState(s1: Boolean, s2: Boolean, s3: Boolean) {
        val total = (if (s1) 1 else 0) + (if (s2) 1 else 0) + (if (s3) 1 else 0)
        val allOccupied = total >= 3
        val newState = _parkingState.value.copy(
            slot1 = s1,
            slot2 = s2,
            slot3 = s3,
            totalOccupied = total,
            gateState = if (allOccupied) GateState.CLOSED else GateState.OPEN,
            buzzerAlert = allOccupied,
            lastUpdated = System.currentTimeMillis()
        )
        _parkingState.value = newState
        gatewaySyncManager.syncParkingState(newState, force = true)
    }

    fun toggleAutoTrafficSimulation(enabled: Boolean) {
        _isSimulationActive.value = enabled
        simulationJob?.cancel()
        if (enabled) {
            simulationJob = viewModelScope.launch {
                val demoPatterns = listOf(
                    Triple(true, false, false),
                    Triple(true, true, false),
                    Triple(true, true, true), // Full -> Gate Closes, Buzzer Alerts!
                    Triple(false, true, true),
                    Triple(false, false, true),
                    Triple(false, false, false)
                )
                var index = 0
                while (isActive) {
                    val pattern = demoPatterns[index % demoPatterns.size]
                    setAllSlotsState(pattern.first, pattern.second, pattern.third)
                    index++
                    delay(3500)
                }
            }
        }
    }

    fun connectToDevice(address: String, name: String) {
        _isSimulationActive.value = false
        simulationJob?.cancel()
        bluetoothManager.connectToDevice(address, name)
    }

    fun disconnectBluetooth() {
        bluetoothManager.disconnect()
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
}
