package com.example.data.model

enum class GateState {
    OPEN,
    CLOSED
}

enum class CarType(val displayName: String, val bodyColor: Long, val styleName: String) {
    GREEN_POLICE("Lime Green Police Cruiser", 0xFF84CC16, "Police Interceptor"),
    GREEN_SPORTS("Lime Green Sports GT", 0xFFA3E635, "Sports Coupe"),
    RED_VINTAGE("Vintage Red Classic Coupe", 0xFFDC2626, "Vintage Classic");

    companion object {
        // Compatibility aliases
        val RED_SEDAN = GREEN_POLICE
        val BLUE_SUV = GREEN_SPORTS
        val YELLOW_SPORTS = RED_VINTAGE
    }
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
    val hasReceivedData: Boolean = false,
    val isConnected: Boolean = false,
    val slot1: Boolean = false,
    val slot2: Boolean = false,
    val slot3: Boolean = false,
    val totalOccupied: Int = 0,
    val gateState: GateState = GateState.OPEN,
    val buzzerAlert: Boolean = false,
    val lastUpdated: Long = 0L,
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
}

data class GatewayConfig(
    val enabled: Boolean = true,
    val endpointUrl: String = "",
    val autoSyncOnChange: Boolean = true,
    val syncIntervalMs: Long = 1000L,
    val lastSyncTime: Long = 0L,
    val lastStatusCode: Int = 0,
    val lastStatusMessage: String = "Configure Firebase URL below to sync",
    val totalPacketsSent: Int = 0,
    val lastPayloadJson: String = ""
)
