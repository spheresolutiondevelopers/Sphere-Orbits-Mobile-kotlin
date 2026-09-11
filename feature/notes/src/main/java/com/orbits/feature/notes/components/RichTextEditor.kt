package com.orbits.feature.notes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RichTextEditor(
    content: String,
    onContentChange: (String) -> Unit,
    isPreviewMode: Boolean,
    modifier: Modifier = Modifier
) {
    if (isPreviewMode) {
        // Preview mode - render markdown/HTML
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            // In production, use a markdown renderer library
            // For now, we just show the raw content
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        // Edit mode
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 500.dp),
            placeholder = { Text("Write your note here...") },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors()
        )
    }
}
