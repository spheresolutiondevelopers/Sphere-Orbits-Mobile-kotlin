package com.orbits.data.settings.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class SettingsDto(
    val id: String,
    val userId: String,

    // Theme & Appearance
    val theme: String = "system",
    val accentColor: String? = null,
    val fontSize: String = "medium",
    val compactMode: Boolean = false,
    val reduceAnimations: Boolean = false,

    // Language & Localization
    val language: String = "en",
    val timezone: String? = null,
    val dateFormat: String = "MM/dd/yyyy",
    val timeFormat: String = "12h",
    val weekStartDay: String = "monday",

    // Notifications
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val inAppNotificationsEnabled: Boolean = true,
    val meetingRemindersEnabled: Boolean = true,
    val taskRemindersEnabled: Boolean = true,
    val eventRemindersEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 15,
    val notificationSound: String? = null,
    val notificationVibration: Boolean = true,

    // Privacy & Security
    val twoFactorEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val incognitoMode: Boolean = false,
    val shareAnalytics: Boolean = true,
    val shareCrashReports: Boolean = true,

    // Calendar & Sync
    val syncEnabled: Boolean = true,
    val syncIntervalMinutes: Int = 30,
    val syncOnlyOnWifi: Boolean = false,
    val googleCalendarSyncEnabled: Boolean = false,
    val outlookCalendarSyncEnabled: Boolean = false,
    val defaultCalendarId: String? = null,

    // Default Values
    val defaultTaskPriority: String = "medium",
    val defaultTaskCategory: String = "general",
    val defaultReminderMinutes: Int = 15,
    val defaultAppointmentDuration: Int = 60,

    // AI & Productivity
    val aiSuggestionsEnabled: Boolean = true,
    val smartSchedulingEnabled: Boolean = true,
    val autoCategorizationEnabled: Boolean = true,
    val productivityTrackingEnabled: Boolean = true,

    val createdAt: String,
    val updatedAt: String
)

@Serializable
internal data class UpdateSettingsRequest(
    // Theme & Appearance
    val theme: String? = null,
    val accentColor: String? = null,
    val fontSize: String? = null,
    val compactMode: Boolean? = null,
    val reduceAnimations: Boolean? = null,

    // Language & Localization
    val language: String? = null,
    val timezone: String? = null,
    val dateFormat: String? = null,
    val timeFormat: String? = null,
    val weekStartDay: String? = null,

    // Notifications
    val pushNotificationsEnabled: Boolean? = null,
    val emailNotificationsEnabled: Boolean? = null,
    val inAppNotificationsEnabled: Boolean? = null,
    val meetingRemindersEnabled: Boolean? = null,
    val taskRemindersEnabled: Boolean? = null,
    val eventRemindersEnabled: Boolean? = null,
    val reminderMinutesBefore: Int? = null,
    val notificationSound: String? = null,
    val notificationVibration: Boolean? = null,

    // Privacy & Security
    val twoFactorEnabled: Boolean? = null,
    val biometricEnabled: Boolean? = null,
    val incognitoMode: Boolean? = null,
    val shareAnalytics: Boolean? = null,
    val shareCrashReports: Boolean? = null,

    // Calendar & Sync
    val syncEnabled: Boolean? = null,
    val syncIntervalMinutes: Int? = null,
    val syncOnlyOnWifi: Boolean? = null,
    val googleCalendarSyncEnabled: Boolean? = null,
    val outlookCalendarSyncEnabled: Boolean? = null,
    val defaultCalendarId: String? = null,

    // Default Values
    val defaultTaskPriority: String? = null,
    val defaultTaskCategory: String? = null,
    val defaultReminderMinutes: Int? = null,
    val defaultAppointmentDuration: Int? = null,

    // AI & Productivity
    val aiSuggestionsEnabled: Boolean? = null,
    val smartSchedulingEnabled: Boolean? = null,
    val autoCategorizationEnabled: Boolean? = null,
    val productivityTrackingEnabled: Boolean? = null
)

@Serializable
internal data class ThemePreferenceDto(
    val theme: String // light, dark, system
)

@Serializable
internal data class NotificationPreferencesDto(
    val pushEnabled: Boolean,
    val emailEnabled: Boolean,
    val inAppEnabled: Boolean,
    val meetingReminders: Boolean,
    val taskReminders: Boolean,
    val eventReminders: Boolean,
    val reminderMinutesBefore: Int
)

@Serializable
internal data class SyncPreferencesDto(
    val syncEnabled: Boolean,
    val syncIntervalMinutes: Int,
    val syncOnlyOnWifi: Boolean,
    val googleCalendarSyncEnabled: Boolean,
    val outlookCalendarSyncEnabled: Boolean,
    val defaultCalendarId: String?
)

@Serializable
internal data class PrivacyPreferencesDto(
    val shareAnalytics: Boolean,
    val shareCrashReports: Boolean,
    val incognitoMode: Boolean,
    val twoFactorEnabled: Boolean,
    val biometricEnabled: Boolean
)