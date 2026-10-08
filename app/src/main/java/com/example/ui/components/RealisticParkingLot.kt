package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CarType
import com.example.data.model.ParkingLotState
import com.example.ui.theme.BushGreenDark
import com.example.ui.theme.BushGreenLight
import com.example.ui.theme.ConcreteCurb
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GrassDark
import com.example.ui.theme.GrassLight
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RoadLineYellow

@Composable
fun RealisticParkingLotView(
    state: ParkingLotState,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val infiniteTransition = rememberInfiniteTransition(label = "lot_sensor_sonar")
    val sonarPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonarPulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        // Section Header with Environmental Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(NeonEmerald.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Park,
                        contentDescription = "Traffic Park",
                        tint = NeonEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Smart Traffic Parking Deck",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (state.hasReceivedData) "Live Telemetry from Arduino HC-05" else "Awaiting Bluetooth Connection",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.hasReceivedData) NeonEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Real-Time Capacity Badge
            if (state.hasReceivedData) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (state.isFull) CrimsonRed.copy(alpha = 0.18f) else NeonEmerald.copy(alpha = 0.18f))
                        .border(
                            1.dp,
                            if (state.isFull) CrimsonRed else NeonEmerald,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (state.isFull) "FULL (0/3)" else "${state.availableCount}/3 FREE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isFull) CrimsonRed else NeonEmerald
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "STANDBY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Realistic Parking Area Container (Surrounding Greenery + Road + 3 Bays)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, if (isDark) Color(0xFF232D3F) else Color(0xFFCBD5E1), RoundedCornerShape(18.dp))
        ) {
            // Background Canvas: Lush Lawns, Hedges, Concrete Curbs, Road, Crosswalk
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRealisticParkEnvironment(size.width, size.height, isDark)
            }

            // 3 Parking Bays
            if (state.hasReceivedData) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 28.dp, end = 28.dp, top = 42.dp, bottom = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Slot 1
                    ParkingBayItem(
                        slotId = 1,
                        isOccupied = state.slot1,
                        carType = CarType.RED_SEDAN,
                        pinsText = "TRIG 4 / ECHO 5",
                        sonarPulse = sonarPulse,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Slot 2
                    ParkingBayItem(
                        slotId = 2,
                        isOccupied = state.slot2,
                        carType = CarType.BLUE_SUV,
                        pinsText = "TRIG 6 / ECHO 7",
                        sonarPulse = sonarPulse,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Slot 3
                    ParkingBayItem(
                        slotId = 3,
                        isOccupied = state.slot3,
                        carType = CarType.YELLOW_SPORTS,
                        pinsText = "TRIG 9 / ECHO 10",
                        sonarPulse = sonarPulse,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // When not receiving data yet: Clean standby overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x66000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.BluetoothSearching,
                                contentDescription = "Connecting",
                                tint = ElectricCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Waiting for Arduino Telemetry",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Connect to HC-05 via Bluetooth tab to view live slots",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParkingBayItem(
    slotId: Int,
    isOccupied: Boolean,
    carType: CarType,
    pinsText: String,
    sonarPulse: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .testTag("slot_bay_$slotId")
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: Ultrasonic HC-SR04 Sensor Arch with dual transducers & LED
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xE6131B28))
                    .padding(horizontal = 5.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slot Label
                Column {
                    Text(
                        text = "SLOT 0$slotId",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                    Text(
                        text = pinsText,
                        fontSize = 7.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Dual Ultrasonic Transducers (Transmitter & Receiver)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF475569)))
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF475569)))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    // Sensor Status LED
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isOccupied) CrimsonRed else NeonEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Main Bay Parking Stall Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // If Empty: Show Glowing Green Sonar & Stencil
                if (!isOccupied) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                drawCircle(
                                    color = NeonEmerald.copy(alpha = (1f - sonarPulse) * 0.40f),
                                    radius = (size.width / 2f) * sonarPulse,
                                    center = center,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                    color = NeonEmerald.copy(alpha = 0.12f),
                                    radius = 16.dp.toPx(),
                                    center = center
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Available",
                                tint = NeonEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonEmerald.copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VACANT",
                                color = NeonEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // If Occupied: Show Photorealistic Car Graphics
                    RealisticTopDownCar(
                        carType = carType,
                        modifier = Modifier.fillMaxSize(),
                        isParked = true
                    )
                }
            }

            // Rubber Wheel Stop with yellow safety reflectors
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF1E222A))
                    .border(0.5.dp, Color(0xFF333B4A), RoundedCornerShape(2.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRect(color = RoadLineYellow, topLeft = Offset(w * 0.18f, 0f), size = Size(w * 0.16f, h))
                    drawRect(color = RoadLineYellow, topLeft = Offset(w * 0.66f, 0f), size = Size(w * 0.16f, h))
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Bottom Status Text
            Text(
                text = if (isOccupied) "OCCUPIED" else "FREE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOccupied) CrimsonRed else NeonEmerald
            )
        }
    }
}

// -------------------------------------------------------------
// REALISTIC TRAFFIC ENVIRONMENT: LAWNS, HEDGES, ROADS, CURBS
// -------------------------------------------------------------
private fun DrawScope.drawRealisticParkEnvironment(w: Float, h: Float, isDark: Boolean) {
    // 1. Asphalt Base Surface for whole deck
    val asphaltColor = if (isDark) Color(0xFF12161F) else Color(0xFF262C38)
    drawRect(color = asphaltColor, size = Size(w, h))

    // 2. Surrounding Lush Greenery (Top Landscape Verge)
    val grassTopH = 34f
    val grassBrush = Brush.verticalGradient(
        colors = if (isDark) listOf(Color(0xFF0F3B22), Color(0xFF14532D))
        else listOf(Color(0xFF16A34A), Color(0xFF22C55E)),
        startY = 0f,
        endY = grassTopH
    )
    drawRect(brush = grassBrush, topLeft = Offset(0f, 0f), size = Size(w, grassTopH))

    // Concrete Curb separating grass from parking
    val curbColor = if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0)
    drawRect(color = curbColor, topLeft = Offset(0f, grassTopH), size = Size(w, 4f))

    // Landscaped Round Hedge Shrubs on the top grass verge
    val shrubColor = if (isDark) Color(0xFF064E26) else Color(0xFF15803D)
    val shrubHighlight = if (isDark) Color(0xFF166534) else Color(0xFF4ADE80)
    val shrubRadius = 11f
    listOf(
        Offset(w * 0.08f, grassTopH * 0.48f),
        Offset(w * 0.22f, grassTopH * 0.48f),
        Offset(w * 0.50f, grassTopH * 0.48f),
        Offset(w * 0.78f, grassTopH * 0.48f),
        Offset(w * 0.92f, grassTopH * 0.48f)
    ).forEach { center ->
        // Shrub shadow
        drawCircle(color = Color(0x44000000), radius = shrubRadius + 2f, center = Offset(center.x + 2f, center.y + 2f))
        // Shrub body
        drawCircle(color = shrubColor, radius = shrubRadius, center = center)
        // Shrub highlight crown
        drawCircle(color = shrubHighlight, radius = shrubRadius * 0.5f, center = Offset(center.x - 2f, center.y - 2f))
    }

    // 3. Flanking Side Greenery Verges (Left & Right borders)
    val sideVergeW = 22f
    val mainLotTop = grassTopH + 4f
    val mainLotBottom = h - 44f
    val sideVergeH = mainLotBottom - mainLotTop

    // Left Grass Verge & Curb
    drawRect(brush = grassBrush, topLeft = Offset(0f, mainLotTop), size = Size(sideVergeW, sideVergeH))
    drawRect(color = curbColor, topLeft = Offset(sideVergeW, mainLotTop), size = Size(3f, sideVergeH))

    // Right Grass Verge & Curb
    drawRect(brush = grassBrush, topLeft = Offset(w - sideVergeW, mainLotTop), size = Size(sideVergeW, sideVergeH))
    drawRect(color = curbColor, topLeft = Offset(w - sideVergeW - 3f, mainLotTop), size = Size(3f, sideVergeH))

    // 4. Approach Road at the Bottom with Driving Lane & Crosswalk
    val roadTop = h - 42f
    val roadH = 42f

    // Road Curb Separator
    drawRect(color = curbColor, topLeft = Offset(0f, roadTop), size = Size(w, 3f))

    // Road Yellow Center Dash Line
    val dashW = 26f
    val gapW = 18f
    var currX = 0f
    val centerY = roadTop + roadH * 0.52f
    while (currX < w) {
        drawLine(
            color = RoadLineYellow,
            start = Offset(currX, centerY),
            end = Offset(currX + dashW, centerY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        currX += (dashW + gapW)
    }

    // Pedestrian Zebra Crosswalk at Entrance (Left section)
    val cwX = sideVergeW + 12f
    for (i in 0..4) {
        drawRect(
            color = Color(0xCCF8FAFC),
            topLeft = Offset(cwX + i * 14f, roadTop + 5f),
            size = Size(8f, roadH - 10f)
        )
    }

    // Directional Traffic Arrow ("ENTRY" driving lane arrow)
    val arrowX = w * 0.72f
    val arrowY = roadTop + roadH * 0.28f
    drawLine(
        color = Color(0xCCF8FAFC),
        start = Offset(arrowX - 22f, arrowY),
        end = Offset(arrowX + 22f, arrowY),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0xCCF8FAFC),
        start = Offset(arrowX + 12f, arrowY - 5f),
        end = Offset(arrowX + 22f, arrowY),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0xCCF8FAFC),
        start = Offset(arrowX + 12f, arrowY + 5f),
        end = Offset(arrowX + 22f, arrowY),
        strokeWidth = 3f
    )

    // 5. Parking Stall Divider Lines & Oil/Tire Wear Marks
    val deckLeft = sideVergeW + 3f
    val deckRight = w - sideVergeW - 3f
    val deckW = deckRight - deckLeft
    val slotW = deckW / 3f

    for (i in 1..2) {
        val divX = deckLeft + slotW * i
        // Double-stroke white stall divider
        drawLine(
            color = Color(0xDDF8FAFC),
            start = Offset(divX - 1.5f, mainLotTop + 8f),
            end = Offset(divX - 1.5f, roadTop - 6f),
            strokeWidth = 2.5f
        )
        drawLine(
            color = Color(0xDDF8FAFC),
            start = Offset(divX + 1.5f, mainLotTop + 8f),
            end = Offset(divX + 1.5f, roadTop - 6f),
            strokeWidth = 2.5f
        )
        // Corner T-brackets
        drawLine(
            color = Color(0xDDF8FAFC),
            start = Offset(divX - 8f, mainLotTop + 8f),
            end = Offset(divX + 8f, mainLotTop + 8f),
            strokeWidth = 2.5f
        )
    }

    // Outer stall boundary lines
    drawLine(
        color = Color(0xDDF8FAFC),
        start = Offset(deckLeft + 4f, mainLotTop + 8f),
        end = Offset(deckLeft + 4f, roadTop - 6f),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0xDDF8FAFC),
        start = Offset(deckRight - 4f, mainLotTop + 8f),
        end = Offset(deckRight - 4f, roadTop - 6f),
        strokeWidth = 3f
    )

    // Realistic Tire/Oil Wear Stains in each stall center for authentic depth
    val stainColor = if (isDark) Color(0x44080B10) else Color(0x220F172A)
    for (i in 0..2) {
        val bayCenterX = deckLeft + slotW * (i + 0.5f)
        val bayCenterY = (mainLotTop + roadTop) * 0.52f
        drawCircle(color = stainColor, radius = 22f, center = Offset(bayCenterX, bayCenterY))
        drawCircle(color = stainColor, radius = 12f, center = Offset(bayCenterX, bayCenterY + 6f))
    }
}
