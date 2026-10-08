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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.CarType

@Composable
fun RealisticTopDownCar(
    carType: CarType,
    modifier: Modifier = Modifier,
    isParked: Boolean = true
) {
    // Entrance / Parking animation
    val parkProgress by animateFloatAsState(
        targetValue = if (isParked) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "parkProgress"
    )

    // Subtle idle headlight pulse
    val infiniteTransition = rememberInfiniteTransition(label = "car_idle")
    val headlightPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headlightPulse"
    )

    Box(
        modifier = modifier
            .offset(y = ((1f - parkProgress) * 45).dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (carType) {
                CarType.RED_SEDAN -> drawRedSedan(w, h, headlightPulse, parkProgress)
                CarType.BLUE_SUV -> drawBlueSuv(w, h, headlightPulse, parkProgress)
                CarType.YELLOW_SPORTS -> drawYellowSportsCoupe(w, h, headlightPulse, parkProgress)
            }
        }
    }
}

// 1. CRIMSON RED PERFORMANCE SEDAN
private fun DrawScope.drawRedSedan(w: Float, h: Float, headlightPulse: Float, progress: Float) {
    val carW = w * 0.58f
    val carH = h * 0.82f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 10f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Contact drop shadow
    drawRoundRect(
        color = Color(0x66000000),
        topLeft = Offset(left - 6f, top + 6f),
        size = Size(carW + 12f, carH + 10f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // 4 Wheels
    val wheelW = carW * 0.16f
    val wheelH = carH * 0.22f
    val wheelColor = Color(0xFF1E242B)
    val rimColor = Color(0xFF94A3B8)

    listOf(
        Offset(left - wheelW * 0.6f, top + carH * 0.15f), // Front Left
        Offset(left + carW - wheelW * 0.4f, top + carH * 0.15f), // Front Right
        Offset(left - wheelW * 0.6f, top + carH * 0.65f), // Rear Left
        Offset(left + carW - wheelW * 0.4f, top + carH * 0.65f) // Rear Right
    ).forEach { pos ->
        drawRoundRect(
            color = wheelColor,
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Alloy rim center
        drawRoundRect(
            color = rimColor,
            topLeft = Offset(pos.x + 2f, pos.y + wheelH * 0.25f),
            size = Size(wheelW - 4f, wheelH * 0.5f),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }

    // Side Mirrors
    drawRoundRect(
        color = Color(0xFF991B1B),
        topLeft = Offset(left - 9f, top + carH * 0.28f),
        size = Size(10f, 16f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFF991B1B),
        topLeft = Offset(left + carW - 1f, top + carH * 0.28f),
        size = Size(10f, 16f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Main Car Body (Metallic Crimson Gradient)
    val bodyBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF991B1B),
            Color(0xFFDC2626),
            Color(0xFFEF4444),
            Color(0xFFDC2626),
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

    // Body highlights & hood lines
    val hoodLineColor = Color(0x33FFFFFF)
    drawLine(
        color = hoodLineColor,
        start = Offset(left + carW * 0.26f, top + 10f),
        end = Offset(left + carW * 0.32f, top + carH * 0.32f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = hoodLineColor,
        start = Offset(left + carW * 0.74f, top + 10f),
        end = Offset(left + carW * 0.68f, top + carH * 0.32f),
        strokeWidth = 2.5f
    )

    // Windshield & Glass (Front)
    val glassColor = Color(0xFF0F172A)
    val glassGlaze = Color(0x4038BDF8)
    val frontGlassTop = top + carH * 0.26f
    val frontGlassH = carH * 0.18f

    val frontGlassPath = Path().apply {
        moveTo(left + carW * 0.16f, frontGlassTop + frontGlassH)
        lineTo(left + carW * 0.24f, frontGlassTop)
        lineTo(left + carW * 0.76f, frontGlassTop)
        lineTo(left + carW * 0.84f, frontGlassTop + frontGlassH)
        close()
    }
    drawPath(frontGlassPath, color = glassColor)
    // Front Glass Glare reflection
    drawLine(
        color = glassGlaze,
        start = Offset(left + carW * 0.30f, frontGlassTop + 4f),
        end = Offset(left + carW * 0.45f, frontGlassTop + frontGlassH - 4f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )

    // Panoramic Sunroof
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.26f, top + carH * 0.46f),
        size = Size(carW * 0.48f, carH * 0.20f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Rear Windshield
    val rearGlassTop = top + carH * 0.68f
    val rearGlassH = carH * 0.14f
    val rearGlassPath = Path().apply {
        moveTo(left + carW * 0.22f, rearGlassTop)
        lineTo(left + carW * 0.78f, rearGlassTop)
        lineTo(left + carW * 0.84f, rearGlassTop + rearGlassH)
        lineTo(left + carW * 0.16f, rearGlassTop + rearGlassH)
        close()
    }
    drawPath(rearGlassPath, color = glassColor)

    // Rear Lip Spoiler
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW * 0.18f, top + carH - 12f),
        size = Size(carW * 0.64f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // LED Headlights
    val headlightColor = Color(0xFFFFFFFF)
    drawRoundRect(
        color = headlightColor,
        topLeft = Offset(left + carW * 0.12f, top + 4f),
        size = Size(carW * 0.18f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = headlightColor,
        topLeft = Offset(left + carW * 0.70f, top + 4f),
        size = Size(carW * 0.18f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Headlight forward light beam cone
    if (progress > 0.4f) {
        val beamAlpha = (0.28f * headlightPulse * progress)
        val beamBrushLeft = Brush.verticalGradient(
            colors = listOf(Color(0xFF38BDF8).copy(alpha = beamAlpha), Color.Transparent),
            startY = top + 4f,
            endY = top - 45f
        )
        drawCircle(
            brush = beamBrushLeft,
            radius = 26f,
            center = Offset(left + carW * 0.21f, top - 8f)
        )
        drawCircle(
            brush = beamBrushLeft,
            radius = 26f,
            center = Offset(left + carW * 0.79f, top - 8f)
        )
    }

    // Rear Tail LED Light Bar
    drawRoundRect(
        color = Color(0xFFFF2222),
        topLeft = Offset(left + carW * 0.15f, top + carH - 5f),
        size = Size(carW * 0.70f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

// 2. MIDNIGHT BLUE LUXURY SUV
private fun DrawScope.drawBlueSuv(w: Float, h: Float, headlightPulse: Float, progress: Float) {
    val carW = w * 0.64f // Wider stance for SUV
    val carH = h * 0.85f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 10f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Drop shadow
    drawRoundRect(
        color = Color(0x66000000),
        topLeft = Offset(left - 7f, top + 6f),
        size = Size(carW + 14f, carH + 12f),
        cornerRadius = CornerRadius(26f, 26f)
    )

    // Beefier SUV Wheels
    val wheelW = carW * 0.17f
    val wheelH = carH * 0.24f
    val wheelColor = Color(0xFF181C22)

    listOf(
        Offset(left - wheelW * 0.5f, top + carH * 0.16f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.16f),
        Offset(left - wheelW * 0.5f, top + carH * 0.64f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.64f)
    ).forEach { pos ->
        drawRoundRect(
            color = wheelColor,
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(7f, 7f)
        )
        drawRoundRect(
            color = Color(0xFF64748B),
            topLeft = Offset(pos.x + 3f, pos.y + wheelH * 0.25f),
            size = Size(wheelW - 6f, wheelH * 0.5f),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // Side Mirrors
    drawRoundRect(
        color = Color(0xFF1E3A8A),
        topLeft = Offset(left - 10f, top + carH * 0.26f),
        size = Size(11f, 18f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFF1E3A8A),
        topLeft = Offset(left + carW - 1f, top + carH * 0.26f),
        size = Size(11f, 18f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // SUV Body - Deep Blue Metallic
    val bodyBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1E3A8A),
            Color(0xFF2563EB),
            Color(0xFF3B82F6),
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

    // Silver Roof Rack Rails
    val railColor = Color(0xFFCBD5E1)
    drawRoundRect(
        color = railColor,
        topLeft = Offset(left + carW * 0.16f, top + carH * 0.28f),
        size = Size(4f, carH * 0.48f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = railColor,
        topLeft = Offset(left + carW * 0.84f - 4f, top + carH * 0.28f),
        size = Size(4f, carH * 0.48f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Windshield (Front)
    val glassColor = Color(0xFF0F172A)
    val frontGlassTop = top + carH * 0.24f
    val frontGlassH = carH * 0.18f

    val frontGlassPath = Path().apply {
        moveTo(left + carW * 0.14f, frontGlassTop + frontGlassH)
        lineTo(left + carW * 0.22f, frontGlassTop)
        lineTo(left + carW * 0.78f, frontGlassTop)
        lineTo(left + carW * 0.86f, frontGlassTop + frontGlassH)
        close()
    }
    drawPath(frontGlassPath, color = glassColor)

    // Roof Center Panel
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.44f),
        size = Size(carW * 0.52f, carH * 0.22f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Rear Windshield (SUV upright angle)
    drawRoundRect(
        color = glassColor,
        topLeft = Offset(left + carW * 0.18f, top + carH * 0.70f),
        size = Size(carW * 0.64f, carH * 0.14f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Dual Hood Accents
    drawLine(
        color = Color(0x33FFFFFF),
        start = Offset(left + carW * 0.32f, top + 10f),
        end = Offset(left + carW * 0.32f, top + carH * 0.22f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = Color(0x33FFFFFF),
        start = Offset(left + carW * 0.68f, top + 10f),
        end = Offset(left + carW * 0.68f, top + carH * 0.22f),
        strokeWidth = 2.5f
    )

    // Headlights
    drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = Offset(left + carW * 0.12f, top + 4f),
        size = Size(carW * 0.20f, 7f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = Offset(left + carW * 0.68f, top + 4f),
        size = Size(carW * 0.20f, 7f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Headlight Glow
    if (progress > 0.4f) {
        val beamAlpha = (0.32f * headlightPulse * progress)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF60A5FA).copy(alpha = beamAlpha), Color.Transparent),
                startY = top + 4f,
                endY = top - 45f
            ),
            radius = 28f,
            center = Offset(left + carW * 0.22f, top - 8f)
        )
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF60A5FA).copy(alpha = beamAlpha), Color.Transparent),
                startY = top + 4f,
                endY = top - 45f
            ),
            radius = 28f,
            center = Offset(left + carW * 0.78f, top - 8f)
        )
    }

    // Rear Lights (L-shaped clusters)
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.12f, top + carH - 7f),
        size = Size(carW * 0.22f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.66f, top + carH - 7f),
        size = Size(carW * 0.22f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

// 3. CYBER AMBER PERFORMANCE GT COUPE
private fun DrawScope.drawYellowSportsCoupe(w: Float, h: Float, headlightPulse: Float, progress: Float) {
    val carW = w * 0.60f
    val carH = h * 0.80f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 10f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Shadow
    drawRoundRect(
        color = Color(0x66000000),
        topLeft = Offset(left - 6f, top + 6f),
        size = Size(carW + 12f, carH + 10f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // Wide Racing Wheels
    val wheelW = carW * 0.18f
    val wheelH = carH * 0.23f
    listOf(
        Offset(left - wheelW * 0.55f, top + carH * 0.18f),
        Offset(left + carW - wheelW * 0.45f, top + carH * 0.18f),
        Offset(left - wheelW * 0.55f, top + carH * 0.62f),
        Offset(left + carW - wheelW * 0.45f, top + carH * 0.62f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF171717),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Gold/bronze caliper/rim accent
        drawRoundRect(
            color = Color(0xFFD97706),
            topLeft = Offset(pos.x + 3f, pos.y + wheelH * 0.3f),
            size = Size(wheelW - 6f, wheelH * 0.4f),
            cornerRadius = CornerRadius(3f, 3f)
        )
    }

    // Mirrors
    drawRoundRect(
        color = Color(0xFF18181B), // Black mirrors
        topLeft = Offset(left - 9f, top + carH * 0.30f),
        size = Size(10f, 15f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW - 1f, top + carH * 0.30f),
        size = Size(10f, 15f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Sports Body - Cyber Yellow / Amber
    val bodyBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFD97706),
            Color(0xFFF59E0B),
            Color(0xFFFCD34D),
            Color(0xFFF59E0B),
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

    // Carbon Fiber Hood Heat Extractors (Twin Black vents)
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.32f, top + carH * 0.12f),
        size = Size(carW * 0.12f, carH * 0.10f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.56f, top + carH * 0.12f),
        size = Size(carW * 0.12f, carH * 0.10f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Fastback Windshield
    val frontGlassTop = top + carH * 0.28f
    val frontGlassH = carH * 0.20f
    val frontGlassPath = Path().apply {
        moveTo(left + carW * 0.16f, frontGlassTop + frontGlassH)
        lineTo(left + carW * 0.26f, frontGlassTop)
        lineTo(left + carW * 0.74f, frontGlassTop)
        lineTo(left + carW * 0.84f, frontGlassTop + frontGlassH)
        close()
    }
    drawPath(frontGlassPath, color = Color(0xFF0F172A))

    // Carbon Roof
    drawRoundRect(
        color = Color(0xFF18181B),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.48f),
        size = Size(carW * 0.52f, carH * 0.18f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Rear Window Fastback Slope
    val rearGlassTop = top + carH * 0.66f
    val rearGlassH = carH * 0.14f
    val rearGlassPath = Path().apply {
        moveTo(left + carW * 0.22f, rearGlassTop)
        lineTo(left + carW * 0.78f, rearGlassTop)
        lineTo(left + carW * 0.82f, rearGlassTop + rearGlassH)
        lineTo(left + carW * 0.18f, rearGlassTop + rearGlassH)
        close()
    }
    drawPath(rearGlassPath, color = Color(0xFF0F172A))

    // Prominent Carbon GT Rear Wing / Spoiler
    drawRoundRect(
        color = Color(0xFF09090B),
        topLeft = Offset(left + carW * 0.12f, top + carH - 12f),
        size = Size(carW * 0.76f, 7f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Headlights
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(left + carW * 0.14f, top + 4f),
        size = Size(carW * 0.18f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(left + carW * 0.68f, top + 4f),
        size = Size(carW * 0.18f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Headlight Glow
    if (progress > 0.4f) {
        val beamAlpha = (0.35f * headlightPulse * progress)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFDE047).copy(alpha = beamAlpha), Color.Transparent),
                startY = top + 4f,
                endY = top - 45f
            ),
            radius = 26f,
            center = Offset(left + carW * 0.23f, top - 8f)
        )
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFDE047).copy(alpha = beamAlpha), Color.Transparent),
                startY = top + 4f,
                endY = top - 45f
            ),
            radius = 26f,
            center = Offset(left + carW * 0.77f, top - 8f)
        )
    }

    // Taillights
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.15f, top + carH - 6f),
        size = Size(carW * 0.70f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}
