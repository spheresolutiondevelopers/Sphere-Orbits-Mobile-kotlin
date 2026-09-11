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
fun TaskCategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (category.lowercase()) {
        "meeting" -> Triple(Color(0xFF082830), Color(0xFF22D3EE), Color(0xFF0E4050))
        "job" -> Triple(Color(0xFF181200), Color(0xFFFFD166), Color(0xFF282000))
        "personal" -> Triple(Color(0xFF10103A), Color(0xFFF472B6), Color(0xFF18185A))
        "event" -> Triple(Color(0xFF1C1C6A), Color(0xFFFF6B8A), Color(0xFF13134A))
        "appointment" -> Triple(Color(0xFF082818), Color(0xFF2DD4A0), Color(0xFF0E3824))
        else -> Triple(SphereTheme.colors.cardTertiary, SphereTheme.colors.textSecondary, SphereTheme.colors.borderSecondary)
    }

    Box(
        modifier = modifier
            .background(bgColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
