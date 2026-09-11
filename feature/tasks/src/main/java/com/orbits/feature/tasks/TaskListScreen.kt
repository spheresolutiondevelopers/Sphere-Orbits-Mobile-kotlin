package com.orbits.feature.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.core.theme.SphereTheme
import com.orbits.feature.tasks.components.*

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isMobile = maxWidth < 640.dp
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── FILTER BAR ──────────────────────────────────────
            TaskFilterBar(
                state = state,
                onFilterSelected = { viewModel.handleEvent(TaskEvent.SelectFilter(it)) }
            )

            // ─── SEARCH & ACTIONS ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SphereTheme.colors.backgroundSecondary, RoundedCornerShape(50.dp))
                        .border(1.dp, SphereTheme.colors.borderSecondary, RoundedCornerShape(50.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SphereTheme.colors.textTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.handleEvent(TaskEvent.Search(it)) },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (state.searchQuery.isEmpty()) {
                                    Text(
                                        "Search tasks...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SphereTheme.colors.textTertiary
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }
                
                Surface(
                    onClick = { /* Sort logic */ },
                    color = SphereTheme.colors.cardSecondary,
                    shape = RoundedCornerShape(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SphereTheme.colors.borderSecondary)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(14.dp)) // Sort icon would be better
                        Text("Sort", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // ─── Error Message ──────────────────────────────────
            if (state.errorMessage != null) {
                // Existing error UI
            }

            // ─── Task Content ────────────────────────────────────
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredTasks.isEmpty()) {
                TaskEmptyState(modifier = Modifier.weight(1f))
            } else {
                if (isMobile) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(state.filteredTasks) { task ->
                            TaskMobileCard(
                                task = task,
                                onTaskClick = onNavigateToTaskDetail,
                                onCompleteClick = { viewModel.handleEvent(TaskEvent.CompleteTask(it)) }
                            )
                        }
                    }
                } else {
                    TaskTable(
                        tasks = state.filteredTasks,
                        onTaskClick = onNavigateToTaskDetail,
                        onCompleteClick = { viewModel.handleEvent(TaskEvent.CompleteTask(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        TaskFab(
            onClick = onNavigateToCreate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
