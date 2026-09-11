package com.orbits.feature.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.domain.notes.Note

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPinToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noteColor = getNoteColor(note.color)
    val bgColor = noteColor.copy(alpha = 0.08f)
    val borderColor = noteColor.copy(alpha = 0.2f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Actions (Top Right)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NoteActionIcon(
                        imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        onClick = onPinToggle,
                        tint = if (note.isPinned) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    NoteActionIcon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        onClick = onClick
                    )
                    NoteActionIcon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        onClick = onDelete,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }

                if (!note.title.isNullOrBlank()) {
                    Text(
                        text = note.title!!,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = noteColor
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 60.dp) // Leave space for actions
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    ),
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )

                if (note.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        note.tags.forEach { tag ->
                            NoteTagChip(tag = tag)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(11.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = note.getFormattedUpdatedAt(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                    
                    if (note.eventId != null || note.taskId != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin, // Placeholder for calendar-star icon
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Linked",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteActionIcon(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(7.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(10.dp),
            tint = tint
        )
    }
}

private fun getNoteColor(colorName: String?): Color {
    return when (colorName?.lowercase()) {
        "violet" -> Color(0xFFA78BFA)
        "rose" -> Color(0xFFFB7185)
        "amber" -> Color(0xFFFBBF24)
        "teal" -> Color(0xFF2DD4A0)
        "sky" -> Color(0xFF38BDF8)
        "pink" -> Color(0xFFF472B6)
        "lime" -> Color(0xFF84CC16)
        "orange" -> Color(0xFFFB923C)
        else -> Color(0xFFA78BFA) // Default to violet
    }
}

