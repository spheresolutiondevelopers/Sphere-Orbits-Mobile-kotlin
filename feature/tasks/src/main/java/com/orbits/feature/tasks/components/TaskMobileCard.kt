package com.orbits.feature.tasks.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
fun TaskMobileCard(
    task: Task,
    onTaskClick: (String) -> Unit,
    onCompleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onTaskClick(task.id) },
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, SphereTheme.colors.border, RoundedCornerShape(0.dp)),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(end = 48.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted()) TextDecoration.LineThrough else null
                    ),
                    color = if (task.isCompleted()) SphereTheme.colors.textTertiary else Color.White
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    TaskCategoryBadge(task.taskType)
                    TaskPriorityBadge(task.priorityLevel)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = SphereTheme.colors.textTertiary
                    )
                    Text(
                        text = task.dueDate ?: "No due date",
                        style = MaterialTheme.typography.labelSmall,
                        color = SphereTheme.colors.textSecondary
                    )
                }
            }

            Checkbox(
                checked = task.isCompleted(),
                onCheckedChange = { onCompleteClick(task.id) },
                modifier = Modifier.align(Alignment.TopEnd),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = SphereTheme.colors.borderSecondary,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}
