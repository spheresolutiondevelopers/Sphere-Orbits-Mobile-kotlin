package com.orbits.feature.analytics.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.orbits.feature.analytics.ReportFormat

@Composable
fun ReportExportButton(
    onExport: (ReportFormat) -> Unit,
    isGenerating: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            enabled = !isGenerating,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = null
            )
            Text(if (isGenerating) "Generating..." else "Export Report")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ReportFormat.values().forEach { format ->
                DropdownMenuItem(
                    text = { Text(format.displayName) },
                    onClick = {
                        expanded = false
                        onExport(format)
                    }
                )
            }
        }
    }
}
