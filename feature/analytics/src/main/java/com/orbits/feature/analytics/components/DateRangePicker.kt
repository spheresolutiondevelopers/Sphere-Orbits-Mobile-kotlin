package com.orbits.feature.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.feature.analytics.AnalyticsPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    selectedPeriod: AnalyticsPeriod,
    startDate: String,
    endDate: String,
    onPeriodSelected: (AnalyticsPeriod) -> Unit,
    onDateRangeSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Period chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(AnalyticsPeriod.values()) { period ->
                FilterChip(
                    selected = period == selectedPeriod,
                    onClick = { onPeriodSelected(period) },
                    label = { Text(period.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Custom date range (shown when Custom is selected)
        if (selectedPeriod == AnalyticsPeriod.CUSTOM) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { onDateRangeSelected(it, endDate) },
                    label = { Text("Start") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("YYYY-MM-DD") }
                )
                Text(
                    text = "to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { onDateRangeSelected(startDate, it) },
                    label = { Text("End") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("YYYY-MM-DD") }
                )
            }
        }
    }
}
