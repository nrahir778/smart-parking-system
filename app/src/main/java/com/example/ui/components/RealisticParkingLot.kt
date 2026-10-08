package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CarType
import com.example.data.model.ParkingLotState
import com.example.ui.theme.AsphaltDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.RoadLineYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RealisticParkingLotView(
    state: ParkingLotState,
    onSlotClicked: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Sensors",
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Live Parking Deck (3 Slots)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ultrasonic Distance Detection (HC-SR04)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Available badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (state.isFull) CrimsonRed.copy(alpha = 0.2f) else NeonEmerald.copy(alpha = 0.2f))
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Asphalt Parking Lot Container with 3 Bays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AsphaltDark)
                .border(2.dp, Color(0xFF262D3D), RoundedCornerShape(16.dp))
        ) {
            // Background Canvas: Asphalt grain, stall borders, wheel stops, drive arrows
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawAsphaltLotBackground(size.width, size.height)
            }

            // 3 Parking Bays
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Slot 1
                ParkingBayItem(
                    slotId = 1,
                    isOccupied = state.slot1,
                    carType = CarType.RED_SEDAN,
                    pinsText = "T4 / E5",
                    sonarPulse = sonarPulse,
                    onClick = { onSlotClicked(1) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Slot 2
                ParkingBayItem(
                    slotId = 2,
                    isOccupied = state.slot2,
                    carType = CarType.BLUE_SUV,
                    pinsText = "T6 / E7",
                    sonarPulse = sonarPulse,
                    onClick = { onSlotClicked(2) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Slot 3
                ParkingBayItem(
                    slotId = 3,
                    isOccupied = state.slot3,
                    carType = CarType.YELLOW_SPORTS,
                    pinsText = "T9 / E10",
                    sonarPulse = sonarPulse,
                    onClick = { onSlotClicked(3) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tip text
        Text(
            text = "Tap any slot to toggle occupancy or verify HC-05 live sensor feed.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary.copy(alpha = 0.8f),
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun ParkingBayItem(
    slotId: Int,
    isOccupied: Boolean,
    carType: CarType,
    pinsText: String,
    sonarPulse: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("slot_bay_$slotId")
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: Overhead Ultrasonic Sensor & Status LED
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkSurface.copy(alpha = 0.85f))
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slot Label & Pins
                Column {
                    Text(
                        text = "SLOT 0$slotId",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = pinsText,
                        fontSize = 8.sp,
                        color = TextSecondary
                    )
                }

                // Sensor LED Indicator with Glow
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (isOccupied) CrimsonRed else NeonEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                        // Ultrasonic scanning waves
                        Box(
                            modifier = Modifier
                                .size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                drawCircle(
                                    color = NeonEmerald.copy(alpha = (1f - sonarPulse) * 0.45f),
                                    radius = (size.width / 2f) * sonarPulse,
                                    center = center,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                    color = NeonEmerald.copy(alpha = 0.15f),
                                    radius = 18.dp.toPx(),
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

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AVAILABLE",
                                color = NeonEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = carType.styleName,
                            color = TextSecondary,
                            fontSize = 8.sp
                        )
                    }
                } else {
                    // If Occupied: Show Realistic Car Graphics
                    RealisticTopDownCar(
                        carType = carType,
                        modifier = Modifier.fillMaxSize(),
                        isParked = true
                    )
                }
            }

            // Bottom: Rubber Wheel Stop with yellow stripes
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF1E222A))
                    .border(0.5.dp, Color(0xFF333B4A), RoundedCornerShape(2.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Reflector stripes
                    val w = size.width
                    val h = size.height
                    drawRect(
                        color = RoadLineYellow,
                        topLeft = Offset(w * 0.20f, 0f),
                        size = Size(w * 0.15f, h)
                    )
                    drawRect(
                        color = RoadLineYellow,
                        topLeft = Offset(w * 0.65f, 0f),
                        size = Size(w * 0.15f, h)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Status Badge at the bottom
            Text(
                text = if (isOccupied) "OCCUPIED" else "VACANT",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOccupied) CrimsonRed else NeonEmerald
            )
        }
    }
}

private fun DrawScope.drawAsphaltLotBackground(w: Float, h: Float) {
    // Asphalt base
    drawRect(color = Color(0xFF141822), size = Size(w, h))

    // Road driving lane threshold at the bottom
    val laneTop = h - 36f
    drawLine(
        color = Color(0x33FFFFFF),
        start = Offset(0f, laneTop),
        end = Offset(w, laneTop),
        strokeWidth = 2f
    )

    // Driving direction arrow in the road
    val arrowX = w / 2f
    val arrowY = h - 18f
    drawLine(
        color = Color(0x44CBD5E1),
        start = Offset(arrowX - 25f, arrowY),
        end = Offset(arrowX + 25f, arrowY),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0x44CBD5E1),
        start = Offset(arrowX + 15f, arrowY - 6f),
        end = Offset(arrowX + 25f, arrowY),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0x44CBD5E1),
        start = Offset(arrowX + 15f, arrowY + 6f),
        end = Offset(arrowX + 25f, arrowY),
        strokeWidth = 3f
    )

    // Parking slot dividers (White thermoplastic paint)
    val slotW = w / 3f
    for (i in 1..2) {
        val divX = slotW * i
        drawLine(
            color = Color(0x88F8FAFC),
            start = Offset(divX, 10f),
            end = Offset(divX, laneTop - 8f),
            strokeWidth = 3.5f
        )
        // Corner tick marks
        drawLine(
            color = Color(0x88F8FAFC),
            start = Offset(divX - 8f, 10f),
            end = Offset(divX + 8f, 10f),
            strokeWidth = 3.5f
        )
    }

    // Outer stall boundary lines
    drawLine(
        color = Color(0x88F8FAFC),
        start = Offset(10f, 10f),
        end = Offset(10f, laneTop - 8f),
        strokeWidth = 3.5f
    )
    drawLine(
        color = Color(0x88F8FAFC),
        start = Offset(w - 10f, 10f),
        end = Offset(w - 10f, laneTop - 8f),
        strokeWidth = 3.5f
    )
}
