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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.CarType
import com.example.data.model.ConnectionStatus
import com.example.data.model.GateState
import com.example.ui.ParkingViewModel
import com.example.ui.components.GateBoomBarrierCard
import com.example.ui.components.RealisticParkingLotView
import com.example.ui.theme.CarBlue
import com.example.ui.theme.CarRed
import com.example.ui.theme.CarYellow
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

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
    val isSimulation by viewModel.isSimulationActive.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Status Bar: Bluetooth & Gateway Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bluetooth Connection Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceElevated)
                    .border(
                        1.dp,
                        if (connStatus is ConnectionStatus.Connected) NeonEmerald.copy(alpha = 0.5f) else DarkSurfaceBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onNavigateToBluetooth() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = "Bluetooth Status",
                    tint = when (connStatus) {
                        is ConnectionStatus.Connected -> NeonEmerald
                        is ConnectionStatus.Connecting -> ElectricCyan
                        else -> TextSecondary
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (connStatus) {
                        is ConnectionStatus.Connected -> "HC-05 Connected"
                        is ConnectionStatus.Connecting -> "Connecting..."
                        is ConnectionStatus.Error -> "BT Notice"
                        else -> "BT Disconnected"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when (connStatus) {
                        is ConnectionStatus.Connected -> NeonEmerald
                        is ConnectionStatus.Connecting -> ElectricCyan
                        else -> TextSecondary
                    }
                )
            }

            // Cloud Gateway Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceElevated)
                    .border(
                        1.dp,
                        if (gatewayConfig.enabled) ElectricCyan.copy(alpha = 0.5f) else DarkSurfaceBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onNavigateToGateway() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = "Gateway Status",
                    tint = if (gatewayConfig.enabled) ElectricCyan else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (gatewayConfig.enabled) "Gateway Active" else "Gateway Paused",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (gatewayConfig.enabled) ElectricCyan else TextSecondary
                )
            }
        }

        // Capacity KPI Summary Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Free Slots Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "AVAILABLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${state.availableCount}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (state.availableCount > 0) NeonEmerald else CrimsonRed
                        )
                        Text(
                            text = " / 3 free",
                            fontSize = 12.sp,
                            color = TextSecondary,
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
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "OCCUPIED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${state.totalOccupied}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                        Text(
                            text = " / 3 bays",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                }
            }
        }

        // Realistic Top-Down Parking Deck
        RealisticParkingLotView(
            state = state,
            onSlotClicked = { slotId -> viewModel.toggleSlotManually(slotId) }
        )

        // MG995 Gate Servo & Buzzer Actuator Card
        GateBoomBarrierCard(
            gateState = state.gateState,
            buzzerAlert = state.buzzerAlert
        )

        // Hardware Simulator / Auto Traffic Demo Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSimulation) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Simulation",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Auto Traffic Simulation",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSimulation) "Simulating arriving & leaving cars..." else "Cycle through parking scenarios",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = isSimulation,
                    onCheckedChange = { viewModel.toggleAutoTrafficSimulation(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ElectricCyan
                    ),
                    modifier = Modifier.testTag("simulation_toggle")
                )
            }
        }

        // Slot Detail Cards with Individual Override Buttons
        Text(
            text = "Slot Details & Sensor Pinout",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

        SlotDetailRow(
            slotId = 1,
            isOccupied = state.slot1,
            carName = "Crimson Red Sport Sedan",
            carColor = CarRed,
            pins = "TRIG Pin 4 • ECHO Pin 5",
            onToggle = { viewModel.toggleSlotManually(1) }
        )

        SlotDetailRow(
            slotId = 2,
            isOccupied = state.slot2,
            carName = "Midnight Blue Electric SUV",
            carColor = CarBlue,
            pins = "TRIG Pin 6 • ECHO Pin 7",
            onToggle = { viewModel.toggleSlotManually(2) }
        )

        SlotDetailRow(
            slotId = 3,
            isOccupied = state.slot3,
            carName = "Cyber Amber GT Performance",
            carColor = CarYellow,
            pins = "TRIG Pin 9 • ECHO Pin 10",
            onToggle = { viewModel.toggleSlotManually(3) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SlotDetailRow(
    slotId: Int,
    isOccupied: Boolean,
    carName: String,
    carColor: Color,
    pins: String,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(
                1.dp,
                if (isOccupied) CrimsonRed.copy(alpha = 0.3f) else NeonEmerald.copy(alpha = 0.3f),
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
                // Color Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isOccupied) carColor.copy(alpha = 0.25f) else DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Car",
                        tint = if (isOccupied) carColor else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Slot 0$slotId",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOccupied) "• OCCUPIED" else "• EMPTY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOccupied) CrimsonRed else NeonEmerald
                        )
                    }
                    Text(
                        text = if (isOccupied) carName else "Vacant Bay",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = pins,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            // Quick Toggle Button
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOccupied) CrimsonRed.copy(alpha = 0.15f) else NeonEmerald.copy(alpha = 0.15f),
                    contentColor = if (isOccupied) CrimsonRed else NeonEmerald
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("toggle_slot_$slotId")
            ) {
                Text(
                    text = if (isOccupied) "Clear" else "Park",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
