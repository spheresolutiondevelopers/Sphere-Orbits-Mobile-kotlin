package com.orbits.feature.meetings.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RowScope.JoinMeetingButton(
    onClick: () -> Unit,
    isLive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isLive) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = null
        )
        Text(if (isLive) " Join Live" else " Join")
    }
}
