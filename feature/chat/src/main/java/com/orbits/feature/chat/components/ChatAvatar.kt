package com.orbits.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChatAvatar(
    name: String,
    size: Dp = 40.dp,
    isGroup: Boolean = false,
    modifier: Modifier = Modifier
) {
    val initial = if (isGroup) {
        name.take(2).uppercase()
    } else {
        name.take(1).uppercase()
    }

    // Generate a consistent color from the name
    val color = Color(
        red = (name.hashCode() % 200 + 55) / 255f,
        green = ((name.hashCode() / 100) % 200 + 55) / 255f,
        blue = ((name.hashCode() / 10000) % 200 + 55) / 255f,
        alpha = 1f
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White
        )
    }
}
