package com.orbits.feature.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orbits.core.theme.SphereTheme
import com.orbits.feature.tasks.TaskFilter
import com.orbits.feature.tasks.TasksUiState

@Composable
fun TaskFilterBar(
    state: TasksUiState,
    onFilterSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedFilter = state.selectedFilter
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SphereTheme.colors.cardSecondary, RoundedCornerShape(16.dp))
            .border(1.5.dp, SphereTheme.colors.borderSecondary, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                TaskFilterPill(
                    label = "Category",
                    value = "All",
                    icon = Icons.Default.Folder,
                    iconColor = Color(0xFF8080C8),
                    count = state.tasks.size,
                    isActive = false,
                    onClick = { /* In a real app, show dropdown */ }
                )
            }
            
            item { 
                VerticalDivider(
                    modifier = Modifier.height(20.dp).width(1.dp), 
                    color = SphereTheme.colors.borderSecondary
                ) 
            }

            item {
                val priorityVal = if (selectedFilter == TaskFilter.HIGH_PRIORITY || selectedFilter == TaskFilter.CRITICAL) 
                    selectedFilter.displayName else "All"
                
                val priorityKey = when (selectedFilter) {
                    TaskFilter.HIGH_PRIORITY -> "high"
                    TaskFilter.CRITICAL -> "critical"
                    else -> null
                }
                
                val priorityCount = if (priorityKey == null) 
                    state.priorityCounts.values.sum() 
                else 
                    state.priorityCounts[priorityKey] ?: 0

                TaskFilterPill(
                    label = "Priority",
                    value = priorityVal,
                    icon = Icons.Default.Bolt,
                    iconColor = Color(0xFFFFD166),
                    count = priorityCount,
                    isActive = selectedFilter == TaskFilter.HIGH_PRIORITY || selectedFilter == TaskFilter.CRITICAL,
                    onClick = { 
                        if (selectedFilter == TaskFilter.HIGH_PRIORITY) onFilterSelected(TaskFilter.CRITICAL)
                        else onFilterSelected(TaskFilter.HIGH_PRIORITY)
                    }
                )
            }

            item { 
                VerticalDivider(
                    modifier = Modifier.height(20.dp).width(1.dp), 
                    color = SphereTheme.colors.borderSecondary
                ) 
            }

            item {
                val dateVal = if (selectedFilter == TaskFilter.TODAY || selectedFilter == TaskFilter.UPCOMING || selectedFilter == TaskFilter.OVERDUE)
                    selectedFilter.displayName else "All"
                val dateCount = when(dateVal) {
                    "Today" -> state.dateCounts["Today"] ?: 0
                    "Upcoming" -> state.dateCounts["Upcoming"] ?: 0
                    else -> state.tasks.size
                }

                TaskFilterPill(
                    label = "Date",
                    value = dateVal,
                    icon = Icons.Default.CalendarToday,
                    iconColor = Color(0xFF22D3EE),
                    count = dateCount,
                    isActive = selectedFilter == TaskFilter.TODAY || selectedFilter == TaskFilter.UPCOMING || selectedFilter == TaskFilter.OVERDUE,
                    onClick = {
                        if (selectedFilter == TaskFilter.TODAY) onFilterSelected(TaskFilter.UPCOMING)
                        else onFilterSelected(TaskFilter.TODAY)
                    }
                )
            }

            item { 
                VerticalDivider(
                    modifier = Modifier.height(20.dp).width(1.dp), 
                    color = SphereTheme.colors.borderSecondary
                ) 
            }

            item {
                val progVal = if (selectedFilter == TaskFilter.COMPLETED || selectedFilter == TaskFilter.IN_PROGRESS)
                    selectedFilter.displayName else "All"
                val progCount = if (progVal == "All")
                    state.progressCounts.values.sum()
                else
                    state.progressCounts[if (selectedFilter == TaskFilter.COMPLETED) "completed" else "in_progress"] ?: 0

                TaskFilterPill(
                    label = "Progress",
                    value = progVal,
                    icon = Icons.Default.DonutLarge,
                    iconColor = Color(0xFF2DD4A0),
                    count = progCount,
                    isActive = selectedFilter == TaskFilter.COMPLETED || selectedFilter == TaskFilter.IN_PROGRESS,
                    onClick = {
                        if (selectedFilter == TaskFilter.COMPLETED) onFilterSelected(TaskFilter.IN_PROGRESS)
                        else onFilterSelected(TaskFilter.COMPLETED)
                    }
                )
            }

            if (selectedFilter != TaskFilter.ALL) {
                item {
                    TextButton(
                        onClick = { onFilterSelected(TaskFilter.ALL) },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF6B8A)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Clear all", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
