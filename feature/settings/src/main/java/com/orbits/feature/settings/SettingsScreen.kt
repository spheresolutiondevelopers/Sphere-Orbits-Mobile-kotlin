package com.orbits.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.settings.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToIntegrations: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    // Handle navigation
    LaunchedEffect(Unit) {
        viewModel.handleEvent(SettingsEvent.LoadSettings)
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

                // ─── Local User Prompt ───────────────────────────
                if (state.user?.isLocal == true) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Sync Your Account",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "You are currently using a local profile. Create an official account to sync your data across devices and never lose your schedules.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Button(
                                    onClick = onNavigateToSignup,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Create Account")
                                }
                            }
                        }
                    }
                }

                // ─── Profile ─────────────────────────────────────
                item {
                    ProfileHeader(
                        user = state.user,
                        onEditClick = {
                            viewModel.handleEvent(SettingsEvent.NavigateToProfileEdit)
                            onNavigateToProfileEdit()
                        }
                    )
                }

                // ─── Appearance ─────────────────────────────────
                item {
                    SettingsSection(title = "Appearance") {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Theme Mode", style = MaterialTheme.typography.titleSmall)
                            ThemeSelector(
                                selectedTheme = state.themeMode,
                                onThemeSelected = { viewModel.handleEvent(SettingsEvent.UpdateTheme(it)) }
                            )

                            Text("Color Palette", style = MaterialTheme.typography.titleSmall)
                            PaletteSelector(
                                selectedPalette = state.themePalette,
                                onPaletteSelected = { viewModel.handleEvent(SettingsEvent.UpdateThemePalette(it)) }
                            )
                        }

                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.FormatSize, contentDescription = null) },
                            title = "Compact Mode",
                            subtitle = "Reduce spacing for denser layout",
                            checked = state.settings?.compactMode ?: false,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleCompactMode(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Animation, contentDescription = null) },
                            title = "Reduce Animations",
                            subtitle = "Disable animations for better performance",
                            checked = state.settings?.reduceAnimations ?: false,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleReduceAnimations(it))
                            }
                        )
                    }
                }

                // ─── Language ────────────────────────────────────
                item {
                    SettingsSection(title = "Language") {
                        SettingsDropdownItem(
                            icon = { Icon(Icons.Default.Language, contentDescription = null) },
                            title = "Language",
                            value = state.language,
                            onClick = { /* Show language picker */ }
                        )
                    }
                }

                // ─── Notifications ──────────────────────────────
                item {
                    SettingsSection(title = "Notifications") {
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                            title = "Push Notifications",
                            checked = state.settings?.pushNotificationsEnabled ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.TogglePushNotifications(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Email, contentDescription = null) },
                            title = "Email Notifications",
                            checked = state.settings?.emailNotificationsEnabled ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleEmailNotifications(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Inbox, contentDescription = null) },
                            title = "In-App Notifications",
                            checked = state.settings?.inAppNotificationsEnabled ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleInAppNotifications(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Event, contentDescription = null) },
                            title = "Meeting Reminders",
                            checked = state.settings?.meetingRemindersEnabled ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleMeetingReminders(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                            title = "Task Reminders",
                            checked = state.settings?.taskRemindersEnabled ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleTaskReminders(it))
                            }
                        )
                        SettingsItem(
                            icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                            title = "Reminder Minutes Before",
                            subtitle = "Default: ${state.settings?.reminderMinutesBefore ?: 15} minutes",
                            onClick = { /* Show picker */ }
                        )
                    }
                }

                // ─── Sync ────────────────────────────────────────
                item {
                    SettingsSection(title = "Sync") {
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Sync, contentDescription = null) },
                            title = "Auto Sync",
                            checked = state.settings?.syncEnabled ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleSyncEnabled(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Wifi, contentDescription = null) },
                            title = "Sync Only on Wi-Fi",
                            checked = state.settings?.syncOnlyOnWifi ?: false,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleSyncOnlyOnWifi(it))
                            }
                        )
                        SettingsItem(
                            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            title = "Sync Interval",
                            subtitle = "${state.settings?.syncIntervalMinutes ?: 30} minutes",
                            onClick = { /* Show interval picker */ }
                        )
                    }
                }

                // ─── Privacy ─────────────────────────────────────
                item {
                    SettingsSection(title = "Privacy & Security") {
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                            title = "Share Analytics",
                            subtitle = "Help us improve the app",
                            checked = state.settings?.shareAnalytics ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleShareAnalytics(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.BugReport, contentDescription = null) },
                            title = "Share Crash Reports",
                            checked = state.settings?.shareCrashReports ?: true,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleShareCrashReports(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) },
                            title = "Incognito Mode",
                            checked = state.settings?.incognitoMode ?: false,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleIncognitoMode(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Shield, contentDescription = null) },
                            title = "Two-Factor Authentication",
                            checked = state.settings?.twoFactorEnabled ?: false,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleTwoFactor(it))
                            }
                        )
                        SettingsToggleItem(
                            icon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                            title = "Biometric Authentication",
                            checked = state.settings?.biometricEnabled ?: false,
                            onCheckedChange = {
                                viewModel.handleEvent(SettingsEvent.ToggleBiometric(it))
                            }
                        )
                    }
                }

                // ─── Integrations ────────────────────────────────
                item {
                    SettingsSection(title = "Integrations") {
                        SettingsItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            title = "Connected Apps",
                            subtitle = "Google Calendar, Outlook, Zoom, Teams",
                            onClick = {
                                viewModel.handleEvent(SettingsEvent.NavigateToIntegrations)
                                onNavigateToIntegrations()
                            }
                        )
                    }
                }

                // ─── Support ─────────────────────────────────────
                item {
                    SettingsSection(title = "Support & About") {
                        SettingsItem(
                            icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null) },
                            title = "Help & Support",
                            onClick = {
                                viewModel.handleEvent(SettingsEvent.NavigateToHelp)
                                onNavigateToHelp()
                            }
                        )
                        SettingsItem(
                            icon = { Icon(Icons.Default.Info, contentDescription = null) },
                            title = "About",
                            subtitle = "Version 1.4.2",
                            onClick = {
                                viewModel.handleEvent(SettingsEvent.NavigateToAbout)
                                onNavigateToAbout()
                            }
                        )
                    }
                }

                // ─── Sign Out ────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleEvent(SettingsEvent.ShowSignOutDialog)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign Out",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // ─── Spacer ──────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // ─── Sign Out Dialog ─────────────────────────────────────
        if (state.isSignOutDialogVisible) {
            SignOutDialog(
                onSignOut = {
                    viewModel.handleEvent(SettingsEvent.SignOut)
                    onSignOut()
                },
                onDismiss = {
                    viewModel.handleEvent(SettingsEvent.HideSignOutDialog)
                }
            )
        }
    }
}
