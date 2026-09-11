package com.orbits.feature.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.analytics.components.*

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // ─── Error Message ──────────────────────────────
                if (state.errorMessage != null) {
                    item {
                        Card(
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
                                    text = state.errorMessage!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { viewModel.handleEvent(AnalyticsEvent.DismissError) }
                                ) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }

                // ─── Date Range Picker ──────────────────────────
                item {
                    DateRangePicker(
                        selectedPeriod = state.selectedPeriod,
                        startDate = state.startDate,
                        endDate = state.endDate,
                        onPeriodSelected = { viewModel.handleEvent(AnalyticsEvent.SelectPeriod(it)) },
                        onDateRangeSelected = { start, end ->
                            viewModel.handleEvent(AnalyticsEvent.SetDateRange(start, end))
                        }
                    )
                }

                // ─── Productivity Score ──────────────────────────
                item {
                    val score = state.stats?.productivityScore ?: 0
                    ProductivityScore(
                        score = score,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ─── Trend Indicator ─────────────────────────────
                if (state.stats != null) {
                    item {
                        TrendIndicator(
                            trend = state.stats?.trend ?: "stable",
                            value = state.stats?.getTrendDisplay() ?: "",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ─── Stats Row ──────────────────────────────────
                if (state.stats != null) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Tasks",
                                value = state.stats?.tasksCompleted?.toString() ?: "0",
                                subtitle = "of ${state.stats?.tasksCreated ?: 0} created",
                                icon = "📋",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Rate",
                                value = state.stats?.getFormattedCompletionRate() ?: "0%",
                                icon = "🎯",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Focus",
                                value = state.stats?.getFormattedFocusHours() ?: "0h",
                                icon = "⏱️",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ─── Insights ────────────────────────────────────
                if (state.insights.isNotEmpty()) {
                    item {
                        Text(
                            text = "Insights",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(state.insights) { insight ->
                        InsightCard(
                            insight = insight,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ─── Bar Chart ──────────────────────────────────
                if (state.trend != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Task Trend",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                state.trend?.let {
                                    BarChart(
                                        data = it,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // ─── Pie Chart ──────────────────────────────────
                if (state.categoryBreakdown.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Category Breakdown",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PieChart(
                                    data = state.categoryBreakdown,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // ─── Focus Time Chart ──────────────────────────
                if (state.focusTimeData.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Focus Time",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LineChart(
                                    data = state.focusTimeData,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // ─── Spacer ──────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
                }
            }
        }

        FloatingActionButton(
            onClick = {
                viewModel.handleEvent(AnalyticsEvent.GenerateReport(ReportFormat.PDF))
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = "Export Report"
            )
        }
    }

}
