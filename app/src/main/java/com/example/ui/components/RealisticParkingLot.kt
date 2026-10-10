package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Park
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.util.ParkingHaptics
import com.example.data.model.CarType
import com.example.data.model.GateState
import com.example.data.model.ParkingLotState
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DividerWhite
import com.example.ui.theme.DividerYellow
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GrassLawnDark
import com.example.ui.theme.GrassLawnLight
import com.example.ui.theme.LimeGreenPolice
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.OrangeBarrierArm
import com.example.ui.theme.PushPinBlue
import com.example.ui.theme.PushPinYellow
import com.example.ui.theme.RoadLineYellow
import com.example.ui.theme.SensorStandBlue
import com.example.ui.theme.SignBlue
import com.example.ui.theme.StopSignRed
import com.example.ui.theme.TreeFoliageGreen
import com.example.ui.theme.TreeTrunkBrown

@Composable
fun RealisticParkingLotView(
    state: ParkingLotState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = remember(context) { ParkingHaptics(context) }
    var hasLotInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(state.isFull) {
        if (!hasLotInitialized) {
            hasLotInitialized = true
            return@LaunchedEffect
        }
        if (state.isFull) {
            haptics.playLotFullAlert()
        }
    }
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

    // Animated Barrier Arm Rotation: 0° (OPEN - swings 90° clear) vs 90° (CLOSED - horizontal)
    val barrierAngle by animateFloatAsState(
        targetValue = if (state.gateState == GateState.CLOSED || state.isFull) 0f else -75f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "barrierAngle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
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
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GrassLawnLight.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Park,
                        contentDescription = "Traffic Park",
                        tint = GrassLawnDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Smart Traffic Parking Deck",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Shree Sarkari Madhyamik Shala Lakhapar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Real-Time Capacity Badge
            if (state.hasReceivedData) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (state.isFull) CrimsonRed.copy(alpha = 0.15f) else NeonEmerald.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (state.isFull) CrimsonRed else NeonEmerald,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (state.isFull) "FULL (3/3)" else "${state.availableCount}/3 VACANT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isFull) CrimsonRed else NeonEmerald
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AWAITING GATEWAY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Physical Model Layout Canvas + Interactive Bays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(490.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, Color(0xFF94A3B8), RoundedCornerShape(18.dp))
        ) {
            // Background Environment Canvas:
            // Top: Dark Asphalt Parking Deck with Island Dividers, Pushpins, Sensor Stands, Road Markings
            // Middle: MG995 Servo Box & Orange Boom Barrier with Red STOP Sign
            // Bottom: Asphalt Road with Crosswalk, Centerline, flanked by Lush Green Grass & Trees
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                drawPhysicalProjectModel(
                    w = w,
                    h = h,
                    barrierAngle = barrierAngle,
                    isClosed = state.gateState == GateState.CLOSED || state.isFull
                )
            }

            // 3 Interactive Top-Down Bays Positioned exactly over the top parking deck area
            if (state.hasReceivedData) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Slot 1: Lime Green Sedan
                    ParkingBayOverlayItem(
                        slotId = 1,
                        isOccupied = state.slot1,
                        carType = CarType.GREEN_POLICE,
                        sonarPulse = sonarPulse,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    // Slot 2: Lime Green Sports Coupe
                    ParkingBayOverlayItem(
                        slotId = 2,
                        isOccupied = state.slot2,
                        carType = CarType.GREEN_SPORTS,
                        sonarPulse = sonarPulse,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    // Slot 3: Vintage Red Classic Coupe
                    ParkingBayOverlayItem(
                        slotId = 3,
                        isOccupied = state.slot3,
                        carType = CarType.RED_VINTAGE,
                        sonarPulse = sonarPulse,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Standby overlay when waiting for Bluetooth telemetry
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xF0FFFFFF))
                            .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.BluetoothSearching,
                                contentDescription = "Connecting",
                                tint = ElectricCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Waiting for Arduino Telemetry",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Connect to HC-05 • Live hardware data only",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// CANVAS: PHYSICAL PROJECT MODEL (Matching user's photos with Road, Grass, Trees, Barrier, Sensors)
// -------------------------------------------------------------------------------------------------
private fun DrawScope.drawPhysicalProjectModel(
    w: Float,
    h: Float,
    barrierAngle: Float,
    isClosed: Boolean
) {
    // Top Deck Height (Parking area): ~52% of total height
    val deckH = h * 0.52f
    val roadwayTop = deckH
    val roadwayH = h - deckH

    // ==========================================
    // 1. TOP PARKING DECK (Black Asphalt)
    // ==========================================
    drawRect(
        color = Color(0xFF141923), // Dark asphalt
        topLeft = Offset(0f, 0f),
        size = Size(w, deckH)
    )

    // White outer border wall of cardboard box
    drawRect(
        color = Color(0xFF94A3B8),
        topLeft = Offset(0f, 0f),
        size = Size(w, deckH),
        style = Stroke(width = 4f)
    )

    // Draw the 3 Bays and Island Dividers
    val bayW = (w - 32f) / 3f
    val paddingX = 16f

    for (i in 0..2) {
        val bayLeft = paddingX + i * bayW
        val bayRight = bayLeft + bayW

        // Blue HC-SR04 Stand at top with ribbon wires
        val standW = 44f
        val standH = 26f
        val standX = bayLeft + (bayW - standW) / 2f
        val standY = 4f

        // Blue stand base
        drawRoundRect(
            color = SensorStandBlue,
            topLeft = Offset(standX, standY),
            size = Size(standW, standH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Ultrasonic dual transducer silver cylinders
        drawCircle(color = Color(0xFFCBD5E1), radius = 8f, center = Offset(standX + 11f, standY + 13f))
        drawCircle(color = Color(0xFF475569), radius = 4f, center = Offset(standX + 11f, standY + 13f))
        drawCircle(color = Color(0xFFCBD5E1), radius = 8f, center = Offset(standX + 33f, standY + 13f))
        drawCircle(color = Color(0xFF475569), radius = 4f, center = Offset(standX + 33f, standY + 13f))

        // Rainbow ribbon wires going up (Red, Yellow, Green, Blue)
        listOf(
            Color(0xFFEF4444) to 12f,
            Color(0xFFFACC15) to 18f,
            Color(0xFF22C55E) to 26f,
            Color(0xFF3B82F6) to 32f
        ).forEach { (wireColor, xOffset) ->
            drawLine(
                color = wireColor,
                start = Offset(standX + xOffset, standY),
                end = Offset(standX + xOffset, 0f),
                strokeWidth = 2.5f
            )
        }

        // Dividers between slots (Pill-shaped white islands with yellow border and blue/yellow pushpins)
        if (i < 2) {
            val divX = bayRight - 5f
            val divY = 38f
            val divW = 10f
            val divH = deckH * 0.62f

            // White island divider
            drawRoundRect(
                color = DividerWhite,
                topLeft = Offset(divX, divY),
                size = Size(divW, divH),
                cornerRadius = CornerRadius(5f, 5f)
            )
            // Yellow border lines on divider
            drawRoundRect(
                color = DividerYellow,
                topLeft = Offset(divX - 1f, divY - 1f),
                size = Size(divW + 2f, divH + 2f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.5f)
            )

            // Push pins at ends: Blue pushpin at top, Yellow pushpin at bottom (matching photo!)
            drawCircle(color = PushPinBlue, radius = 5.5f, center = Offset(divX + divW / 2f, divY + 8f))
            drawCircle(color = Color(0xFF93C5FD), radius = 2f, center = Offset(divX + divW / 2f, divY + 8f))

            drawCircle(color = PushPinYellow, radius = 5.5f, center = Offset(divX + divW / 2f, divY + divH - 8f))
            drawCircle(color = Color(0xFFFEF08A), radius = 2f, center = Offset(divX + divW / 2f, divY + divH - 8f))
        }
    }

    // Aisle Arrow Decal: 3-way turn arrow (⮤ ⬆ ⮥)
    val arrowY = deckH * 0.88f
    drawLine(
        color = Color(0xE6FFFFFF),
        start = Offset(w * 0.5f, arrowY),
        end = Offset(w * 0.5f, arrowY - 18f),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0xE6FFFFFF),
        start = Offset(w * 0.5f - 8f, arrowY - 10f),
        end = Offset(w * 0.5f, arrowY - 18f),
        strokeWidth = 3f
    )
    drawLine(
        color = Color(0xE6FFFFFF),
        start = Offset(w * 0.5f + 8f, arrowY - 10f),
        end = Offset(w * 0.5f, arrowY - 18f),
        strokeWidth = 3f
    )

    // Left Signboard: Blue "P PARKING ZONE" (Exact sign from user photo 4)
    val signPLeft = 8f
    val signPTop = deckH * 0.82f
    drawRoundRect(
        color = SignBlue,
        topLeft = Offset(signPLeft, signPTop),
        size = Size(36f, 28f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(signPLeft + 1f, signPTop + 1f),
        size = Size(34f, 26f),
        cornerRadius = CornerRadius(2f, 2f),
        style = Stroke(width = 1f)
    )
    // Letter "P"
    drawLine(
        color = Color.White,
        start = Offset(signPLeft + 10f, signPTop + 6f),
        end = Offset(signPLeft + 10f, signPTop + 20f),
        strokeWidth = 3f
    )
    drawArc(
        color = Color.White,
        startAngle = -90f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(signPLeft + 7f, signPTop + 6f),
        size = Size(14f, 8f),
        style = Stroke(width = 2.5f)
    )

    // =========================================================================
    // 2. LOWER SECTION: CENTRAL ROADWAY FLANKED BY LUSH GREEN GRASS & TREES
    // =========================================================================
    val roadW = w * 0.38f
    val roadLeft = (w - roadW) / 2f
    val roadRight = roadLeft + roadW

    // A) LEFT GRASS LAWN WITH MINIATURE TREES
    val grassBrushLeft = Brush.verticalGradient(
        colors = listOf(GrassLawnLight, GrassLawnDark),
        startY = roadwayTop,
        endY = h
    )
    drawRect(
        brush = grassBrushLeft,
        topLeft = Offset(0f, roadwayTop),
        size = Size(roadLeft, roadwayH)
    )

    // B) RIGHT GRASS LAWN WITH MINIATURE TREES & "WAITING AREA" SIGN
    val grassBrushRight = Brush.verticalGradient(
        colors = listOf(GrassLawnLight, GrassLawnDark),
        startY = roadwayTop,
        endY = h
    )
    drawRect(
        brush = grassBrushRight,
        topLeft = Offset(roadRight, roadwayTop),
        size = Size(w - roadRight, roadwayH)
    )

    // Plant miniature trees / topiary yarn shrubs on left grass lawn
    listOf(
        Offset(roadLeft * 0.30f, roadwayTop + roadwayH * 0.20f),
        Offset(roadLeft * 0.70f, roadwayTop + roadwayH * 0.45f),
        Offset(roadLeft * 0.35f, roadwayTop + roadwayH * 0.75f)
    ).forEach { pos ->
        // Tree trunk shadow
        drawCircle(color = Color(0x33000000), radius = 13f, center = Offset(pos.x + 2f, pos.y + 4f))
        // Shrub base dark green
        drawCircle(color = TreeFoliageGreen, radius = 12f, center = pos)
        // Fluffy pom-pom bright foliage highlight
        drawCircle(color = Color(0xFF4ADE80), radius = 7f, center = Offset(pos.x - 3f, pos.y - 3f))
    }

    // Plant miniature trees on right grass lawn
    listOf(
        Offset(roadRight + (w - roadRight) * 0.35f, roadwayTop + roadwayH * 0.22f),
        Offset(roadRight + (w - roadRight) * 0.70f, roadwayTop + roadwayH * 0.50f),
        Offset(roadRight + (w - roadRight) * 0.40f, roadwayTop + roadwayH * 0.80f)
    ).forEach { pos ->
        drawCircle(color = Color(0x33000000), radius = 13f, center = Offset(pos.x + 2f, pos.y + 4f))
        drawCircle(color = TreeFoliageGreen, radius = 12f, center = pos)
        drawCircle(color = Color(0xFF4ADE80), radius = 7f, center = Offset(pos.x - 3f, pos.y - 3f))
    }

    // "WAITING AREA" Green Signpost on right grass lawn (From user photo 4)
    val waitSignX = roadRight + (w - roadRight) * 0.30f
    val waitSignY = roadwayTop + roadwayH * 0.60f
    drawRoundRect(
        color = Color(0xFF15803D),
        topLeft = Offset(waitSignX, waitSignY),
        size = Size(54f, 18f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(waitSignX + 1f, waitSignY + 1f),
        size = Size(52f, 16f),
        cornerRadius = CornerRadius(2f, 2f),
        style = Stroke(width = 1f)
    )

    // C) CENTRAL ASPHALT ROADWAY
    drawRect(
        color = Color(0xFF151922),
        topLeft = Offset(roadLeft, roadwayTop),
        size = Size(roadW, roadwayH)
    )

    // Yellow roadside curbs
    drawLine(
        color = RoadLineYellow,
        start = Offset(roadLeft, roadwayTop),
        end = Offset(roadLeft, h),
        strokeWidth = 3.5f
    )
    drawLine(
        color = RoadLineYellow,
        start = Offset(roadRight, roadwayTop),
        end = Offset(roadRight, h),
        strokeWidth = 3.5f
    )

    // Pedestrian Zebra Crosswalk right before the barrier gate
    val crosswalkY = roadwayTop + 45f
    val crosswalkH = 28f
    drawRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(roadLeft, crosswalkY),
        size = Size(roadW, crosswalkH)
    )
    val numStripes = 5
    val stripeW = roadW / (numStripes * 2f)
    for (s in 0 until numStripes) {
        drawRect(
            color = Color(0xF0FFFFFF),
            topLeft = Offset(roadLeft + (s * 2 + 0.5f) * stripeW, crosswalkY + 3f),
            size = Size(stripeW, crosswalkH - 6f)
        )
    }

    // White dashed road centerline
    val dashRoadTop = crosswalkY + crosswalkH + 10f
    var dY = dashRoadTop
    while (dY < h - 10f) {
        drawLine(
            color = Color(0xE6FFFFFF),
            start = Offset(w * 0.5f, dY),
            end = Offset(w * 0.5f, dY + 16f),
            strokeWidth = 3f
        )
        dY += 28f
    }

    // =========================================================================
    // 3. ENTRANCE GATE: MG995 SERVO MOTOR BOX & ORANGE ARM WITH "STOP" SIGN
    // =========================================================================
    val gateY = roadwayTop + 8f

    // Black MG995 Servo Motor housing box on the right side of the roadway
    val servoBoxW = 28f
    val servoBoxH = 26f
    val servoX = roadRight - 10f
    val servoY = gateY - 6f

    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(servoX, servoY),
        size = Size(servoBoxW, servoBoxH),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Servo motor mounting ear & brass axle pin
    drawCircle(
        color = Color(0xFFF59E0B),
        radius = 5f,
        center = Offset(servoX + 8f, servoY + servoBoxH / 2f)
    )

    // Orange Barrier Gate Arm with STOP sign
    val armPivot = Offset(servoX + 8f, servoY + servoBoxH / 2f)
    val armLength = roadW + 12f
    val armThickness = 8f

    rotate(degrees = barrierAngle, pivot = armPivot) {
        // Orange main boom arm
        drawRoundRect(
            color = OrangeBarrierArm,
            topLeft = Offset(armPivot.x - armLength, armPivot.y - armThickness / 2f),
            size = Size(armLength, armThickness),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Red Octagonal "STOP" Sign attached in middle of barrier arm (From user photo 4)
        val stopCenter = Offset(armPivot.x - armLength * 0.55f, armPivot.y)
        drawCircle(
            color = StopSignRed,
            radius = 12f,
            center = stopCenter
        )
        drawCircle(
            color = Color.White,
            radius = 10f,
            center = stopCenter,
            style = Stroke(width = 1.5f)
        )
    }
}

// -------------------------------------------------------------------------------------------------
// PARKING BAY OVERLAY (Renders the photorealistic car model and sensor telemetry inside each stall)
// -------------------------------------------------------------------------------------------------
@Composable
private fun ParkingBayOverlayItem(
    slotId: Int,
    isOccupied: Boolean,
    carType: CarType,
    sonarPulse: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = remember(context) { ParkingHaptics(context) }
    var hasItemInitialized by remember { mutableStateOf(false) }

    // Beautiful tactile feedback when car arrives or departs
    LaunchedEffect(isOccupied) {
        if (!hasItemInitialized) {
            hasItemInitialized = true
            return@LaunchedEffect
        }
        if (isOccupied) {
            haptics.playCarArrived()
        } else {
            haptics.playCarDeparted()
        }
    }

    // Subtle Car Glide & Settling Animations
    val carOffsetY by animateDpAsState(
        targetValue = if (isOccupied) 0.dp else 38.dp,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "carOffsetY"
    )
    val carAlpha by animateFloatAsState(
        targetValue = if (isOccupied) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "carAlpha"
    )
    val carScale by animateFloatAsState(
        targetValue = if (isOccupied) 1f else 0.88f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "carScale"
    )

    // Subtle Vacant Badge Bloom Animations
    val vacantScale by animateFloatAsState(
        targetValue = if (!isOccupied) 1f else 0.70f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "vacantScale"
    )
    val vacantAlpha by animateFloatAsState(
        targetValue = if (!isOccupied) 1f else 0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "vacantAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("slot_bay_$slotId")
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer below the sensor stand
            Spacer(modifier = Modifier.height(28.dp))

            // Bay Display Area (Holds car when occupied, or clean vacant indicator)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Subtle floor arrival halo pulse when occupied
                if (carAlpha > 0.05f) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.09f * carAlpha))
                    )
                }

                // Photorealistic Car (Glide in / out with scale & alpha)
                if (carAlpha > 0.01f) {
                    RealisticTopDownCar(
                        carType = carType,
                        isParked = isOccupied,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = carOffsetY)
                            .graphicsLayer {
                                alpha = carAlpha
                                scaleX = carScale
                                scaleY = carScale
                            }
                    )
                }

                // Vacant Bay Indicator (Blooming in / out)
                if (vacantAlpha > 0.01f) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.graphicsLayer {
                            alpha = vacantAlpha
                            scaleX = vacantScale
                            scaleY = vacantScale
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald.copy(alpha = 0.18f * sonarPulse))
                                .border(1.5.dp, NeonEmerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                color = NeonEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "VACANT",
                            color = NeonEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Stencil label: "⬆ SLOT-1", "⬆ SLOT-2", "⬆ SLOT-3" matching user's photo!
            Text(
                text = "⬆ SLOT-$slotId",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xF0FFFFFF),
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}
