package com.orbits.feature.settings

import com.orbits.domain.settings.Settings
import com.orbits.domain.auth.AuthUser
import com.orbits.core.theme.ThemePalette

/**
 * UI state for the settings feature.
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val user: AuthUser? = null,
    val settings: Settings? = null,
    val themeMode: String = "system",
    val themePalette: ThemePalette = ThemePalette.SPHERE,
    val language: String = "en",
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isSignOutDialogVisible: Boolean = false
)

/**
 * Profile edit UI state.
 */
data class ProfileEditUiState(
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val displayNameError: String? = null,
    val phoneNumberError: String? = null
)

/**
 * Integrations UI state.
 */
data class IntegrationsUiState(
    val isLoading: Boolean = true,
    val googleCalendarConnected: Boolean = false,
    val outlookCalendarConnected: Boolean = false,
    val zoomConnected: Boolean = false,
    val teamsConnected: Boolean = false,
    val slackConnected: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Settings section categories.
 */
enum class SettingsSection(val displayName: String) {
    PROFILE("Profile"),
    APPEARANCE("Appearance"),
    NOTIFICATIONS("Notifications"),
    SYNC("Sync"),
    PRIVACY("Privacy & Security"),
    INTEGRATIONS("Integrations"),
    SUPPORT("Support & About")
}
