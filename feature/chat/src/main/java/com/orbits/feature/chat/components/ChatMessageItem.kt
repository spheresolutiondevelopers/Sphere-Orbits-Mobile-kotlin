package com.orbits.feature.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.orbits.domain.chat.Message

@Composable
fun ChatMessageItem(
    message: Message,
    isFromCurrentUser: Boolean,
    showAvatar: Boolean = true,
    modifier: Modifier = Modifier
) {
    val alignment = if (isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isFromCurrentUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isFromCurrentUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        // Avatar for other user
        if (!isFromCurrentUser && showAvatar) {
            ChatAvatar(
                name = message.senderUserId,
                size = 32.dp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Surface(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isFromCurrentUser) 16.dp else 4.dp,
                            topEnd = if (isFromCurrentUser) 4.dp else 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    ),
                color = backgroundColor
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (message.contentType != "text") {
                        Text(
                            text = "📎 ${message.contentType}",
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor
                        )
                    }
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }

            // Timestamp
            Text(
                text = message.getFormattedTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Avatar for current user (right side)
        if (isFromCurrentUser && showAvatar) {
            ChatAvatar(
                name = message.senderUserId,
                size = 32.dp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
