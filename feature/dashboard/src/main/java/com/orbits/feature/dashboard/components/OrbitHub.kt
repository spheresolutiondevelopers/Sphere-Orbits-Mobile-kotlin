package com.orbits.feature.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.core.theme.*
import kotlin.math.*

@Composable
fun OrbitHub(
    onItemClick: (String) -> Unit,
    onFocusedItemChanged: (OrbitItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            OrbitItem("✅", "Tasks", "18 tasks today · 8 done · 3 urgent pending", SphereBase),
            OrbitItem("👥", "Meetings", "4 meetings today · Next in 45 min", SphereGreen),
            OrbitItem("📅", "Calendar", "3 events this week · Sprint Planning tomorrow", AccentBase),
            OrbitItem("⏰", "Reminders", "2 reminders due · Standup at 9:00 AM", SphereYellow),
            OrbitItem("📊", "Analytics", "92% productivity score · Best day this week", Color(0xFF22D3EE)),
            OrbitItem("🔔", "Alerts", "5 unread notifications · 1 urgent action needed", Color(0xFFF472B6)),
            OrbitItem("🤖", "AI Assist", "Smart scheduling active · 3 suggestions ready", Color(0xFFC084FC)),
            OrbitItem("📋", "Appt", "5 appointments this month · 1-on-1 today at 3PM", Color(0xFFFB923C)),
            OrbitItem("📝", "Notes", "12 saved notes · 2 pinned · Last edited today", Color(0xFF34D399)),
            OrbitItem("💬", "Chat", "3 unread messages · Team standup thread active", Color(0xFF818CF8))
        )
    }

    var lastFocusedIndex by remember { mutableStateOf(-1) }

    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val orbitA = with(density) { 160.dp.toPx() }
        val orbitB = with(density) { 100.dp.toPx() }

        // ─── Orbit Path ─────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            
            val path = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(cx - orbitA, cy - orbitB, cx + orbitA, cy + orbitB))
            }
            
            drawPath(
                path = path,
                color = SphereBase.copy(alpha = 0.15f),
                style = Stroke(
                    width = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 25f), 0f)
                )
            )
        }

        // ─── Central Sphere ──────────────────────────────────
        SphereInvestmentCenter(
            modifier = Modifier.size(160.dp, 120.dp)
        )

        // ─── Planets ────────────────────────────────────────
        items.forEachIndexed { index, item ->
            val angle = orbitAngle + (index.toFloat() / items.size) * 2f * PI.toFloat() + PI.toFloat() / 2f
            val x = orbitA * cos(angle)
            val y = orbitB * sin(angle)

            // Determine if this planet is at the "bottom" (focused)
            val normalizedAngle = (angle % (2f * PI.toFloat()) + 2f * PI.toFloat()) % (2f * PI.toFloat())
            val diff = abs(normalizedAngle - PI.toFloat() / 2f)
            
            // We use a threshold to decide which one is "most bottom"
            // In tick(), we'd find the min. Here we can use a smaller threshold for display.
            
            // For the description card, we need to report the one with min diff.
            // Since this is a Composable, we'll calculate the min diff among all items.
            
            Box(
                modifier = Modifier
                    .offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onItemClick(item.title) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(item.color.copy(alpha = 0.8f), item.color),
                                    center = androidx.compose.ui.geometry.Offset(15f, 15f)
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.icon, fontSize = 18.sp)
                    }
                    Text(
                        text = item.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
        
        // Logic to report focused item
        LaunchedEffect(orbitAngle) {
            var minD = Float.MAX_VALUE
            var bottomIdx = 0
            items.forEachIndexed { index, _ ->
                val angle = orbitAngle + (index.toFloat() / items.size) * 2f * PI.toFloat() + PI.toFloat() / 2f
                val normalizedAngle = (angle % (2f * PI.toFloat()) + 2f * PI.toFloat()) % (2f * PI.toFloat())
                val d = abs(normalizedAngle - PI.toFloat() / 2f)
                if (d < minD) {
                    minD = d
                    bottomIdx = index
                }
            }
            if (bottomIdx != lastFocusedIndex) {
                lastFocusedIndex = bottomIdx
                onFocusedItemChanged(items[bottomIdx])
            }
        }
    }
}

@Composable
fun SphereInvestmentCenter(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sphere")
    val rotY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val a = 62.dp.toPx()
        val b = 44.dp.toPx()
        
        // Clip to ellipse
        val clipPath = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(cx - a, cy - b, cx + a, cy + b))
        }
        
        clipPath(clipPath) {
            // Base teal fill
            drawRect(
                color = Color(0xFF37AFC8).copy(alpha = 0.95f),
                size = size
            )
            
            // Draw checkerboard (simplified for performance)
            val step = 15f
            for (lat in -90..90 step 15) {
                for (lng in -180..180 step 15) {
                    val la = lat * PI.toFloat() / 180f
                    val lo = (lng.toFloat() + rotY * 180f / PI.toFloat()) * PI.toFloat() / 180f
                    
                    val z = a * cos(la) * sin(lo)
                    if (z > -0.08f) {
                        val x = a * cos(la) * cos(lo)
                        val y = b * sin(la)
                        
                        val l = 0.45f + (z / a) * 0.65f
                        val isEven = ((lat / 15) + (lng / 15)) % 2 == 0
                        val color = if (isEven) {
                            Color(
                                (80 + l * 45).toInt(),
                                (195 + l * 28).toInt(),
                                (215 + l * 15).toInt()
                            )
                        } else {
                            Color(
                                (30 + l * 38).toInt(),
                                (148 + l * 28).toInt(),
                                (172 + l * 18).toInt()
                            )
                        }
                        
                        // Draw a small cell representing the grid point
                        // Real checkerboard involves drawing polygons between points
                        // Here we approximate with points/small rects for simplicity in this draft
                        drawRect(
                            color = color,
                            topLeft = androidx.compose.ui.geometry.Offset(cx + x - 4f, cy - y - 4f),
                            size = androidx.compose.ui.geometry.Size(8f, 8f)
                        )
                    }
                }
            }
        }
        
        // Border
        drawPath(
            path = clipPath,
            color = Color(0xFFD2F8FF).copy(alpha = 0.85f),
            style = Stroke(width = 1.5.dp.toPx())
        )
        
        // Specular highlight
        val specularGradient = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.55f), Color(0xFFD2F8FF).copy(alpha = 0.15f), Color.Transparent),
            center = androidx.compose.ui.geometry.Offset(cx - a * 0.38f, cy - b * 0.36f),
            radius = a * 0.46f
        )
        clipPath(clipPath) {
            drawRect(brush = specularGradient, size = size)
        }
        
        // Rim glow
        val rimGradient = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xFF8CEBFF).copy(alpha = 0.2f), Color.Transparent),
            center = androidx.compose.ui.geometry.Offset(cx, cy),
            radius = a * 1.15f
        )
        drawCircle(brush = rimGradient, radius = a * 1.15f, center = androidx.compose.ui.geometry.Offset(cx, cy))
        
        // "SPHERE" Letters (approximation)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 20.dp.toPx()
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
            setShadowLayer(3f, 1f, 1f, android.graphics.Color.argb(180, 2, 5, 30))
        }
        
        drawContext.canvas.nativeCanvas.drawText(
            "SPHERE",
            cx,
            cy - b * 0.22f,
            textPaint
        )
        
        // Grass (simple) approximation
        val oy = cy + b * 0.78f
        val grassPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#587324")
            alpha = 190
        }
        
        clipPath(clipPath) {
            drawCircle(
                color = Color(0xFF587324).copy(alpha = 0.75f),
                radius = a * 0.5f,
                center = androidx.compose.ui.geometry.Offset(cx, oy + 20f)
            )
        }
        
        // "ACT"
        val actPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#145214")
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 9.dp.toPx()
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        
        drawContext.canvas.nativeCanvas.drawText(
            "ACT",
            cx,
            oy + 10.dp.toPx(),
            actPaint
        )
    }
}
