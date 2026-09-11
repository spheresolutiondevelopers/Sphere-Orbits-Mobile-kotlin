package com.orbits.feature.settings

import com.orbits.domain.settings.Settings

/**
 * UI events for the settings feature.
 */
sealed class SettingsEvent {
    // ─── Main Settings ────────────────────────────────────────────

    data object LoadSettings : SettingsEvent()
    data object Refresh : SettingsEvent()
    data object DismissError : SettingsEvent()
    data object NavigateToProfileEdit : SettingsEvent()
    data object NavigateToIntegrations : SettingsEvent()
    data object NavigateToHelp : SettingsEvent()
    data object NavigateToAbout : SettingsEvent()
    data object ShowSignOutDialog : SettingsEvent()
    data object HideSignOutDialog : SettingsEvent()
    data object SignOut : SettingsEvent()

    // ─── Appearance ──────────────────────────────────────────────

    data class UpdateTheme(val theme: String) : SettingsEvent()
    data class UpdateThemePalette(val palette: com.orbits.core.theme.ThemePalette) : SettingsEvent()
    data class UpdateAccentColor(val color: String?) : SettingsEvent()
    data class UpdateFontSize(val size: String) : SettingsEvent()
    data class ToggleCompactMode(val enabled: Boolean) : SettingsEvent()
    data class ToggleReduceAnimations(val enabled: Boolean) : SettingsEvent()

    // ─── Language ────────────────────────────────────────────────

    data class UpdateLanguage(val language: String) : SettingsEvent()

    // ─── Notifications ───────────────────────────────────────────

    data class TogglePushNotifications(val enabled: Boolean) : SettingsEvent()
    data class ToggleEmailNotifications(val enabled: Boolean) : SettingsEvent()
    data class ToggleInAppNotifications(val enabled: Boolean) : SettingsEvent()
    data class ToggleMeetingReminders(val enabled: Boolean) : SettingsEvent()
    data class ToggleTaskReminders(val enabled: Boolean) : SettingsEvent()
    data class ToggleEventReminders(val enabled: Boolean) : SettingsEvent()
    data class UpdateReminderMinutes(val minutes: Int) : SettingsEvent()

    // ─── Sync ────────────────────────────────────────────────────

    data class ToggleSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class UpdateSyncInterval(val minutes: Int) : SettingsEvent()
    data class ToggleSyncOnlyOnWifi(val enabled: Boolean) : SettingsEvent()

    // ─── Privacy ──────────────────────────────────────────────────

    data class ToggleShareAnalytics(val enabled: Boolean) : SettingsEvent()
    data class ToggleShareCrashReports(val enabled: Boolean) : SettingsEvent()
    data class ToggleIncognitoMode(val enabled: Boolean) : SettingsEvent()
    data class ToggleTwoFactor(val enabled: Boolean) : SettingsEvent()
    data class ToggleBiometric(val enabled: Boolean) : SettingsEvent()

    // ─── Profile Edit ────────────────────────────────────────────

    data class ProfileDisplayNameChanged(val name: String) : SettingsEvent()
    data class ProfileFirstNameChanged(val name: String) : SettingsEvent()
    data class ProfileLastNameChanged(val name: String) : SettingsEvent()
    data class ProfilePhoneNumberChanged(val phone: String) : SettingsEvent()
    data object ProfileSubmit : SettingsEvent()
    data object ProfileCancel : SettingsEvent()
    data object ProfileDismissError : SettingsEvent()

    // ─── Integrations ────────────────────────────────────────────

    data object LoadIntegrations : SettingsEvent()
    data class ConnectGoogleCalendar(val authCode: String) : SettingsEvent()
    data object DisconnectGoogleCalendar : SettingsEvent()
    data class ConnectOutlookCalendar(val authCode: String) : SettingsEvent()
    data object DisconnectOutlookCalendar : SettingsEvent()
    data class ConnectZoom(val authCode: String) : SettingsEvent()
    data object DisconnectZoom : SettingsEvent()
    data class ConnectTeams(val authCode: String) : SettingsEvent()
    data object DisconnectTeams : SettingsEvent()
}
