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
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
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
                CarType.GREEN_POLICE -> drawLimeGreenPoliceCar(w, h, headlightPulse, parkProgress)
                CarType.GREEN_SPORTS -> drawLimeGreenSportsCoupe(w, h, headlightPulse, parkProgress)
                CarType.RED_VINTAGE -> drawVintageRedClassicBeetle(w, h, headlightPulse, parkProgress)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 1. SLOT 1: LIME GREEN POLICE CRUISER (Matching User's Toy Police Car with Badges & Lightning)
// -----------------------------------------------------------------------------------------
private fun DrawScope.drawLimeGreenPoliceCar(w: Float, h: Float, pulse: Float, progress: Float) {
    val carW = w * 0.62f
    val carH = h * 0.84f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 8f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Ground Drop Shadows
    drawRoundRect(
        color = Color(0x38000000),
        topLeft = Offset(left - 6f, top + 8f),
        size = Size(carW + 12f, carH + 12f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // 4 Wheels
    val wheelW = carW * 0.16f
    val wheelH = carH * 0.22f
    listOf(
        Offset(left - wheelW * 0.5f, top + carH * 0.16f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.16f),
        Offset(left - wheelW * 0.5f, top + carH * 0.64f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.64f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF14171C),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = wheelW * 0.3f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // Lime Green Main Body
    val bodyGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4D7C0F), // Dark olive green edge
            Color(0xFF84CC16), // Vibrant Lime Green
            Color(0xFFA3E635), // Bright Highlight
            Color(0xFF84CC16),
            Color(0xFF4D7C0F)
        ),
        startX = left,
        endX = left + carW
    )
    drawRoundRect(
        brush = bodyGradient,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(20f, 20f)
    )

    // Side Mirrors
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left - 7f, top + carH * 0.28f),
        size = Size(8f, 15f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left + carW - 1f, top + carH * 0.28f),
        size = Size(8f, 15f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Black Lightning Bolt Hood Accents (From user photo 3)
    val lightningPathLeft = Path().apply {
        moveTo(left + carW * 0.22f, top + carH * 0.08f)
        lineTo(left + carW * 0.16f, top + carH * 0.24f)
        lineTo(left + carW * 0.28f, top + carH * 0.22f)
        lineTo(left + carW * 0.20f, top + carH * 0.38f)
    }
    drawPath(lightningPathLeft, color = Color(0xFF18181B), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

    val lightningPathRight = Path().apply {
        moveTo(left + carW * 0.78f, top + carH * 0.08f)
        lineTo(left + carW * 0.84f, top + carH * 0.24f)
        lineTo(left + carW * 0.72f, top + carH * 0.22f)
        lineTo(left + carW * 0.80f, top + carH * 0.38f)
    }
    drawPath(lightningPathRight, color = Color(0xFF18181B), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

    // Windshield (Black / tinted)
    val windshieldPath = Path().apply {
        moveTo(left + carW * 0.20f, top + carH * 0.44f)
        lineTo(left + carW * 0.26f, top + carH * 0.28f)
        lineTo(left + carW * 0.74f, top + carH * 0.28f)
        lineTo(left + carW * 0.80f, top + carH * 0.44f)
        close()
    }
    drawPath(windshieldPath, color = Color(0xFF0F172A))
    drawPath(windshieldPath, color = Color(0xFF334155), style = Stroke(width = 1.5f))

    // Police Shield / Star Emblem on the front hood (Exact detail from user photo 3)
    val badgeCenter = Offset(cx, top + carH * 0.20f)
    drawCircle(color = Color(0xFF0F172A), radius = carW * 0.13f, center = badgeCenter)
    drawCircle(color = Color(0xFFFACC15), radius = carW * 0.11f, center = badgeCenter, style = Stroke(width = 2f))
    // 5-Point Golden Police Star
    drawCircle(color = Color(0xFFFACC15), radius = carW * 0.05f, center = badgeCenter)

    // Roof & Rear Window
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.46f),
        size = Size(carW * 0.52f, carH * 0.24f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // "POLICE" White/Yellow Stencil Text Badge on Roof
    drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = Offset(cx - carW * 0.22f, top + carH * 0.54f),
        size = Size(carW * 0.44f, carH * 0.08f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - carW * 0.20f, top + carH * 0.555f),
        size = Size(carW * 0.40f, carH * 0.05f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Rear Windshield
    val rearGlass = Path().apply {
        moveTo(left + carW * 0.24f, top + carH * 0.72f)
        lineTo(left + carW * 0.76f, top + carH * 0.72f)
        lineTo(left + carW * 0.80f, top + carH * 0.84f)
        lineTo(left + carW * 0.20f, top + carH * 0.84f)
        close()
    }
    drawPath(rearGlass, color = Color(0xFF0F172A))

    // Headlights (Twin white headlights with pulse)
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(left + carW * 0.14f, top + 3f),
        size = Size(carW * 0.20f, 6f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(left + carW * 0.66f, top + 3f),
        size = Size(carW * 0.20f, 6f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Taillights (Red)
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.16f, top + carH - 5f),
        size = Size(carW * 0.22f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.62f, top + carH - 5f),
        size = Size(carW * 0.22f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

// -----------------------------------------------------------------------------------------
// 2. SLOT 2: LIME GREEN SPORTS GT COUPE (Matching User's Slot 2 Car in Photo 2 & 4)
// -----------------------------------------------------------------------------------------
private fun DrawScope.drawLimeGreenSportsCoupe(w: Float, h: Float, pulse: Float, progress: Float) {
    val carW = w * 0.62f
    val carH = h * 0.84f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 8f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Ground Shadow
    drawRoundRect(
        color = Color(0x38000000),
        topLeft = Offset(left - 6f, top + 8f),
        size = Size(carW + 12f, carH + 12f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // 4 Wheels
    val wheelW = carW * 0.16f
    val wheelH = carH * 0.22f
    listOf(
        Offset(left - wheelW * 0.5f, top + carH * 0.16f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.16f),
        Offset(left - wheelW * 0.5f, top + carH * 0.64f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.64f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF14171C),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = wheelW * 0.3f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // Lime Green Body (Vibrant Apple / Lime Sport finish)
    val bodyGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF65A30D),
            Color(0xFFA3E635),
            Color(0xFFBEF264),
            Color(0xFFA3E635),
            Color(0xFF65A30D)
        ),
        startX = left,
        endX = left + carW
    )
    drawRoundRect(
        brush = bodyGradient,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // Aerodynamic Hood Creases & Air Vent
    drawLine(
        color = Color(0xFF4D7C0F),
        start = Offset(left + carW * 0.30f, top + carH * 0.08f),
        end = Offset(left + carW * 0.34f, top + carH * 0.28f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = Color(0xFF4D7C0F),
        start = Offset(left + carW * 0.70f, top + carH * 0.08f),
        end = Offset(left + carW * 0.66f, top + carH * 0.28f),
        strokeWidth = 2.5f
    )
    // Central Sport Hood Scoop
    drawRoundRect(
        color = Color(0xFF365314),
        topLeft = Offset(cx - carW * 0.14f, top + carH * 0.14f),
        size = Size(carW * 0.28f, carH * 0.08f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Side Mirrors
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left - 7f, top + carH * 0.30f),
        size = Size(8f, 15f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left + carW - 1f, top + carH * 0.30f),
        size = Size(8f, 15f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Cockpit Curved Windshield
    val windshieldPath = Path().apply {
        moveTo(left + carW * 0.18f, top + carH * 0.44f)
        lineTo(left + carW * 0.24f, top + carH * 0.28f)
        lineTo(left + carW * 0.76f, top + carH * 0.28f)
        lineTo(left + carW * 0.82f, top + carH * 0.44f)
        close()
    }
    drawPath(windshieldPath, color = Color(0xFF0F172A))
    drawPath(windshieldPath, color = Color(0xFF334155), style = Stroke(width = 1.5f))

    // Roof Panel
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.46f),
        size = Size(carW * 0.52f, carH * 0.22f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Fastback Rear Glass
    val rearGlass = Path().apply {
        moveTo(left + carW * 0.22f, top + carH * 0.70f)
        lineTo(left + carW * 0.78f, top + carH * 0.70f)
        lineTo(left + carW * 0.82f, top + carH * 0.84f)
        lineTo(left + carW * 0.18f, top + carH * 0.84f)
        close()
    }
    drawPath(rearGlass, color = Color(0xFF0F172A))

    // Rear GT Spoiler Wing
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.08f, top + carH * 0.90f),
        size = Size(carW * 0.84f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Headlights
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(left + carW * 0.12f, top + 4f),
        size = Size(carW * 0.22f, 6f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(left + carW * 0.66f, top + 4f),
        size = Size(carW * 0.22f, 6f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Taillights
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.14f, top + carH - 4f),
        size = Size(carW * 0.72f, 3.5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

// -----------------------------------------------------------------------------------------
// 3. SLOT 3: VINTAGE RED CLASSIC BEETLE COUPE (Matching User's Red Car in Photo 1 & 4)
// -----------------------------------------------------------------------------------------
private fun DrawScope.drawVintageRedClassicBeetle(w: Float, h: Float, pulse: Float, progress: Float) {
    val carW = w * 0.60f
    val carH = h * 0.82f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 8f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Ground Shadow (Rounded vintage shadow)
    drawOval(
        color = Color(0x40000000),
        topLeft = Offset(left - 8f, top + 6f),
        size = Size(carW + 16f, carH + 10f)
    )

    // 4 Vintage Wheels with Chrome Hubcaps
    val wheelW = carW * 0.15f
    val wheelH = carH * 0.20f
    listOf(
        Offset(left - wheelW * 0.45f, top + carH * 0.18f),
        Offset(left + carW - wheelW * 0.55f, top + carH * 0.18f),
        Offset(left - wheelW * 0.45f, top + carH * 0.66f),
        Offset(left + carW - wheelW * 0.55f, top + carH * 0.66f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF18181B),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(5f, 5f)
        )
        // Vintage Chrome Hubcap
        drawCircle(
            color = Color(0xFFE2E8F0),
            radius = wheelW * 0.35f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // Wide Curving Fenders (Characteristic of the user's vintage beetle model)
    val fenderGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF7F1D1D),
            Color(0xFFDC2626),
            Color(0xFFEF4444),
            Color(0xFFDC2626),
            Color(0xFF7F1D1D)
        ),
        startX = left - 4f,
        endX = left + carW + 4f
    )

    // Front Fenders (Rounded)
    drawRoundRect(
        brush = fenderGradient,
        topLeft = Offset(left - 4f, top + carH * 0.10f),
        size = Size(carW + 8f, carH * 0.30f),
        cornerRadius = CornerRadius(18f, 18f)
    )

    // Rear Fenders (Prominently wide & bulbous as seen in photo 1)
    drawRoundRect(
        brush = fenderGradient,
        topLeft = Offset(left - 5f, top + carH * 0.58f),
        size = Size(carW + 10f, carH * 0.34f),
        cornerRadius = CornerRadius(20f, 20f)
    )

    // Main Rounded Cabin Shell
    drawRoundRect(
        brush = fenderGradient,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(26f, 26f)
    )

    // Vintage Chrome Front Bumper Bar
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(left + carW * 0.10f, top - 2f),
        size = Size(carW * 0.80f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Vintage Round Headlights (Bullet style)
    drawCircle(
        color = Color(0xFFFFFFFF),
        radius = carW * 0.11f,
        center = Offset(left + carW * 0.22f, top + 6f)
    )
    drawCircle(
        color = Color(0xFF94A3B8),
        radius = carW * 0.11f,
        center = Offset(left + carW * 0.22f, top + 6f),
        style = Stroke(width = 1.5f)
    )
    drawCircle(
        color = Color(0xFFFFFFFF),
        radius = carW * 0.11f,
        center = Offset(left + carW * 0.78f, top + 6f)
    )
    drawCircle(
        color = Color(0xFF94A3B8),
        radius = carW * 0.11f,
        center = Offset(left + carW * 0.78f, top + 6f),
        style = Stroke(width = 1.5f)
    )

    // Rounded Beetle Hood Ridge Line
    drawLine(
        color = Color(0xFF7F1D1D),
        start = Offset(cx, top + 4f),
        end = Offset(cx, top + carH * 0.32f),
        strokeWidth = 2f
    )

    // Split Windshield (Classic vintage rounded curve)
    val vintageWindshield = Path().apply {
        moveTo(left + carW * 0.16f, top + carH * 0.44f)
        lineTo(left + carW * 0.24f, top + carH * 0.30f)
        lineTo(left + carW * 0.76f, top + carH * 0.30f)
        lineTo(left + carW * 0.84f, top + carH * 0.44f)
        close()
    }
    drawPath(vintageWindshield, color = Color(0xFF0F172A))
    drawPath(vintageWindshield, color = Color(0xFFCBD5E1), style = Stroke(width = 1.5f))
    // Center divider post
    drawLine(
        color = Color(0xFFDC2626),
        start = Offset(cx, top + carH * 0.30f),
        end = Offset(cx, top + carH * 0.44f),
        strokeWidth = 3f
    )

    // Rounded Oval Roof Top
    drawRoundRect(
        color = Color(0xFFB91C1C),
        topLeft = Offset(left + carW * 0.22f, top + carH * 0.46f),
        size = Size(carW * 0.56f, carH * 0.22f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Small Oval Rear Window (Classic VW Beetle split oval glass)
    drawOval(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - carW * 0.18f, top + carH * 0.70f),
        size = Size(carW * 0.36f, carH * 0.10f)
    )
    drawOval(
        color = Color(0xFFCBD5E1),
        topLeft = Offset(cx - carW * 0.18f, top + carH * 0.70f),
        size = Size(carW * 0.36f, carH * 0.10f),
        style = Stroke(width = 1.5f)
    )

    // Vintage Chrome Rear Bumper Bar & Round Taillights
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(left + carW * 0.10f, top + carH - 3f),
        size = Size(carW * 0.80f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawCircle(
        color = Color(0xFFEF4444),
        radius = carW * 0.08f,
        center = Offset(left + carW * 0.20f, top + carH - 6f)
    )
    drawCircle(
        color = Color(0xFFEF4444),
        radius = carW * 0.08f,
        center = Offset(left + carW * 0.80f, top + carH - 6f)
    )
}
