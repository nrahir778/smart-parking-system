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
        if (trimmed.isEmpty()) return

        val currentLogs = _parkingState.value.rawTelemetryLog.toMutableList()
        if (currentLogs.size > 50) currentLogs.removeAt(0)
        currentLogs.add("${System.currentTimeMillis() % 100000}: $trimmed")

        var s1 = _parkingState.value.slot1
        var s2 = _parkingState.value.slot2
        var s3 = _parkingState.value.slot3
        var total = _parkingState.value.totalOccupied
        var gate = _parkingState.value.gateState
        var buzzer = _parkingState.value.buzzerAlert
        var recognizedChange = false

        // 1. JSON format support: {"s1":1, "s2":0, ...} or {"slot1":true, ...}
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                val json = org.json.JSONObject(trimmed)
                if (json.has("s1")) { s1 = parseSlotBool(json.getString("s1")); recognizedChange = true }
                if (json.has("slot1")) { s1 = parseSlotBool(json.getString("slot1")); recognizedChange = true }
                if (json.has("s2")) { s2 = parseSlotBool(json.getString("s2")); recognizedChange = true }
                if (json.has("slot2")) { s2 = parseSlotBool(json.getString("slot2")); recognizedChange = true }
                if (json.has("s3")) { s3 = parseSlotBool(json.getString("s3")); recognizedChange = true }
                if (json.has("slot3")) { s3 = parseSlotBool(json.getString("slot3")); recognizedChange = true }
                if (json.has("gate")) {
                    gate = if (json.getString("gate").equals("CLOSED", true)) GateState.CLOSED else GateState.OPEN
                    recognizedChange = true
                }
            } catch (e: Exception) {
                // fall through to token parsing
            }
        }

        // 2. Simple 3-digit comma/space format e.g. "1,0,1" or "1 0 1"
        if (!recognizedChange) {
            val parts = trimmed.split(Regex("[,|;\\s]+")).filter { it.isNotBlank() }
            if (parts.size == 3 && parts.all { it == "0" || it == "1" }) {
                s1 = parts[0] == "1"
                s2 = parts[1] == "1"
                s3 = parts[2] == "1"
                recognizedChange = true
            }
        }

        // 3. Key-Value token parsing: e.g. "S1: OCCUPIED, S2: VACANT" or individual lines
        if (!recognizedChange) {
            val tokens = trimmed.split(Regex("[,|;]+"))
            for (token in tokens) {
                val t = token.trim()
                val lower = t.lowercase()
                when {
                    lower.startsWith("s1:") || lower.startsWith("slot 1:") || lower.startsWith("slot1:") -> {
                        val v = t.substringAfter(":").trim()
                        s1 = parseSlotBool(v)
                        recognizedChange = true
                    }
                    lower.startsWith("s2:") || lower.startsWith("slot 2:") || lower.startsWith("slot2:") -> {
                        val v = t.substringAfter(":").trim()
                        s2 = parseSlotBool(v)
                        recognizedChange = true
                    }
                    lower.startsWith("s3:") || lower.startsWith("slot 3:") || lower.startsWith("slot3:") -> {
                        val v = t.substringAfter(":").trim()
                        s3 = parseSlotBool(v)
                        recognizedChange = true
                    }
                    lower.startsWith("total:") || lower.startsWith("count:") -> {
                        t.substringAfter(":").trim().toIntOrNull()?.let {
                            total = it
                            recognizedChange = true
                        }
                    }
                    lower.startsWith("gate:") -> {
                        val stateStr = t.substringAfter(":").trim()
                        gate = if (stateStr.equals("CLOSED", ignoreCase = true)) GateState.CLOSED else GateState.OPEN
                        recognizedChange = true
                    }
                }
            }
        }

        val calculatedTotal = (if (s1) 1 else 0) + (if (s2) 1 else 0) + (if (s3) 1 else 0)
        val allFull = calculatedTotal >= 3 || total >= 3
        buzzer = allFull
        if (allFull) {
            gate = GateState.CLOSED
        }

        val prevState = _parkingState.value
        val newState = prevState.copy(
            hasReceivedData = prevState.hasReceivedData || recognizedChange,
            isConnected = true,
            slot1 = s1,
            slot2 = s2,
            slot3 = s3,
            totalOccupied = calculatedTotal,
            gateState = gate,
            buzzerAlert = buzzer,
            lastUpdated = if (recognizedChange) System.currentTimeMillis() else prevState.lastUpdated,
            rawTelemetryLog = currentLogs
        )

        _parkingState.value = newState
        if (recognizedChange && (prevState.slot1 != s1 || prevState.slot2 != s2 || prevState.slot3 != s3 || prevState.gateState != gate)) {
            gatewaySyncManager.syncParkingState(newState)
        }
    }

    private fun parseSlotBool(v: String): Boolean {
        val s = v.trim().lowercase()
        return s == "occupied" || s == "1" || s == "high" || s == "true" || s == "parked" || s == "yes"
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
