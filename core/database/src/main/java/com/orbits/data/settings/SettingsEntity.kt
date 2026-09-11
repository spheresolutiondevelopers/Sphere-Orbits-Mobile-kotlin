package com.orbits.data.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "settings",
    indices = [
        Index(value = ["user_id"], unique = true),
        Index(value = ["updated_at"])
    ]
)
data class SettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "theme")
    val theme: String = "system",

    @ColumnInfo(name = "accent_color")
    val accentColor: String? = null,

    @ColumnInfo(name = "font_size")
    val fontSize: String = "medium",

    @ColumnInfo(name = "compact_mode")
    val compactMode: Boolean = false,

    @ColumnInfo(name = "reduce_animations")
    val reduceAnimations: Boolean = false,

    @ColumnInfo(name = "language")
    val language: String = "en",

    @ColumnInfo(name = "timezone")
    val timezone: String? = null,

    @ColumnInfo(name = "date_format")
    val dateFormat: String = "MM/dd/yyyy",

    @ColumnInfo(name = "time_format")
    val timeFormat: String = "12h",

    @ColumnInfo(name = "week_start_day")
    val weekStartDay: String = "monday",

    @ColumnInfo(name = "push_notifications_enabled")
    val pushNotificationsEnabled: Boolean = true,

    @ColumnInfo(name = "email_notifications_enabled")
    val emailNotificationsEnabled: Boolean = true,

    @ColumnInfo(name = "in_app_notifications_enabled")
    val inAppNotificationsEnabled: Boolean = true,

    @ColumnInfo(name = "meeting_reminders_enabled")
    val meetingRemindersEnabled: Boolean = true,

    @ColumnInfo(name = "task_reminders_enabled")
    val taskRemindersEnabled: Boolean = true,

    @ColumnInfo(name = "event_reminders_enabled")
    val eventRemindersEnabled: Boolean = true,

    @ColumnInfo(name = "reminder_minutes_before")
    val reminderMinutesBefore: Int = 15,

    @ColumnInfo(name = "notification_sound")
    val notificationSound: String? = null,

    @ColumnInfo(name = "notification_vibration")
    val notificationVibration: Boolean = true,

    @ColumnInfo(name = "two_factor_enabled")
    val twoFactorEnabled: Boolean = false,

    @ColumnInfo(name = "biometric_enabled")
    val biometricEnabled: Boolean = false,

    @ColumnInfo(name = "incognito_mode")
    val incognitoMode: Boolean = false,

    @ColumnInfo(name = "share_analytics")
    val shareAnalytics: Boolean = true,

    @ColumnInfo(name = "share_crash_reports")
    val shareCrashReports: Boolean = true,

    @ColumnInfo(name = "sync_enabled")
    val syncEnabled: Boolean = true,

    @ColumnInfo(name = "sync_interval_minutes")
    val syncIntervalMinutes: Int = 30,

    @ColumnInfo(name = "sync_only_on_wifi")
    val syncOnlyOnWifi: Boolean = false,

    @ColumnInfo(name = "google_calendar_sync_enabled")
    val googleCalendarSyncEnabled: Boolean = false,

    @ColumnInfo(name = "outlook_calendar_sync_enabled")
    val outlookCalendarSyncEnabled: Boolean = false,

    @ColumnInfo(name = "default_calendar_id")
    val defaultCalendarId: String? = null,

    @ColumnInfo(name = "default_task_priority")
    val defaultTaskPriority: String = "medium",

    @ColumnInfo(name = "default_task_category")
    val defaultTaskCategory: String = "general",

    @ColumnInfo(name = "default_reminder_minutes")
    val defaultReminderMinutes: Int = 15,

    @ColumnInfo(name = "default_appointment_duration")
    val defaultAppointmentDuration: Int = 60,

    @ColumnInfo(name = "ai_suggestions_enabled")
    val aiSuggestionsEnabled: Boolean = true,

    @ColumnInfo(name = "smart_scheduling_enabled")
    val smartSchedulingEnabled: Boolean = true,

    @ColumnInfo(name = "auto_categorization_enabled")
    val autoCategorizationEnabled: Boolean = true,

    @ColumnInfo(name = "productivity_tracking_enabled")
    val productivityTrackingEnabled: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced"
)
