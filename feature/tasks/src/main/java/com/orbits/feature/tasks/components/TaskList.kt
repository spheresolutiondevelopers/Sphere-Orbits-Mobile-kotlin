package com.orbits.feature.tasks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.tasks.Task

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onCompleteClick: (String) -> Unit,
    onMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        TaskEmptyState(
            message = "No tasks found",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = { onTaskClick(task.id) },
                    onCompleteClick = { onCompleteClick(task.id) },
                    onMenuClick = { onMenuClick(task.id) }
                )
            }
        }
    }
}
