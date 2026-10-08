package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.CarType

@Composable
fun RealisticTopDownCar(
    carType: CarType,
    modifier: Modifier = Modifier,
    isParked: Boolean = true
) {
    // Smooth parking glide-in animation
    val parkProgress by animateFloatAsState(
        targetValue = if (isParked) 1f else 0f,
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "parkProgress"
    )

    // Subtle optical headlight shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "car_headlight")
    val headlightPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headlightPulse"
    )

    Box(
        modifier = modifier
            .offset(y = ((1f - parkProgress) * 35).dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (carType) {
                CarType.RED_SEDAN -> drawPhotorealisticSedan(w, h, headlightPulse, parkProgress)
                CarType.BLUE_SUV -> drawPhotorealisticSuv(w, h, headlightPulse, parkProgress)
                CarType.YELLOW_SPORTS -> drawPhotorealisticGtCoupe(w, h, headlightPulse, parkProgress)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. CRIMSON METALLIC EXECUTIVE SPORT SEDAN
// -------------------------------------------------------------
private fun DrawScope.drawPhotorealisticSedan(w: Float, h: Float, pulse: Float, progress: Float) {
    val carW = w * 0.60f
    val carH = h * 0.84f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 8f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // 1. Ambient Occlusion + Diffuse Ground Shadows
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left - 8f, top + 8f),
        size = Size(carW + 16f, carH + 12f),
        cornerRadius = CornerRadius(24f, 24f)
    )
    drawRoundRect(
        color = Color(0x77000000),
        topLeft = Offset(left - 3f, top + 4f),
        size = Size(carW + 6f, carH + 6f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // 2. Wheels with Brake Discs & Red Calipers
    val wheelW = carW * 0.16f
    val wheelH = carH * 0.22f
    val tireColor = Color(0xFF14171C)
    val discColor = Color(0xFFCBD5E1)
    val caliperColor = Color(0xFFEF4444)

    listOf(
        Offset(left - wheelW * 0.55f, top + carH * 0.16f),
        Offset(left + carW - wheelW * 0.45f, top + carH * 0.16f),
        Offset(left - wheelW * 0.55f, top + carH * 0.64f),
        Offset(left + carW - wheelW * 0.45f, top + carH * 0.64f)
    ).forEach { pos ->
        // Tire rubber
        drawRoundRect(
            color = tireColor,
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Brake disc
        drawCircle(
            color = discColor,
            radius = wheelW * 0.35f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
        // Brake Caliper
        drawRoundRect(
            color = caliperColor,
            topLeft = Offset(pos.x + 2f, pos.y + wheelH * 0.22f),
            size = Size(wheelW * 0.4f, wheelH * 0.25f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        // 5-Spoke Alloy Rim face
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = wheelW * 0.25f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // 3. Side Mirrors with Reflective Mirror Glass
    val mirrorW = 12f
    val mirrorH = 18f
    // Left mirror
    drawRoundRect(
        color = Color(0xFF991B1B),
        topLeft = Offset(left - mirrorW + 2f, top + carH * 0.26f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(left - mirrorW + 4f, top + carH * 0.26f + 2f),
        size = Size(3f, mirrorH - 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // Right mirror
    drawRoundRect(
        color = Color(0xFF991B1B),
        topLeft = Offset(left + carW - 2f, top + carH * 0.26f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(left + carW + mirrorW - 7f, top + carH * 0.26f + 2f),
        size = Size(3f, mirrorH - 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // 4. Main Body: Metallic Crimson Multi-Stop Gradient
    val bodyBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF7F1D1D),
            Color(0xFFB91C1C),
            Color(0xFFDC2626),
            Color(0xFFEF4444),
            Color(0xFFDC2626),
            Color(0xFF991B1B),
            Color(0xFF7F1D1D)
        ),
        startX = left,
        endX = left + carW
    )
    drawRoundRect(
        brush = bodyBrush,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(24f, 24f)
    )

    // 5. Hood Sculpted Lines & Front Honeycomb Grille
    // Sculpted lines
    drawLine(
        color = Color(0x40FFFFFF),
        start = Offset(left + carW * 0.28f, top + 10f),
        end = Offset(left + carW * 0.34f, top + carH * 0.28f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = Color(0x40FFFFFF),
        start = Offset(left + carW * 0.72f, top + 10f),
        end = Offset(left + carW * 0.66f, top + carH * 0.28f),
        strokeWidth = 2.5f
    )
    // Grille mesh
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(left + carW * 0.32f, top + 2f),
        size = Size(carW * 0.36f, 7f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 6. Curved Front Windshield + Black Ceramic Frit Border
    val frontGlassTop = top + carH * 0.24f
    val frontGlassH = carH * 0.19f

    // Frit border
    val fritPath = Path().apply {
        moveTo(left + carW * 0.14f, frontGlassTop + frontGlassH)
        lineTo(left + carW * 0.22f, frontGlassTop)
        lineTo(left + carW * 0.78f, frontGlassTop)
        lineTo(left + carW * 0.86f, frontGlassTop + frontGlassH)
        close()
    }
    drawPath(fritPath, color = Color(0xFF090D14))

    // Interior Cockpit Silhouette (Dashboard & Steering Wheel)
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.22f, frontGlassTop + 6f),
        size = Size(carW * 0.56f, 10f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Steering Wheel circle
    drawCircle(
        color = Color(0xFF475569),
        radius = 7f,
        center = Offset(left + carW * 0.35f, frontGlassTop + 14f),
        style = Stroke(width = 2.5f)
    )

    // Glass glare diagonal streak
    drawLine(
        color = Color(0x3560A5FA),
        start = Offset(left + carW * 0.28f, frontGlassTop + 4f),
        end = Offset(left + carW * 0.44f, frontGlassTop + frontGlassH - 4f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    // Two Windshield Wipers
    drawLine(
        color = Color(0xFF000000),
        start = Offset(left + carW * 0.30f, frontGlassTop + frontGlassH - 2f),
        end = Offset(left + carW * 0.48f, frontGlassTop + frontGlassH - 6f),
        strokeWidth = 2f
    )
    drawLine(
        color = Color(0xFF000000),
        start = Offset(left + carW * 0.52f, frontGlassTop + frontGlassH - 2f),
        end = Offset(left + carW * 0.70f, frontGlassTop + frontGlassH - 6f),
        strokeWidth = 2f
    )

    // 7. Panoramic Dark Glass Sunroof
    drawRoundRect(
        color = Color(0xFF0B1320),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.46f),
        size = Size(carW * 0.52f, carH * 0.20f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Roof Shark Fin Antenna
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW * 0.48f, top + carH * 0.64f),
        size = Size(carW * 0.04f, 10f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // 8. Rear Windshield with Defroster Lines
    val rearGlassTop = top + carH * 0.67f
    val rearGlassH = carH * 0.14f
    val rearGlassPath = Path().apply {
        moveTo(left + carW * 0.20f, rearGlassTop)
        lineTo(left + carW * 0.80f, rearGlassTop)
        lineTo(left + carW * 0.84f, rearGlassTop + rearGlassH)
        lineTo(left + carW * 0.16f, rearGlassTop + rearGlassH)
        close()
    }
    drawPath(rearGlassPath, color = Color(0xFF0B1320))
    // Defroster lines
    for (i in 1..2) {
        val lineY = rearGlassTop + (rearGlassH / 3f) * i
        drawLine(
            color = Color(0x33F59E0B),
            start = Offset(left + carW * 0.22f, lineY),
            end = Offset(left + carW * 0.78f, lineY),
            strokeWidth = 1f
        )
    }

    // 9. Rear Carbon Lip Spoiler & Exhaust Tips
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW * 0.16f, top + carH - 9f),
        size = Size(carW * 0.68f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Dual chrome exhaust tips
    drawCircle(color = Color(0xFFE2E8F0), radius = 3.5f, center = Offset(left + carW * 0.26f, top + carH - 1f))
    drawCircle(color = Color(0xFF0F172A), radius = 2f, center = Offset(left + carW * 0.26f, top + carH - 1f))
    drawCircle(color = Color(0xFFE2E8F0), radius = 3.5f, center = Offset(left + carW * 0.74f, top + carH - 1f))
    drawCircle(color = Color(0xFF0F172A), radius = 2f, center = Offset(left + carW * 0.74f, top + carH - 1f))

    // 10. Projector Headlights with Dual Optics & Amber Turn Signals
    val hlWidth = carW * 0.20f
    val hlHeight = 8f
    listOf(
        Offset(left + carW * 0.10f, top + 4f),
        Offset(left + carW * 0.70f, top + 4f)
    ).forEach { hlPos ->
        // Headlight housing
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = hlPos,
            size = Size(hlWidth, hlHeight),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Projector dual LED bulbs
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(hlPos.x + hlWidth * 0.35f, hlPos.y + 4f))
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(hlPos.x + hlWidth * 0.70f, hlPos.y + 4f))
        // Amber corner
        drawRoundRect(
            color = Color(0xFFF59E0B),
            topLeft = Offset(hlPos.x + 2f, hlPos.y + 1f),
            size = Size(3f, hlHeight - 2f),
            cornerRadius = CornerRadius(1f, 1f)
        )
    }

    // Light Beam on Road
    if (progress > 0.4f) {
        val beamAlpha = 0.35f * pulse * progress
        val beamBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF38BDF8).copy(alpha = beamAlpha), Color.Transparent),
            startY = top + 4f,
            endY = top - 45f
        )
        drawCircle(brush = beamBrush, radius = 26f, center = Offset(left + carW * 0.20f, top - 10f))
        drawCircle(brush = beamBrush, radius = 26f, center = Offset(left + carW * 0.80f, top - 10f))
    }

    // Rear Taillight Continuous LED Bar
    drawRoundRect(
        color = Color(0xFFFF1A1A),
        topLeft = Offset(left + carW * 0.14f, top + carH - 5f),
        size = Size(carW * 0.72f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

// -------------------------------------------------------------
// 2. MIDNIGHT SAPPHIRE LUXURY SUV
// -------------------------------------------------------------
private fun DrawScope.drawPhotorealisticSuv(w: Float, h: Float, pulse: Float, progress: Float) {
    val carW = w * 0.65f // Broad muscular stance
    val carH = h * 0.86f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 8f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // 1. Shadows
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left - 9f, top + 9f),
        size = Size(carW + 18f, carH + 14f),
        cornerRadius = CornerRadius(28f, 28f)
    )
    drawRoundRect(
        color = Color(0x77000000),
        topLeft = Offset(left - 3f, top + 4f),
        size = Size(carW + 6f, carH + 6f),
        cornerRadius = CornerRadius(24f, 24f)
    )

    // 2. Rugged All-Terrain Wheels with Gold Calipers
    val wheelW = carW * 0.17f
    val wheelH = carH * 0.24f
    listOf(
        Offset(left - wheelW * 0.50f, top + carH * 0.16f),
        Offset(left + carW - wheelW * 0.50f, top + carH * 0.16f),
        Offset(left - wheelW * 0.50f, top + carH * 0.65f),
        Offset(left + carW - wheelW * 0.50f, top + carH * 0.65f)
    ).forEach { pos ->
        // Deep black tread rubber
        drawRoundRect(
            color = Color(0xFF111418),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(7f, 7f)
        )
        // Silver multi-spoke rim
        drawCircle(
            color = Color(0xFF64748B),
            radius = wheelW * 0.38f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
        // Gold brake caliper
        drawRoundRect(
            color = Color(0xFFEAB308),
            topLeft = Offset(pos.x + 3f, pos.y + wheelH * 0.25f),
            size = Size(wheelW * 0.35f, wheelH * 0.25f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }

    // 3. Side Mirrors
    val mirrorW = 13f
    val mirrorH = 20f
    drawRoundRect(
        color = Color(0xFF1E3A8A),
        topLeft = Offset(left - mirrorW + 2f, top + carH * 0.24f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFF1E3A8A),
        topLeft = Offset(left + carW - 2f, top + carH * 0.24f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // 4. Muscular SUV Body: Sapphire Metallic Gradient
    val bodyBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF172554),
            Color(0xFF1E3A8A),
            Color(0xFF2563EB),
            Color(0xFF3B82F6),
            Color(0xFF2563EB),
            Color(0xFF1D4ED8),
            Color(0xFF172554)
        ),
        startX = left,
        endX = left + carW
    )
    drawRoundRect(
        brush = bodyBrush,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(26f, 26f)
    )

    // 5. Dual Silver Roof Rails
    val railColor = Color(0xFFE2E8F0)
    drawRoundRect(
        color = railColor,
        topLeft = Offset(left + carW * 0.15f, top + carH * 0.28f),
        size = Size(5f, carH * 0.48f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = railColor,
        topLeft = Offset(left + carW * 0.85f - 5f, top + carH * 0.28f),
        size = Size(5f, carH * 0.48f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 6. Windshield with Cockpit & Dual Wipers
    val frontGlassTop = top + carH * 0.22f
    val frontGlassH = carH * 0.19f
    val fritPath = Path().apply {
        moveTo(left + carW * 0.14f, frontGlassTop + frontGlassH)
        lineTo(left + carW * 0.20f, frontGlassTop)
        lineTo(left + carW * 0.80f, frontGlassTop)
        lineTo(left + carW * 0.86f, frontGlassTop + frontGlassH)
        close()
    }
    drawPath(fritPath, color = Color(0xFF090D14))

    // Interior steering wheel
    drawCircle(
        color = Color(0xFF475569),
        radius = 8f,
        center = Offset(left + carW * 0.35f, frontGlassTop + 13f),
        style = Stroke(width = 2.5f)
    )

    // Dual Sunroof Glass Panes
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(left + carW * 0.25f, top + carH * 0.44f),
        size = Size(carW * 0.50f, carH * 0.12f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(left + carW * 0.25f, top + carH * 0.58f),
        size = Size(carW * 0.50f, carH * 0.09f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Upright SUV Rear Glass
    drawRoundRect(
        color = Color(0xFF090D14),
        topLeft = Offset(left + carW * 0.18f, top + carH * 0.70f),
        size = Size(carW * 0.64f, carH * 0.13f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Rear Roof Spoiler with Center High Brake Light
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.16f, top + carH * 0.69f),
        size = Size(carW * 0.68f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFFF1A1A),
        topLeft = Offset(left + carW * 0.42f, top + carH * 0.69f),
        size = Size(carW * 0.16f, 3f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )

    // Quad Projector LED Headlights
    listOf(
        Offset(left + carW * 0.10f, top + 4f),
        Offset(left + carW * 0.68f, top + 4f)
    ).forEach { hlPos ->
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = hlPos,
            size = Size(carW * 0.22f, 8f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(hlPos.x + 6f, hlPos.y + 4f))
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(hlPos.x + 14f, hlPos.y + 4f))
    }

    // Headlight Light Beam Cone
    if (progress > 0.4f) {
        val beamAlpha = 0.38f * pulse * progress
        val beamBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF60A5FA).copy(alpha = beamAlpha), Color.Transparent),
            startY = top + 4f,
            endY = top - 45f
        )
        drawCircle(brush = beamBrush, radius = 28f, center = Offset(left + carW * 0.21f, top - 10f))
        drawCircle(brush = beamBrush, radius = 28f, center = Offset(left + carW * 0.79f, top - 10f))
    }

    // Rear Taillights
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.12f, top + carH - 6f),
        size = Size(carW * 0.24f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.64f, top + carH - 6f),
        size = Size(carW * 0.24f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

// -------------------------------------------------------------
// 3. CYBER AMBER PERFORMANCE GT COUPE
// -------------------------------------------------------------
private fun DrawScope.drawPhotorealisticGtCoupe(w: Float, h: Float, pulse: Float, progress: Float) {
    val carW = w * 0.61f
    val carH = h * 0.82f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 8f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // 1. Shadows
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left - 8f, top + 8f),
        size = Size(carW + 16f, carH + 12f),
        cornerRadius = CornerRadius(24f, 24f)
    )
    drawRoundRect(
        color = Color(0x77000000),
        topLeft = Offset(left - 3f, top + 4f),
        size = Size(carW + 6f, carH + 6f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // 2. Wide Racing Wheels with Bronze Rims & Red Brembo Calipers
    val wheelW = carW * 0.18f
    val wheelH = carH * 0.23f
    listOf(
        Offset(left - wheelW * 0.55f, top + carH * 0.17f),
        Offset(left + carW - wheelW * 0.45f, top + carH * 0.17f),
        Offset(left - wheelW * 0.55f, top + carH * 0.63f),
        Offset(left + carW - wheelW * 0.45f, top + carH * 0.63f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF14171A),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Bronze Rim
        drawCircle(
            color = Color(0xFFD97706),
            radius = wheelW * 0.35f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
        // Red Brembo Caliper
        drawRoundRect(
            color = Color(0xFFEF4444),
            topLeft = Offset(pos.x + 2f, pos.y + wheelH * 0.25f),
            size = Size(wheelW * 0.4f, wheelH * 0.25f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }

    // 3. Black Racing Mirrors
    val mirrorW = 11f
    val mirrorH = 16f
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left - mirrorW + 2f, top + carH * 0.28f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW - 2f, top + carH * 0.28f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 4. Aggressive Aerodynamic Body: Cyber Amber / Pearl Gold
    val bodyBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFB45309),
            Color(0xFFD97706),
            Color(0xFFF59E0B),
            Color(0xFFFDE047),
            Color(0xFFF59E0B),
            Color(0xFFD97706),
            Color(0xFFB45309)
        ),
        startX = left,
        endX = left + carW
    )
    drawRoundRect(
        brush = bodyBrush,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(24f, 24f)
    )

    // 5. Dual Carbon Fiber Hood Heat Extractor Vents
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW * 0.30f, top + carH * 0.12f),
        size = Size(carW * 0.14f, carH * 0.10f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW * 0.56f, top + carH * 0.12f),
        size = Size(carW * 0.14f, carH * 0.10f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 6. Fastback Windshield with Steering Wheel
    val frontGlassTop = top + carH * 0.26f
    val frontGlassH = carH * 0.20f
    val fritPath = Path().apply {
        moveTo(left + carW * 0.14f, frontGlassTop + frontGlassH)
        lineTo(left + carW * 0.24f, frontGlassTop)
        lineTo(left + carW * 0.76f, frontGlassTop)
        lineTo(left + carW * 0.86f, frontGlassTop + frontGlassH)
        close()
    }
    drawPath(fritPath, color = Color(0xFF090D14))

    // Sport steering wheel
    drawCircle(
        color = Color(0xFF64748B),
        radius = 7.5f,
        center = Offset(left + carW * 0.35f, frontGlassTop + 14f),
        style = Stroke(width = 2.5f)
    )

    // Carbon Fiber Roof Panel
    drawRoundRect(
        color = Color(0xFF141416),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.48f),
        size = Size(carW * 0.52f, carH * 0.18f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Fastback Rear Window
    val rearGlassTop = top + carH * 0.66f
    val rearGlassH = carH * 0.14f
    val rearGlassPath = Path().apply {
        moveTo(left + carW * 0.22f, rearGlassTop)
        lineTo(left + carW * 0.78f, rearGlassTop)
        lineTo(left + carW * 0.82f, rearGlassTop + rearGlassH)
        lineTo(left + carW * 0.18f, rearGlassTop + rearGlassH)
        close()
    }
    drawPath(rearGlassPath, color = Color(0xFF090D14))

    // 7. Prominent Carbon GT Rear Wing with Stanchions
    // Wing Stanchions
    drawRect(color = Color(0xFF27272A), topLeft = Offset(left + carW * 0.28f, top + carH - 16f), size = Size(4f, 10f))
    drawRect(color = Color(0xFF27272A), topLeft = Offset(left + carW * 0.68f, top + carH - 16f), size = Size(4f, 10f))
    // Wing Blade
    drawRoundRect(
        color = Color(0xFF09090B),
        topLeft = Offset(left + carW * 0.08f, top + carH - 16f),
        size = Size(carW * 0.84f, 7f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 8. Laser Headlights with Optics
    listOf(
        Offset(left + carW * 0.12f, top + 4f),
        Offset(left + carW * 0.68f, top + 4f)
    ).forEach { hlPos ->
        drawRoundRect(
            color = Color(0xFF18181B),
            topLeft = hlPos,
            size = Size(carW * 0.20f, 7f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(hlPos.x + 6f, hlPos.y + 3.5f))
        drawCircle(color = Color.White, radius = 2.5f, center = Offset(hlPos.x + 13f, hlPos.y + 3.5f))
    }

    // Light Beam
    if (progress > 0.4f) {
        val beamAlpha = 0.40f * pulse * progress
        val beamBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFDE047).copy(alpha = beamAlpha), Color.Transparent),
            startY = top + 4f,
            endY = top - 45f
        )
        drawCircle(brush = beamBrush, radius = 26f, center = Offset(left + carW * 0.22f, top - 10f))
        drawCircle(brush = beamBrush, radius = 26f, center = Offset(left + carW * 0.78f, top - 10f))
    }

    // Taillights
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.15f, top + carH - 6f),
        size = Size(carW * 0.70f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}
