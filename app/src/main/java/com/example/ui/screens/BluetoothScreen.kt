package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.ui.ParkingViewModel
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun BluetoothScreen(
    viewModel: ParkingViewModel,
    modifier: Modifier = Modifier
) {
    val connStatus by viewModel.connectionStatus.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val state by viewModel.parkingState.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Connection Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.dp,
                    when (connStatus) {
                        is ConnectionStatus.Connected -> NeonEmerald.copy(alpha = 0.6f)
                        is ConnectionStatus.Connecting -> ElectricCyan.copy(alpha = 0.6f)
                        is ConnectionStatus.Error -> CrimsonRed.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.outline
                    },
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    when (connStatus) {
                                        is ConnectionStatus.Connected -> NeonEmerald.copy(alpha = 0.18f)
                                        is ConnectionStatus.Connecting -> ElectricCyan.copy(alpha = 0.18f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (connStatus) {
                                    is ConnectionStatus.Connected -> Icons.Default.BluetoothConnected
                                    is ConnectionStatus.Connecting -> Icons.Default.Bluetooth
                                    else -> Icons.Default.BluetoothDisabled
                                },
                                contentDescription = "BT",
                                tint = when (connStatus) {
                                    is ConnectionStatus.Connected -> NeonEmerald
                                    is ConnectionStatus.Connecting -> ElectricCyan
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "HC-05 Bluetooth Serial (SPP)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (connStatus) {
                                    is ConnectionStatus.Connected -> "Connected to ${(connStatus as ConnectionStatus.Connected).deviceName}"
                                    is ConnectionStatus.Connecting -> "Connecting to ${(connStatus as ConnectionStatus.Connecting).deviceName}..."
                                    is ConnectionStatus.Error -> (connStatus as ConnectionStatus.Error).message
                                    else -> "Disconnected — Select your paired HC-05 below"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = when (connStatus) {
                                    is ConnectionStatus.Connected -> NeonEmerald
                                    is ConnectionStatus.Connecting -> ElectricCyan
                                    is ConnectionStatus.Error -> CrimsonRed
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    if (connStatus is ConnectionStatus.Connecting) {
                        CircularProgressIndicator(
                            color = ElectricCyan,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                if (connStatus is ConnectionStatus.Connected) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.disconnectBluetooth() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonRed.copy(alpha = 0.15f),
                            contentColor = CrimsonRed
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("bt_disconnect_button")
                    ) {
                        Text("Disconnect HC-05", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Paired Devices List Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Paired Devices",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { viewModel.refreshPairedDevices() },
                        modifier = Modifier.size(32.dp).testTag("bt_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = ElectricCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (pairedDevices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "No paired Bluetooth devices detected",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Power your Arduino Uno and HC-05 module (5V)\n2. Open Android Settings > Bluetooth > Pair New Device\n3. Tap 'HC-05' (Default Pairing PIN: 1234 or 0000)\n4. Return here and tap Refresh to connect!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    pairedDevices.forEach { device ->
                        val isHc05 = device.name.contains("HC-05", ignoreCase = true) ||
                                device.name.contains("HC-06", ignoreCase = true) ||
                                device.name.contains("Arduino", ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isHc05) ElectricCyan.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isHc05) ElectricCyan.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = device.name,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp
                                        )
                                        if (isHc05) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(ElectricCyan)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "HC-05 TARGET",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = device.address,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { viewModel.connectToDevice(device.address, device.name) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isHc05) ElectricCyan else MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = if (isHc05) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("connect_device_${device.address}")
                                ) {
                                    Text(
                                        text = "Connect",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Real Serial Telemetry Console
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Console",
                        tint = NeonEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live HC-05 Serial Feed (9600 Baud)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Terminal Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070A0F))
                        .border(1.dp, Color(0xFF161F2E), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    val logs = state.rawTelemetryLog
                    if (logs.isEmpty()) {
                        Text(
                            text = "> Standby: Waiting for Arduino serial lines...\n> Format: S1:OCCUPIED, S2:EMPTY, S3:EMPTY, TOTAL:1, GATE:OPEN",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    } else {
                        val displayLogs = logs.takeLast(6).joinToString("\n") { "> $it" }
                        Text(
                            text = displayLogs,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = NeonEmerald
                        )
                    }
                }
            }
        }

        // Arduino Circuit Wiring Schematic Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeveloperBoard,
                        contentDescription = "Board",
                        tint = CyberAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arduino Uno Hardware Pinout",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val pinInfo = listOf(
                    "HC-05 TXD" to "Arduino Pin D2 (SoftwareSerial RX)",
                    "HC-05 RXD" to "Arduino Pin D3 (via 2.2k/3.3k divider)",
                    "Slot 1 HC-SR04" to "TRIG: Pin 4 • ECHO: Pin 5",
                    "Slot 2 HC-SR04" to "TRIG: Pin 6 • ECHO: Pin 7",
                    "Slot 3 HC-SR04" to "TRIG: Pin 9 • ECHO: Pin 10",
                    "Active Buzzer (+)" to "Arduino Pin 8 (Active HIGH)",
                    "MG995 Gate Servo" to "Arduino Pin 11 (0° OPEN / 90° CLOSED)"
                )

                pinInfo.forEach { (component, pin) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = component,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = pin,
                            fontSize = 12.sp,
                            color = ElectricCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
