package com.example.data.model

enum class GateState {
    OPEN,
    CLOSED
}

enum class CarType(val displayName: String, val bodyColor: Long, val styleName: String) {
    RED_SEDAN("Red Aero Sedan", 0xFFDC2626, "Sport Sedan"),
    BLUE_SUV("Midnight Blue SUV", 0xFF2563EB, "Electric SUV"),
    YELLOW_SPORTS("Cyber Amber GT", 0xFFF59E0B, "Performance Coupe")
}

data class ParkingSlotInfo(
    val id: Int,
    val isOccupied: Boolean,
    val carType: CarType,
    val label: String = "Slot $id",
    val occupiedSince: Long? = null,
    val lastDistanceCm: Float = 0f
)

data class ParkingLotState(
    val slot1: Boolean = false,
    val slot2: Boolean = false,
    val slot3: Boolean = false,
    val totalOccupied: Int = 0,
    val gateState: GateState = GateState.OPEN,
    val buzzerAlert: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val rawTelemetryLog: List<String> = emptyList(),
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected
) {
    val totalSlots: Int = 3
    val availableCount: Int
        get() = (totalSlots - totalOccupied).coerceIn(0, totalSlots)

    val isFull: Boolean
        get() = totalOccupied >= totalSlots
}

data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val isPaired: Boolean = true
)

sealed class ConnectionStatus {
    object Disconnected : ConnectionStatus()
    data class Connecting(val deviceName: String) : ConnectionStatus()
    data class Connected(val deviceName: String, val deviceAddress: String) : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
    object Simulation : ConnectionStatus()
}

data class GatewayConfig(
    val enabled: Boolean = true,
    val endpointUrl: String = "https://smart-parking-iot-default-rtdb.firebaseio.com/parking_live.json",
    val autoSyncOnChange: Boolean = true,
    val syncIntervalMs: Long = 1000L,
    val lastSyncTime: Long = 0L,
    val lastStatusCode: Int = 0,
    val lastStatusMessage: String = "Ready",
    val totalPacketsSent: Int = 0,
    val lastPayloadJson: String = ""
)
