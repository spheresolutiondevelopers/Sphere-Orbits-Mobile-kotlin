package com.orbits.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.settings.Settings
import com.orbits.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileEditUiState())
    val profileState: StateFlow<ProfileEditUiState> = _profileState.asStateFlow()

    private val _integrationsState = MutableStateFlow(IntegrationsUiState())
    val integrationsState: StateFlow<IntegrationsUiState> = _integrationsState.asStateFlow()

    init {
        loadSettings()
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.LoadSettings -> loadSettings()
            SettingsEvent.Refresh -> refresh()
            SettingsEvent.DismissError -> dismissError()

            SettingsEvent.NavigateToProfileEdit -> navigateToProfileEdit()
            SettingsEvent.NavigateToIntegrations -> navigateToIntegrations()
            SettingsEvent.NavigateToHelp -> navigateToHelp()
            SettingsEvent.NavigateToAbout -> navigateToAbout()

            SettingsEvent.ShowSignOutDialog -> showSignOutDialog()
            SettingsEvent.HideSignOutDialog -> hideSignOutDialog()
            SettingsEvent.SignOut -> signOut()

            // Appearance
            is SettingsEvent.UpdateTheme -> updateTheme(event.theme)
            is SettingsEvent.UpdateThemePalette -> updateThemePalette(event.palette)
            is SettingsEvent.UpdateAccentColor -> updateAccentColor(event.color)
            is SettingsEvent.UpdateFontSize -> updateFontSize(event.size)
            is SettingsEvent.ToggleCompactMode -> toggleCompactMode(event.enabled)
            is SettingsEvent.ToggleReduceAnimations -> toggleReduceAnimations(event.enabled)

            // Language
            is SettingsEvent.UpdateLanguage -> updateLanguage(event.language)

            // Notifications
            is SettingsEvent.TogglePushNotifications -> togglePushNotifications(event.enabled)
            is SettingsEvent.ToggleEmailNotifications -> toggleEmailNotifications(event.enabled)
            is SettingsEvent.ToggleInAppNotifications -> toggleInAppNotifications(event.enabled)
            is SettingsEvent.ToggleMeetingReminders -> toggleMeetingReminders(event.enabled)
            is SettingsEvent.ToggleTaskReminders -> toggleTaskReminders(event.enabled)
            is SettingsEvent.ToggleEventReminders -> toggleEventReminders(event.enabled)
            is SettingsEvent.UpdateReminderMinutes -> updateReminderMinutes(event.minutes)

            // Sync
            is SettingsEvent.ToggleSyncEnabled -> toggleSyncEnabled(event.enabled)
            is SettingsEvent.UpdateSyncInterval -> updateSyncInterval(event.minutes)
            is SettingsEvent.ToggleSyncOnlyOnWifi -> toggleSyncOnlyOnWifi(event.enabled)

            // Privacy
            is SettingsEvent.ToggleShareAnalytics -> toggleShareAnalytics(event.enabled)
            is SettingsEvent.ToggleShareCrashReports -> toggleShareCrashReports(event.enabled)
            is SettingsEvent.ToggleIncognitoMode -> toggleIncognitoMode(event.enabled)
            is SettingsEvent.ToggleTwoFactor -> toggleTwoFactor(event.enabled)
            is SettingsEvent.ToggleBiometric -> toggleBiometric(event.enabled)

            // Profile
            is SettingsEvent.ProfileDisplayNameChanged -> updateProfileDisplayName(event.name)
            is SettingsEvent.ProfileFirstNameChanged -> updateProfileFirstName(event.name)
            is SettingsEvent.ProfileLastNameChanged -> updateProfileLastName(event.name)
            is SettingsEvent.ProfilePhoneNumberChanged -> updateProfilePhoneNumber(event.phone)
            SettingsEvent.ProfileSubmit -> submitProfile()
            SettingsEvent.ProfileCancel -> cancelProfile()
            SettingsEvent.ProfileDismissError -> dismissProfileError()

            // Integrations
            SettingsEvent.LoadIntegrations -> loadIntegrations()
            is SettingsEvent.ConnectGoogleCalendar -> connectGoogleCalendar(event.authCode)
            SettingsEvent.DisconnectGoogleCalendar -> disconnectGoogleCalendar()
            is SettingsEvent.ConnectOutlookCalendar -> connectOutlookCalendar(event.authCode)
            SettingsEvent.DisconnectOutlookCalendar -> disconnectOutlookCalendar()
            is SettingsEvent.ConnectZoom -> connectZoom(event.authCode)
            SettingsEvent.DisconnectZoom -> disconnectZoom()
            is SettingsEvent.ConnectTeams -> connectTeams(event.authCode)
            SettingsEvent.DisconnectTeams -> disconnectTeams()
        }
    }

    // ─── Load Operations ─────────────────────────────────────────

    private fun loadSettings() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Load user
                val user = authRepository.getCurrentUser()
                _state.update { it.copy(user = user) }

                // Load settings
                settingsRepository.observeSettings().collect { settings ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            settings = settings,
                            themeMode = settings.theme,
                            themePalette = try {
                                com.orbits.core.theme.ThemePalette.valueOf(settings.themePalette.uppercase())
                            } catch (e: Exception) {
                                com.orbits.core.theme.ThemePalette.SPHERE
                            },
                            language = settings.language
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.e("Settings", "Failed to load settings", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load settings"
                    )
                }
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadSettings()
        _state.update { it.copy(isRefreshing = false) }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // ─── Navigation ──────────────────────────────────────────────

    private fun navigateToProfileEdit() {
        val user = _state.value.user
        if (user != null) {
            _profileState.value = ProfileEditUiState(
                displayName = user.displayName ?: "",
                firstName = user.firstName ?: "",
                lastName = user.lastName ?: "",
                phoneNumber = user.phoneNumber ?: "",
                email = user.email
            )
        }
    }

    private fun navigateToIntegrations() {
        loadIntegrations()
    }

    private fun navigateToHelp() {
        // Navigation handled by NavGraph
    }

    private fun navigateToAbout() {
        // Navigation handled by NavGraph
    }

    private fun showSignOutDialog() {
        _state.update { it.copy(isSignOutDialogVisible = true) }
    }

    private fun hideSignOutDialog() {
        _state.update { it.copy(isSignOutDialogVisible = false) }
    }

    private fun signOut() {
        viewModelScope.launch {
            val result = authRepository.logout()
            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isSignOutDialogVisible = false) }
                    // Navigation handled by NavGraph
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to sign out"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Appearance ──────────────────────────────────────────────

    private fun updateTheme(theme: String) {
        _state.update { it.copy(themeMode = theme) }
        viewModelScope.launch {
            val result = settingsRepository.updateTheme(theme)
            when (result) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update theme"
                        )
                    }
                }
                else -> { /* Success */ }
            }
        }
    }

    private fun updateThemePalette(palette: com.orbits.core.theme.ThemePalette) {
        _state.update { it.copy(themePalette = palette) }
        viewModelScope.launch {
            val currentSettings = _state.value.settings ?: return@launch
            val updatedSettings = currentSettings.copy(themePalette = palette.name.lowercase())
            val result = settingsRepository.updateSettings(updatedSettings)
            when (result) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update theme palette"
                        )
                    }
                }
                else -> { /* Success */ }
            }
        }
    }

    private fun updateAccentColor(color: String?) {
        // In production, this would update the accent color
        Logger.d("Settings", "Accent color updated: $color")
    }

    private fun updateFontSize(size: String) {
        // In production, this would update the font size
        Logger.d("Settings", "Font size updated: $size")
    }

    private fun toggleCompactMode(enabled: Boolean) {
        // In production, this would toggle compact mode
        Logger.d("Settings", "Compact mode: $enabled")
    }

    private fun toggleReduceAnimations(enabled: Boolean) {
        // In production, this would toggle reduced animations
        Logger.d("Settings", "Reduce animations: $enabled")
    }

    // ─── Language ────────────────────────────────────────────────

    private fun updateLanguage(language: String) {
        _state.update { it.copy(language = language) }
        viewModelScope.launch {
            val result = settingsRepository.updateLanguage(language)
            when (result) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update language"
                        )
                    }
                }
                else -> { /* Success */ }
            }
        }
    }

    // ─── Notifications ───────────────────────────────────────────

    private fun togglePushNotifications(enabled: Boolean) {
        updateNotificationSetting { copy(pushNotificationsEnabled = enabled) }
    }

    private fun toggleEmailNotifications(enabled: Boolean) {
        updateNotificationSetting { copy(emailNotificationsEnabled = enabled) }
    }

    private fun toggleInAppNotifications(enabled: Boolean) {
        updateNotificationSetting { copy(inAppNotificationsEnabled = enabled) }
    }

    private fun toggleMeetingReminders(enabled: Boolean) {
        updateNotificationSetting { copy(meetingRemindersEnabled = enabled) }
    }

    private fun toggleTaskReminders(enabled: Boolean) {
        updateNotificationSetting { copy(taskRemindersEnabled = enabled) }
    }

    private fun toggleEventReminders(enabled: Boolean) {
        updateNotificationSetting { copy(eventRemindersEnabled = enabled) }
    }

    private fun updateReminderMinutes(minutes: Int) {
        if (minutes !in 0..1440) {
            _state.update {
                it.copy(
                    errorMessage = "Reminder minutes must be between 0 and 1440"
                )
            }
            return
        }
        updateNotificationSetting { copy(reminderMinutesBefore = minutes) }
    }

    private fun updateNotificationSetting(update: Settings.() -> Settings) {
        val current = _state.value.settings ?: return
        val updated = current.update()
        _state.update { it.copy(settings = updated) }

        viewModelScope.launch {
            val result = settingsRepository.updateSettings(updated)
            when (result) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update settings"
                        )
                    }
                }
                else -> { /* Success */ }
            }
        }
    }

    // ─── Sync ────────────────────────────────────────────────────

    private fun toggleSyncEnabled(enabled: Boolean) {
        updateSyncSetting { copy(syncEnabled = enabled) }
    }

    private fun updateSyncInterval(minutes: Int) {
        if (minutes !in 5..1440) {
            _state.update {
                it.copy(
                    errorMessage = "Sync interval must be between 5 and 1440 minutes"
                )
            }
            return
        }
        updateSyncSetting { copy(syncIntervalMinutes = minutes) }
    }

    private fun toggleSyncOnlyOnWifi(enabled: Boolean) {
        updateSyncSetting { copy(syncOnlyOnWifi = enabled) }
    }

    private fun updateSyncSetting(update: Settings.() -> Settings) {
        val current = _state.value.settings ?: return
        val updated = current.update()
        _state.update { it.copy(settings = updated) }

        viewModelScope.launch {
            val result = settingsRepository.updateSettings(updated)
            when (result) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update sync settings"
                        )
                    }
                }
                else -> { /* Success */ }
            }
        }
    }

    // ─── Privacy ──────────────────────────────────────────────────

    private fun toggleShareAnalytics(enabled: Boolean) {
        updatePrivacySetting { copy(shareAnalytics = enabled) }
    }

    private fun toggleShareCrashReports(enabled: Boolean) {
        updatePrivacySetting { copy(shareCrashReports = enabled) }
    }

    private fun toggleIncognitoMode(enabled: Boolean) {
        updatePrivacySetting { copy(incognitoMode = enabled) }
    }

    private fun toggleTwoFactor(enabled: Boolean) {
        updatePrivacySetting { copy(twoFactorEnabled = enabled) }
    }

    private fun toggleBiometric(enabled: Boolean) {
        updatePrivacySetting { copy(biometricEnabled = enabled) }
    }

    private fun updatePrivacySetting(update: Settings.() -> Settings) {
        val current = _state.value.settings ?: return
        val updated = current.update()
        _state.update { it.copy(settings = updated) }

        viewModelScope.launch {
            val result = settingsRepository.updateSettings(updated)
            when (result) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update privacy settings"
                        )
                    }
                }
                else -> { /* Success */ }
            }
        }
    }

    // ─── Profile Edit ────────────────────────────────────────────

    private fun updateProfileDisplayName(name: String) {
        _profileState.update {
            it.copy(
                displayName = name,
                displayNameError = if (name.isNotBlank() && name.length > 100) {
                    "Display name must be 100 characters or less"
                } else null
            )
        }
    }

    private fun updateProfileFirstName(name: String) {
        _profileState.update { it.copy(firstName = name) }
    }

    private fun updateProfileLastName(name: String) {
        _profileState.update { it.copy(lastName = name) }
    }

    private fun updateProfilePhoneNumber(phone: String) {
        _profileState.update {
            it.copy(
                phoneNumber = phone,
                phoneNumberError = if (phone.isNotBlank() && phone.replace(Regex("[^0-9]"), "").length < 10) {
                    "Phone number must be at least 10 digits"
                } else null
            )
        }
    }

    private fun submitProfile() {
        val state = _profileState.value

        if (state.displayNameError != null || state.phoneNumberError != null) {
            _profileState.update {
                it.copy(
                    errorMessage = "Please fix the errors above"
                )
            }
            return
        }

        _profileState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // In production, update the user profile via AuthRepository
                // For now, just simulate success
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                loadSettings()
            } catch (e: Exception) {
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to update profile"
                    )
                }
            }
        }
    }

    private fun cancelProfile() {
        _profileState.value = ProfileEditUiState()
    }

    private fun dismissProfileError() {
        _profileState.update { it.copy(errorMessage = null) }
    }

    // ─── Integrations ────────────────────────────────────────────

    private fun loadIntegrations() {
        _integrationsState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // In production, check integration status from repository
                // For now, use default values
                _integrationsState.update {
                    it.copy(
                        isLoading = false,
                        googleCalendarConnected = false,
                        outlookCalendarConnected = false,
                        zoomConnected = false,
                        teamsConnected = false,
                        slackConnected = false
                    )
                }
            } catch (e: Exception) {
                _integrationsState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load integrations"
                    )
                }
            }
        }
    }

    private fun connectGoogleCalendar(authCode: String) {
        _integrationsState.update { it.copy(errorMessage = null) }
        viewModelScope.launch {
            try {
                // In production, connect Google Calendar with the auth code
                _integrationsState.update {
                    it.copy(googleCalendarConnected = true)
                }
            } catch (e: Exception) {
                _integrationsState.update {
                    it.copy(
                        errorMessage = e.message ?: "Failed to connect Google Calendar"
                    )
                }
            }
        }
    }

    private fun disconnectGoogleCalendar() {
        _integrationsState.update {
            it.copy(
                googleCalendarConnected = false,
                errorMessage = null
            )
        }
    }

    private fun connectOutlookCalendar(authCode: String) {
        _integrationsState.update { it.copy(errorMessage = null) }
        viewModelScope.launch {
            try {
                _integrationsState.update {
                    it.copy(outlookCalendarConnected = true)
                }
            } catch (e: Exception) {
                _integrationsState.update {
                    it.copy(
                        errorMessage = e.message ?: "Failed to connect Outlook Calendar"
                    )
                }
            }
        }
    }

    private fun disconnectOutlookCalendar() {
        _integrationsState.update {
            it.copy(
                outlookCalendarConnected = false,
                errorMessage = null
            )
        }
    }

    private fun connectZoom(authCode: String) {
        _integrationsState.update { it.copy(errorMessage = null) }
        viewModelScope.launch {
            try {
                _integrationsState.update {
                    it.copy(zoomConnected = true)
                }
            } catch (e: Exception) {
                _integrationsState.update {
                    it.copy(
                        errorMessage = e.message ?: "Failed to connect Zoom"
                    )
                }
            }
        }
    }

    private fun disconnectZoom() {
        _integrationsState.update {
            it.copy(
                zoomConnected = false,
                errorMessage = null
            )
        }
    }

    private fun connectTeams(authCode: String) {
        _integrationsState.update { it.copy(errorMessage = null) }
        viewModelScope.launch {
            try {
                _integrationsState.update {
                    it.copy(teamsConnected = true)
                }
            } catch (e: Exception) {
                _integrationsState.update {
                    it.copy(
                        errorMessage = e.message ?: "Failed to connect Teams"
                    )
                }
            }
        }
    }

    private fun disconnectTeams() {
        _integrationsState.update {
            it.copy(
                teamsConnected = false,
                errorMessage = null
            )
        }
    }
}
