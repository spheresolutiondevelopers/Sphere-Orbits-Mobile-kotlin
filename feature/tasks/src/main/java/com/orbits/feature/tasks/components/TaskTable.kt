package com.orbits.feature.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.orbits.core.theme.SphereTheme
import com.orbits.domain.tasks.Task

@Composable
fun TaskTable(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onCompleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SphereTheme.colors.backgroundSecondary,
        border = androidx.compose.foundation.BorderStroke(1.dp, SphereTheme.colors.borderSecondary)
    ) {
        Column {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(18.dp)) // Checkbox space
                Spacer(Modifier.width(12.dp))
                
                Text("TITLE", Modifier.weight(2f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SphereTheme.colors.textTertiary)
                Text("CATEGORY", Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SphereTheme.colors.textTertiary)
                Text("PRIORITY", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SphereTheme.colors.textTertiary)
                Text("DATE", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SphereTheme.colors.textTertiary)
                Text("PROGRESS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SphereTheme.colors.textTertiary)
            }
            
            HorizontalDivider(color = SphereTheme.colors.borderSecondary)
            
            LazyColumn(modifier = Modifier.heightIn(max = 600.dp)) {
                items(tasks) { task ->
                    TaskTableRow(task, onTaskClick, onCompleteClick)
                }
            }
        }
    }
}

@Composable
fun TaskTableRow(
    task: Task,
    onTaskClick: (String) -> Unit,
    onCompleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onTaskClick(task.id) },
        modifier = modifier
            .fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted(),
                    onCheckedChange = { onCompleteClick(task.id) },
                    modifier = Modifier.size(18.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = SphereTheme.colors.borderSecondary
                    )
                )
                
                Spacer(Modifier.width(12.dp))
                
                // Title
                Text(
                    text = task.title,
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted()) TextDecoration.LineThrough else null
                    ),
                    color = if (task.isCompleted()) SphereTheme.colors.textTertiary else Color.White
                )
                
                // Category
                Box(modifier = Modifier.weight(1.2f)) {
                    TaskCategoryBadge(task.taskType)
                }
                
                // Priority
                Box(modifier = Modifier.weight(1f)) {
                    TaskPriorityBadge(task.priorityLevel)
                }
                
                // Date
                Text(
                    text = task.dueDate ?: "-",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = SphereTheme.colors.textSecondary
                )
                
                // Progress
                Box(modifier = Modifier.weight(1f)) {
                    TaskProgressBadge(task.status)
                }
            }
            HorizontalDivider(color = SphereTheme.colors.border, thickness = 0.5.dp)
        }
    }
}
