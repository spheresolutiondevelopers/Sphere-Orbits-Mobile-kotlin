package com.orbits.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.settings.components.SettingsItem
import com.orbits.feature.settings.components.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.integrationsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(SettingsEvent.LoadIntegrations)
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
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // ─── Refresh Action ─────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.handleEvent(SettingsEvent.LoadIntegrations) }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Refresh")
                        }
                    }
                }

                // ─── Error Message ──────────────────────────────
                if (state.errorMessage != null) {
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
                                    text = state.errorMessage!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { viewModel.handleEvent(SettingsEvent.DismissError) }
                                ) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }

                // ─── Calendar Integrations ──────────────────────
                item {
                    SettingsSection(title = "Calendar") {
                        SettingsItem(
                            icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            title = "Google Calendar",
                            subtitle = if (state.googleCalendarConnected) "Connected" else "Not connected",
                            trailing = {
                                if (state.googleCalendarConnected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Connected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                if (state.googleCalendarConnected) {
                                    viewModel.handleEvent(SettingsEvent.DisconnectGoogleCalendar)
                                } else {
                                    // In production, start OAuth flow
                                }
                            }
                        )
                        SettingsItem(
                            icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            title = "Outlook Calendar",
                            subtitle = if (state.outlookCalendarConnected) "Connected" else "Not connected",
                            trailing = {
                                if (state.outlookCalendarConnected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Connected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                if (state.outlookCalendarConnected) {
                                    viewModel.handleEvent(SettingsEvent.DisconnectOutlookCalendar)
                                } else {
                                    // In production, start OAuth flow
                                }
                            }
                        )
                    }
                }

                // ─── Meeting Integrations ────────────────────────
                item {
                    SettingsSection(title = "Meeting Platforms") {
                        SettingsItem(
                            icon = { Icon(Icons.Default.Videocam, contentDescription = null) },
                            title = "Zoom",
                            subtitle = if (state.zoomConnected) "Connected" else "Not connected",
                            trailing = {
                                if (state.zoomConnected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Connected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                if (state.zoomConnected) {
                                    viewModel.handleEvent(SettingsEvent.DisconnectZoom)
                                } else {
                                    // In production, start OAuth flow
                                }
                            }
                        )
                        SettingsItem(
                            icon = { Icon(Icons.Default.Videocam, contentDescription = null) },
                            title = "Microsoft Teams",
                            subtitle = if (state.teamsConnected) "Connected" else "Not connected",
                            trailing = {
                                if (state.teamsConnected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Connected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                if (state.teamsConnected) {
                                    viewModel.handleEvent(SettingsEvent.DisconnectTeams)
                                } else {
                                    // In production, start OAuth flow
                                }
                            }
                        )
                    }
                }

                // ─── Other Integrations ──────────────────────────
                item {
                    SettingsSection(title = "Other") {
                        SettingsItem(
                            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                            title = "Slack",
                            subtitle = "Coming soon",
                            onClick = {}
                        )
                        SettingsItem(
                            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                            title = "Discord",
                            subtitle = "Coming soon",
                            onClick = {}
                        )
                    }
                }

                // ─── Spacer ──────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
