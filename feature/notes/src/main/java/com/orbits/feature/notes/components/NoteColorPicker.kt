package com.orbits.feature.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteColorPicker(
    selectedColor: String?,
    onColorSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        "violet" to Color(0xFFA78BFA),
        "rose" to Color(0xFFFB7185),
        "amber" to Color(0xFFFBBF24),
        "teal" to Color(0xFF2DD4A0),
        "sky" to Color(0xFF38BDF8),
        "pink" to Color(0xFFF472B6),
        "lime" to Color(0xFF84CC16),
        "orange" to Color(0xFFFB923C)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            text = "Color",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                color = Color(0xFF5E5C88)
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(colors) { (colorName, color) ->
                val isSelected = selectedColor == colorName
                
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onColorSelected(colorName) }
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
