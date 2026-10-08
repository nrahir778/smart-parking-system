package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.ui.ParkingViewModel
import com.example.ui.components.GateBoomBarrierCard
import com.example.ui.components.RealisticParkingLotView
import com.example.ui.theme.CarAmberMetallic
import com.example.ui.theme.CarBlueMetallic
import com.example.ui.theme.CarRedMetallic
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun HomeScreen(
    viewModel: ParkingViewModel,
    onNavigateToBluetooth: () -> Unit,
    onNavigateToGateway: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.parkingState.collectAsState()
    val connStatus by viewModel.connectionStatus.collectAsState()
    val gatewayConfig by viewModel.gatewayConfig.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Status Bar: Bluetooth & Gateway Quick Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bluetooth Connection Status Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        if (connStatus is ConnectionStatus.Connected) NeonEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onNavigateToBluetooth() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("bluetooth_status_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (connStatus is ConnectionStatus.Connected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                    contentDescription = "Bluetooth Status",
                    tint = when (connStatus) {
                        is ConnectionStatus.Connected -> NeonEmerald
                        is ConnectionStatus.Connecting -> ElectricCyan
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (connStatus) {
                        is ConnectionStatus.Connected -> "HC-05 Connected"
                        is ConnectionStatus.Connecting -> "Connecting..."
                        is ConnectionStatus.Error -> "BT Disconnected"
                        else -> "Connect HC-05"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when (connStatus) {
                        is ConnectionStatus.Connected -> NeonEmerald
                        is ConnectionStatus.Connecting -> ElectricCyan
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Cloud Gateway Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        if (gatewayConfig.enabled) ElectricCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onNavigateToGateway() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("gateway_status_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = "Gateway Status",
                    tint = if (gatewayConfig.enabled) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (gatewayConfig.enabled) "Gateway Ready" else "Gateway Off",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (gatewayConfig.enabled) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Connection Notice if not receiving data yet
        if (!state.hasReceivedData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = "Connect",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Real Hardware Mode Active",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pair with your HC-05 module to view real Arduino readings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onNavigateToBluetooth,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("connect_now_button")
                    ) {
                        Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Real-Time Capacity KPI Cards (Only shows data when received)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Free Slots Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "AVAILABLE BAYS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (state.hasReceivedData) "${state.availableCount}" else "--",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (state.hasReceivedData && state.availableCount > 0) NeonEmerald else CrimsonRed
                        )
                        Text(
                            text = " / 3 free",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                }
            }

            // Occupied Slots Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "OCCUPIED BAYS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (state.hasReceivedData) "${state.totalOccupied}" else "--",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                        Text(
                            text = " / 3 slots",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                }
            }
        }

        // Realistic Top-Down Traffic Park View (Road, Greenery & Photorealistic Cars)
        RealisticParkingLotView(
            state = state
        )

        // MG995 Barrier Gate Servo & Buzzer Alert Actuator Card
        GateBoomBarrierCard(
            gateState = state.gateState,
            buzzerAlert = state.buzzerAlert
        )

        // Live Sensor Slot Readings List
        Text(
            text = "Ultrasonic Sensor Telemetry",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )

        LiveSlotDetailRow(
            slotId = 1,
            hasData = state.hasReceivedData,
            isOccupied = state.slot1,
            carName = "Crimson Metallic Sport Sedan",
            carColor = CarRedMetallic,
            pins = "HC-SR04 • TRIG Pin 4 • ECHO Pin 5"
        )

        LiveSlotDetailRow(
            slotId = 2,
            hasData = state.hasReceivedData,
            isOccupied = state.slot2,
            carName = "Midnight Sapphire Luxury SUV",
            carColor = CarBlueMetallic,
            pins = "HC-SR04 • TRIG Pin 6 • ECHO Pin 7"
        )

        LiveSlotDetailRow(
            slotId = 3,
            hasData = state.hasReceivedData,
            isOccupied = state.slot3,
            carName = "Cyber Amber GT Performance",
            carColor = CarAmberMetallic,
            pins = "HC-SR04 • TRIG Pin 9 • ECHO Pin 10"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LiveSlotDetailRow(
    slotId: Int,
    hasData: Boolean,
    isOccupied: Boolean,
    carName: String,
    carColor: Color,
    pins: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (!hasData) MaterialTheme.colorScheme.outline
                else if (isOccupied) CrimsonRed.copy(alpha = 0.4f) else NeonEmerald.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Vehicle Icon Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (hasData && isOccupied) carColor.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surface
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Car",
                        tint = if (hasData && isOccupied) carColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Slot 0$slotId",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!hasData) "• AWAITING DATA"
                            else if (isOccupied) "• OCCUPIED" else "• AVAILABLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!hasData) MaterialTheme.colorScheme.onSurfaceVariant
                            else if (isOccupied) CrimsonRed else NeonEmerald
                        )
                    }
                    Text(
                        text = if (hasData && isOccupied) carName else if (hasData) "Vacant Parking Stall" else "Waiting for Arduino transmission",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = pins,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan.copy(alpha = 0.85f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
