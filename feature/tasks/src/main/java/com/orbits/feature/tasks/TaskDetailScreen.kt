package com.orbits.feature.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.domain.tasks.Task
import com.orbits.feature.tasks.components.TaskPriorityChip
import com.orbits.feature.tasks.components.TaskStatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    viewModel: TaskViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToEdit: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    // Finding task in current state list. In production, might fetch specifically.
    val task = state.filteredTasks.find { it.id == taskId } ?: state.tasks.find { it.id == taskId }
    val isLoading = state.isLoading

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (task == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Task not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── Screen Actions ──────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!task.isCompleted()) {
                        IconButton(onClick = {
                            viewModel.handleEvent(TaskEvent.CompleteTask(taskId))
                        }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Complete",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onNavigateToEdit(task) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                    IconButton(onClick = {
                        viewModel.handleEvent(TaskEvent.DeleteTask(taskId))
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ─── Title ─────────────────────────────────────
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (task.isCompleted()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )

                    // ─── Status & Priority ─────────────────────────
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskStatusChip(status = task.status)
                        TaskPriorityChip(priority = task.priorityLevel)
                    }

                    // ─── Due Date ──────────────────────────────────
                    if (task.dueDate != null) {
                        InfoRow(
                            label = "Due Date",
                            value = "${task.dueDate}${if (task.dueTime != null) " at ${task.dueTime}" else ""}"
                        )
                    }

                    // ─── Description ──────────────────────────────
                    if (task.description != null) {
                        HorizontalDivider()
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = task.description ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // ─── Location ──────────────────────────────────
                    if (task.locationName != null) {
                        InfoRow(
                            label = "Location",
                            value = task.locationName ?: ""
                        )
                    }

                    // ─── Tags ──────────────────────────────────────
                    if (task.tags.isNotEmpty()) {
                        InfoRow(
                            label = "Tags",
                            value = task.tags.joinToString(", ")
                        )
                    }

                    // ─── Notes ─────────────────────────────────────
                    if (task.notes != null) {
                        HorizontalDivider()
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = task.notes ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // ─── Metadata ──────────────────────────────────
                    HorizontalDivider()
                    Text(
                        text = "Created: ${task.createdAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Last Updated: ${task.updatedAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
