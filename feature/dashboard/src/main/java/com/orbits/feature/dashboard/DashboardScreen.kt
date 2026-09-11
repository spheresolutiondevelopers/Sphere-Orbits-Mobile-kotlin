package com.orbits.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.dashboard.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMeetings: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var focusedOrbitItem by remember { mutableStateOf<OrbitItem?>(null) }

    // Handle initial refresh
    LaunchedEffect(Unit) {
        viewModel.handleEvent(DashboardEvent.Refresh)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // ─── Welcome Header ──────────────────────────────
                item {
                    WelcomeHeader(
                        userName = state.user?.displayName,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // ─── Error Message ──────────────────────────────
                state.refreshError?.let { error ->
                    item {
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.handleEvent(DashboardEvent.DismissError) }) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }

                // ─── Focus Ticker ────────────────────────────────
                if (state.topInsight != null) {
                    item {
                        FocusTicker(
                            insights = listOf(state.topInsight!!),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ─── Orbit Hub ──────────────────────────────────
                item {
                    OrbitHub(
                        onItemClick = { title ->
                            when (title) {
                                "Tasks" -> onNavigateToTasks()
                                "Meetings" -> onNavigateToMeetings()
                                "Calendar" -> onNavigateToCalendar()
                                "Analytics" -> onNavigateToAnalytics()
                                "Appointments" -> onNavigateToAppointments()
                                "Notes" -> onNavigateToNotes()
                                "Chat" -> onNavigateToChat()
                            }
                        },
                        onFocusedItemChanged = { focusedOrbitItem = it }
                    )
                }

                // ─── Active Feature Card ────────────────────────
                focusedOrbitItem?.let { item ->
                    item {
                        OrbitFeatureCard(
                            item = item,
                            onClick = {
                                when (item.title) {
                                    "Tasks" -> onNavigateToTasks()
                                    "Meetings" -> onNavigateToMeetings()
                                    "Calendar" -> onNavigateToCalendar()
                                    "Analytics" -> onNavigateToAnalytics()
                                    "Appointments" -> onNavigateToAppointments()
                                    "Notes" -> onNavigateToNotes()
                                    "Chat" -> onNavigateToChat()
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // ─── Calendar Strip ─────────────────────────────
                item {
                    CalendarStrip(
                        selectedDate = state.selectedDate,
                        onDateSelected = { date ->
                            viewModel.handleEvent(DashboardEvent.SelectDate(date))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // ─── Upcoming Tasks ─────────────────────────────
                item {
                    TaskSummaryCard(
                        tasks = state.upcomingTasks,
                        title = "Upcoming Tasks",
                        emptyMessage = "No upcoming tasks",
                        onTaskClick = onNavigateToTaskDetail,
                        onViewAll = onNavigateToTasks,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // ─── Weekly Progress ────────────────────────────
                item {
                    ProgressRing(
                        stats = state.dashboardStats,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
