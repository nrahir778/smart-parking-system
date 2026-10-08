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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GateState
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.NeonEmerald

@Composable
fun GateBoomBarrierCard(
    gateState: GateState,
    buzzerAlert: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // 0 deg = OPEN (pointing up), 90 deg = CLOSED (horizontal across lane)
    val targetAngle = if (gateState == GateState.CLOSED) 0f else -75f
    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "barrierAngle"
    )

    // Buzzer warning pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "buzzer_infinite")
    val buzzerPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buzzerPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (gateState == GateState.CLOSED) CrimsonRed.copy(alpha = 0.6f) else NeonEmerald.copy(alpha = 0.4f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
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
                            .background(
                                if (gateState == GateState.CLOSED) CrimsonRed.copy(alpha = 0.2f)
                                else NeonEmerald.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (gateState == GateState.CLOSED) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Gate Status",
                            tint = if (gateState == GateState.CLOSED) CrimsonRed else NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MG995 Entry Barrier Gate",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (gateState == GateState.CLOSED) "Status: CLOSED (Servo 90°)" else "Status: OPEN (Servo 0°)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (gateState == GateState.CLOSED) CrimsonRed else NeonEmerald
                        )
                    }
                }

                // Buzzer Pin Indicator
                if (buzzerAlert) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CrimsonRed.copy(alpha = buzzerPulse * 0.35f))
                            .border(1.dp, CrimsonRed.copy(alpha = buzzerPulse), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Buzzer",
                            tint = CrimsonRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BUZZER ON",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonRed
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Buzzer: Normal",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Canvas of Road & Barrier Gate Arm
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF0F131C) else Color(0xFF1E2430))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(95.dp)) {
                    val w = size.width
                    val h = size.height

                    // Draw asphalt road markings
                    drawRect(color = Color(0xFF141822), size = Size(w, h))

                    // Road center dashed lines
                    val dashW = 24f
                    val gapW = 16f
                    var curX = 0f
                    while (curX < w) {
                        drawLine(
                            color = Color(0x33E2E8F0),
                            start = Offset(curX, h * 0.75f),
                            end = Offset(curX + dashW, h * 0.75f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                        curX += (dashW + gapW)
                    }

                    // Stop line on the road
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(w * 0.35f, h * 0.35f),
                        end = Offset(w * 0.35f, h * 0.95f),
                        strokeWidth = 6f
                    )

                    // Barrier Motor Base Post (Pin 11 Servo enclosure)
                    val postX = w * 0.22f
                    val postY = h * 0.30f
                    val postW = 34f
                    val postH = 48f

                    // Post shadow
                    drawRoundRect(
                        color = Color(0x66000000),
                        topLeft = Offset(postX - 2f, postY + postH - 4f),
                        size = Size(postW + 8f, 10f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Post body (industrial safety yellow & black)
                    drawRoundRect(
                        color = Color(0xFFF59E0B),
                        topLeft = Offset(postX, postY),
                        size = Size(postW, postH),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    // Post grill
                    drawRoundRect(
                        color = Color(0xFF18181B),
                        topLeft = Offset(postX + 4f, postY + 8f),
                        size = Size(postW - 8f, postH - 16f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    // Pivot axle center
                    val pivot = Offset(postX + postW / 2f, postY + 14f)
                    drawCircle(color = Color(0xFFCBD5E1), radius = 8f, center = pivot)

                    // LED Signal on Post
                    val ledColor = if (gateState == GateState.CLOSED) CrimsonRed else NeonEmerald
                    drawCircle(color = ledColor, radius = 5f, center = Offset(postX + postW / 2f, postY + postH - 8f))
                    drawCircle(
                        color = ledColor.copy(alpha = 0.35f),
                        radius = 11f,
                        center = Offset(postX + postW / 2f, postY + postH - 8f)
                    )

                    // Barrier Arm (Rotates around pivot)
                    rotate(degrees = animatedAngle, pivot = pivot) {
                        val armLen = w * 0.60f
                        val armThickness = 10f
                        val armTop = pivot.y - armThickness / 2f
                        val armLeft = pivot.x

                        // Draw arm base background
                        drawRoundRect(
                            color = Color(0xFFF8FAFC),
                            topLeft = Offset(armLeft, armTop),
                            size = Size(armLen, armThickness),
                            cornerRadius = CornerRadius(3f, 3f)
                        )

                        // Draw alternating red safety stripes
                        val stripeW = 20f
                        var stripeX = armLeft + 10f
                        while (stripeX < armLeft + armLen - 10f) {
                            drawRoundRect(
                                color = CrimsonRed,
                                topLeft = Offset(stripeX, armTop),
                                size = Size(stripeW, armThickness),
                                cornerRadius = CornerRadius(0f, 0f)
                            )
                            stripeX += (stripeW * 2f)
                        }

                        // Tip reflector
                        drawCircle(
                            color = if (gateState == GateState.CLOSED) CrimsonRed else NeonEmerald,
                            radius = 6f,
                            center = Offset(armLeft + armLen, pivot.y)
                        )
                    }
                }
            }
        }
    }
}
