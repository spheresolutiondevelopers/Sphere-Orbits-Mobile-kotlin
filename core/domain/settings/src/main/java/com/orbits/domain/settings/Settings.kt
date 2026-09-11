/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.domain.settings

/**
 * Pure domain model for user settings and preferences.
 */
data class Settings(
    val id: String,
    val userId: String,
    val theme: String = "system",
    val themePalette: String = "sphere",
    val accentColor: String? = null,
    val fontSize: String = "medium",
    val compactMode: Boolean = false,
    val reduceAnimations: Boolean = false,
    val language: String = "en",
    val timezone: String? = null,
    val dateFormat: String = "MM/dd/yyyy",
    val timeFormat: String = "12h",
    val weekStartDay: String = "monday",
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val inAppNotificationsEnabled: Boolean = true,
    val meetingRemindersEnabled: Boolean = true,
    val taskRemindersEnabled: Boolean = true,
    val eventRemindersEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 15,
    val notificationSound: String? = null,
    val notificationVibration: Boolean = true,
    val twoFactorEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val incognitoMode: Boolean = false,
    val shareAnalytics: Boolean = true,
    val shareCrashReports: Boolean = true,
    val syncEnabled: Boolean = true,
    val syncIntervalMinutes: Int = 30,
    val syncOnlyOnWifi: Boolean = false,
    val googleCalendarSyncEnabled: Boolean = false,
    val outlookCalendarSyncEnabled: Boolean = false,
    val defaultCalendarId: String? = null,
    val defaultTaskPriority: String = "medium",
    val defaultTaskCategory: String = "general",
    val defaultReminderMinutes: Int = 15,
    val defaultAppointmentDuration: Int = 60,
    val aiSuggestionsEnabled: Boolean = true,
    val smartSchedulingEnabled: Boolean = true,
    val autoCategorizationEnabled: Boolean = true,
    val productivityTrackingEnabled: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)
