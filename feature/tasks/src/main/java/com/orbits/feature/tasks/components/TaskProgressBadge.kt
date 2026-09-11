package com.orbits.feature.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbits.core.theme.SphereTheme

@Composable
fun TaskProgressBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor, label) = when (status.lowercase()) {
        "completed" -> Triple(Color(0xFF082818), Color(0xFF2DD4A0), Color(0xFF0E3824)) + "Done"
        "in_progress" -> Triple(Color(0xFF181200), Color(0xFFFFD166), Color(0xFF282000)) + "WIP"
        "cancelled" -> Triple(Color(0xFF1C1C6A), Color(0xFFFF6B8A), Color(0xFF13134A)) + "Off"
        else -> Triple(Color(0xFF082830), Color(0xFF22D3EE), Color(0xFF0E4050)) + "Open"
    }

    Box(
        modifier = modifier
            .background(bgColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// Helper to add String to Triple
private operator fun <A, B, C> Triple<A, B, C>.plus(d: String): Quadruple<A, B, C, String> =
    Quadruple(first, second, third, d)

private data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
