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

/**
 * Minimalist, Professional Top-Down Automotive Models.
 * Perfectly proportioned with ~1.68 length-to-width ratio to avoid elongated appearance.
 */
@Composable
fun RealisticTopDownCar(
    carType: CarType,
    modifier: Modifier = Modifier,
    isParked: Boolean = true
) {
    // Smooth parking glide-in animation
    val parkProgress by animateFloatAsState(
        targetValue = if (isParked) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "parkProgress"
    )

    // Subtle optical headlight shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "car_headlight")
    val headlightPulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headlightPulse"
    )

    Box(
        modifier = modifier
            .offset(y = ((1f - parkProgress) * 25).dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (carType) {
                CarType.GREEN_POLICE -> drawLimeGreenSedan(w, h, headlightPulse, parkProgress)
                CarType.GREEN_SPORTS -> drawLimeGreenSportsCoupe(w, h, headlightPulse, parkProgress)
                CarType.RED_VINTAGE -> drawVintageRedCoupe(w, h, headlightPulse, parkProgress)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 1. SLOT 1: LIME GREEN MODERN SEDAN (Minimalist, Sleek, Proportional)
// -----------------------------------------------------------------------------------------
private fun DrawScope.drawLimeGreenSedan(w: Float, h: Float, pulse: Float, progress: Float) {
    // Proportional dimensions: 1.68 length-to-width ratio
    val carW = (w * 0.64f).coerceIn(44f, 62f)
    val carH = carW * 1.68f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 6f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Soft Ambient Ground Shadow
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left - 4f, top + 5f),
        size = Size(carW + 8f, carH + 7f),
        cornerRadius = CornerRadius(14f, 14f)
    )

    // 4 Matte Black Wheels with subtle alloy center
    val wheelW = carW * 0.15f
    val wheelH = carH * 0.20f
    listOf(
        Offset(left - wheelW * 0.45f, top + carH * 0.16f),
        Offset(left + carW - wheelW * 0.55f, top + carH * 0.16f),
        Offset(left - wheelW * 0.45f, top + carH * 0.66f),
        Offset(left + carW - wheelW * 0.55f, top + carH * 0.66f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF1E242B),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = wheelW * 0.25f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // Lime Green Main Body (Metallic gradient)
    val bodyGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF527E12), // Subtle shadow edge
            Color(0xFF84CC16), // Vibrant Lime
            Color(0xFFA3E635), // Gloss highlight
            Color(0xFF84CC16),
            Color(0xFF527E12)
        ),
        startX = left,
        endX = left + carW
    )
    val bodyRadius = carW * 0.20f
    drawRoundRect(
        brush = bodyGradient,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(bodyRadius, bodyRadius)
    )

    // Sleek Side Mirrors
    val mirrorW = carW * 0.10f
    val mirrorH = carH * 0.08f
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left - mirrorW * 0.85f, top + carH * 0.30f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(2.5f, 2.5f)
    )
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left + carW - mirrorW * 0.15f, top + carH * 0.30f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(2.5f, 2.5f)
    )

    // Tinted Cockpit Glass (Windshield, Roof, Rear Window)
    // Windshield
    val windshieldPath = Path().apply {
        moveTo(left + carW * 0.20f, top + carH * 0.46f)
        lineTo(left + carW * 0.25f, top + carH * 0.30f)
        lineTo(left + carW * 0.75f, top + carH * 0.30f)
        lineTo(left + carW * 0.80f, top + carH * 0.46f)
        close()
    }
    drawPath(windshieldPath, color = Color(0xFF0F172A))
    drawPath(windshieldPath, color = Color(0xFF334155), style = Stroke(width = 1f))

    // Roof Panel
    val roofW = carW * 0.54f
    val roofH = carH * 0.22f
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.23f, top + carH * 0.46f),
        size = Size(roofW, roofH),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Rear Window Glass
    val rearGlassPath = Path().apply {
        moveTo(left + carW * 0.23f, top + carH * 0.68f)
        lineTo(left + carW * 0.77f, top + carH * 0.68f)
        lineTo(left + carW * 0.80f, top + carH * 0.80f)
        lineTo(left + carW * 0.20f, top + carH * 0.80f)
        close()
    }
    drawPath(rearGlassPath, color = Color(0xFF0F172A))

    // Minimalist Hood Creases
    drawLine(
        color = Color(0x30FFFFFF),
        start = Offset(left + carW * 0.30f, top + carH * 0.10f),
        end = Offset(left + carW * 0.33f, top + carH * 0.28f),
        strokeWidth = 1.2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0x30FFFFFF),
        start = Offset(left + carW * 0.70f, top + carH * 0.10f),
        end = Offset(left + carW * 0.67f, top + carH * 0.28f),
        strokeWidth = 1.2f,
        cap = StrokeCap.Round
    )

    // Projector LED Headlights (Crisp white with pulse)
    val lightW = carW * 0.18f
    val lightH = 4.5f
    drawRoundRect(
        color = Color(0xFFF8FAFC).copy(alpha = pulse),
        topLeft = Offset(left + carW * 0.15f, top + 2f),
        size = Size(lightW, lightH),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFF8FAFC).copy(alpha = pulse),
        topLeft = Offset(left + carW * 0.67f, top + 2f),
        size = Size(lightW, lightH),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Sleek Rear LED Taillights (Ruby red)
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.16f, top + carH - 4.5f),
        size = Size(lightW, 3.5f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.66f, top + carH - 4.5f),
        size = Size(lightW, 3.5f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )
}

// -----------------------------------------------------------------------------------------
// 2. SLOT 2: LIME GREEN SPORTS GT COUPE (Minimalist, Sporty, Proportional)
// -----------------------------------------------------------------------------------------
private fun DrawScope.drawLimeGreenSportsCoupe(w: Float, h: Float, pulse: Float, progress: Float) {
    // Sporty dimensions: ~1.65 ratio, slightly wider stance
    val carW = (w * 0.66f).coerceIn(46f, 64f)
    val carH = carW * 1.65f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 6f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Drop Shadow
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left - 4f, top + 5f),
        size = Size(carW + 8f, carH + 7f),
        cornerRadius = CornerRadius(14f, 14f)
    )

    // 4 Wide Sports Wheels
    val wheelW = carW * 0.16f
    val wheelH = carH * 0.21f
    listOf(
        Offset(left - wheelW * 0.5f, top + carH * 0.18f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.18f),
        Offset(left - wheelW * 0.5f, top + carH * 0.64f),
        Offset(left + carW - wheelW * 0.5f, top + carH * 0.64f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF1E242B),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = wheelW * 0.28f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // Lime Green Body with Gloss Gradient
    val sportGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4D7C0F),
            Color(0xFF84CC16),
            Color(0xFFA3E635),
            Color(0xFF84CC16),
            Color(0xFF4D7C0F)
        ),
        startX = left,
        endX = left + carW
    )
    val bodyRadius = carW * 0.22f
    drawRoundRect(
        brush = sportGradient,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(bodyRadius, bodyRadius)
    )

    // Side Mirrors
    val mirrorW = carW * 0.10f
    val mirrorH = carH * 0.08f
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left - mirrorW * 0.85f, top + carH * 0.32f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(2.5f, 2.5f)
    )
    drawRoundRect(
        color = Color(0xFF65A30D),
        topLeft = Offset(left + carW - mirrorW * 0.15f, top + carH * 0.32f),
        size = Size(mirrorW, mirrorH),
        cornerRadius = CornerRadius(2.5f, 2.5f)
    )

    // Fastback Windshield
    val windshieldPath = Path().apply {
        moveTo(left + carW * 0.19f, top + carH * 0.46f)
        lineTo(left + carW * 0.26f, top + carH * 0.30f)
        lineTo(left + carW * 0.74f, top + carH * 0.30f)
        lineTo(left + carW * 0.81f, top + carH * 0.46f)
        close()
    }
    drawPath(windshieldPath, color = Color(0xFF0F172A))

    // Sport Fastback Roof
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(left + carW * 0.24f, top + carH * 0.46f),
        size = Size(carW * 0.52f, carH * 0.22f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Sloping Fastback Rear Window
    val fastbackRear = Path().apply {
        moveTo(left + carW * 0.24f, top + carH * 0.68f)
        lineTo(left + carW * 0.76f, top + carH * 0.68f)
        lineTo(left + carW * 0.79f, top + carH * 0.82f)
        lineTo(left + carW * 0.21f, top + carH * 0.82f)
        close()
    }
    drawPath(fastbackRear, color = Color(0xFF0F172A))

    // Minimal Integrated Rear Spoiler Strip
    drawRoundRect(
        color = Color(0xFF1E242B),
        topLeft = Offset(left + carW * 0.14f, top + carH * 0.88f),
        size = Size(carW * 0.72f, 4.5f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Headlights
    val lightW = carW * 0.18f
    drawRoundRect(
        color = Color(0xFFF8FAFC).copy(alpha = pulse),
        topLeft = Offset(left + carW * 0.14f, top + 2f),
        size = Size(lightW, 4.5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = Color(0xFFF8FAFC).copy(alpha = pulse),
        topLeft = Offset(left + carW * 0.68f, top + 2f),
        size = Size(lightW, 4.5f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Continuous Full-Width Rear Taillight Strip
    drawRoundRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(left + carW * 0.16f, top + carH - 4f),
        size = Size(carW * 0.68f, 3f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )
}

// -----------------------------------------------------------------------------------------
// 3. SLOT 3: VINTAGE RED CLASSIC COUPE (Minimalist, Contoured, Proportional)
// -----------------------------------------------------------------------------------------
private fun DrawScope.drawVintageRedCoupe(w: Float, h: Float, pulse: Float, progress: Float) {
    // Proportional dimensions: 1.66 ratio
    val carW = (w * 0.64f).coerceIn(44f, 60f)
    val carH = carW * 1.66f
    val cx = w / 2f
    val cy = h / 2f + (1f - progress) * 6f
    val left = cx - carW / 2f
    val top = cy - carH / 2f

    // Soft Curved Ground Shadow
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left - 4f, top + 5f),
        size = Size(carW + 8f, carH + 7f),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // 4 Wheels with Chrome Hubcaps
    val wheelW = carW * 0.15f
    val wheelH = carH * 0.19f
    listOf(
        Offset(left - wheelW * 0.45f, top + carH * 0.18f),
        Offset(left + carW - wheelW * 0.55f, top + carH * 0.18f),
        Offset(left - wheelW * 0.45f, top + carH * 0.65f),
        Offset(left + carW - wheelW * 0.55f, top + carH * 0.65f)
    ).forEach { pos ->
        drawRoundRect(
            color = Color(0xFF1E242B),
            topLeft = pos,
            size = Size(wheelW, wheelH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(
            color = Color(0xFFE2E8F0), // Chrome hubcap
            radius = wheelW * 0.32f,
            center = Offset(pos.x + wheelW * 0.5f, pos.y + wheelH * 0.5f)
        )
    }

    // Classic Crimson Red Body (Metallic rich gradient)
    val redGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF881337), // Deep wine edge
            Color(0xFFDC2626), // Vibrant red
            Color(0xFFEF4444), // Highlight gloss
            Color(0xFFDC2626),
            Color(0xFF881337)
        ),
        startX = left,
        endX = left + carW
    )
    val bodyRadius = carW * 0.24f
    drawRoundRect(
        brush = redGradient,
        topLeft = Offset(left, top),
        size = Size(carW, carH),
        cornerRadius = CornerRadius(bodyRadius, bodyRadius)
    )

    // Classic Chrome Front Bumper
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(left + carW * 0.15f, top - 1f),
        size = Size(carW * 0.70f, 3.5f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )

    // Vintage Rounded Windshield
    val windshieldPath = Path().apply {
        moveTo(left + carW * 0.20f, top + carH * 0.46f)
        lineTo(left + carW * 0.25f, top + carH * 0.32f)
        lineTo(left + carW * 0.75f, top + carH * 0.32f)
        lineTo(left + carW * 0.80f, top + carH * 0.46f)
        close()
    }
    drawPath(windshieldPath, color = Color(0xFF0F172A))
    drawPath(windshieldPath, color = Color(0xFF94A3B8), style = Stroke(width = 1f))

    // Vintage Rounded Roof
    drawRoundRect(
        color = Color(0xFF991B1B),
        topLeft = Offset(left + carW * 0.23f, top + carH * 0.46f),
        size = Size(carW * 0.54f, carH * 0.22f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Oval Split Rear Window
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(left + carW * 0.28f, top + carH * 0.70f),
        size = Size(carW * 0.44f, carH * 0.10f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Round Classic Headlights (Chrome ring + luminous center)
    val headRadius = carW * 0.08f
    val headCenterY = top + carH * 0.07f
    listOf(
        Offset(left + carW * 0.24f, headCenterY),
        Offset(left + carW * 0.76f, headCenterY)
    ).forEach { center ->
        drawCircle(color = Color(0xFFE2E8F0), radius = headRadius + 1.2f, center = center)
        drawCircle(color = Color(0xFFFFFFFF).copy(alpha = pulse), radius = headRadius, center = center)
    }

    // Classic Chrome Rear Bumper
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(left + carW * 0.15f, top + carH - 2.5f),
        size = Size(carW * 0.70f, 3.5f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )

    // Twin Round Ruby Taillights
    val tailRadius = carW * 0.06f
    val tailCenterY = top + carH * 0.93f
    listOf(
        Offset(left + carW * 0.24f, tailCenterY),
        Offset(left + carW * 0.76f, tailCenterY)
    ).forEach { center ->
        drawCircle(color = Color(0xFFEF4444), radius = tailRadius, center = center)
    }
}
