package com.orbits.data.settings.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.settings.local.SettingsEntity
import com.orbits.data.settings.remote.SettingsDto
import com.orbits.data.settings.remote.UpdateSettingsRequest
import com.orbits.domain.settings.Settings
import javax.inject.Inject

internal class SettingsMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: SettingsEntity): Settings {
        return Settings(
            id = entity.id,
            userId = entity.userId,

            // Theme & Appearance
            theme = entity.theme,
            accentColor = entity.accentColor,
            fontSize = entity.fontSize,
            compactMode = entity.compactMode,
            reduceAnimations = entity.reduceAnimations,

            // Language & Localization
            language = entity.language,
            timezone = entity.timezone,
            dateFormat = entity.dateFormat,
            timeFormat = entity.timeFormat,
            weekStartDay = entity.weekStartDay,

            // Notifications
            pushNotificationsEnabled = entity.pushNotificationsEnabled,
            emailNotificationsEnabled = entity.emailNotificationsEnabled,
            inAppNotificationsEnabled = entity.inAppNotificationsEnabled,
            meetingRemindersEnabled = entity.meetingRemindersEnabled,
            taskRemindersEnabled = entity.taskRemindersEnabled,
            eventRemindersEnabled = entity.eventRemindersEnabled,
            reminderMinutesBefore = entity.reminderMinutesBefore,
            notificationSound = entity.notificationSound,
            notificationVibration = entity.notificationVibration,

            // Privacy & Security
            twoFactorEnabled = entity.twoFactorEnabled,
            biometricEnabled = entity.biometricEnabled,
            incognitoMode = entity.incognitoMode,
            shareAnalytics = entity.shareAnalytics,
            shareCrashReports = entity.shareCrashReports,

            // Calendar & Sync
            syncEnabled = entity.syncEnabled,
            syncIntervalMinutes = entity.syncIntervalMinutes,
            syncOnlyOnWifi = entity.syncOnlyOnWifi,
            googleCalendarSyncEnabled = entity.googleCalendarSyncEnabled,
            outlookCalendarSyncEnabled = entity.outlookCalendarSyncEnabled,
            defaultCalendarId = entity.defaultCalendarId,

            // Default Values
            defaultTaskPriority = entity.defaultTaskPriority,
            defaultTaskCategory = entity.defaultTaskCategory,
            defaultReminderMinutes = entity.defaultReminderMinutes,
            defaultAppointmentDuration = entity.defaultAppointmentDuration,

            // AI & Productivity
            aiSuggestionsEnabled = entity.aiSuggestionsEnabled,
            smartSchedulingEnabled = entity.smartSchedulingEnabled,
            autoCategorizationEnabled = entity.autoCategorizationEnabled,
            productivityTrackingEnabled = entity.productivityTrackingEnabled,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Settings): SettingsEntity {
        return SettingsEntity(
            id = domain.id,
            userId = domain.userId,

            // Theme & Appearance
            theme = domain.theme,
            accentColor = domain.accentColor,
            fontSize = domain.fontSize,
            compactMode = domain.compactMode,
            reduceAnimations = domain.reduceAnimations,

            // Language & Localization
            language = domain.language,
            timezone = domain.timezone,
            dateFormat = domain.dateFormat,
            timeFormat = domain.timeFormat,
            weekStartDay = domain.weekStartDay,

            // Notifications
            pushNotificationsEnabled = domain.pushNotificationsEnabled,
            emailNotificationsEnabled = domain.emailNotificationsEnabled,
            inAppNotificationsEnabled = domain.inAppNotificationsEnabled,
            meetingRemindersEnabled = domain.meetingRemindersEnabled,
            taskRemindersEnabled = domain.taskRemindersEnabled,
            eventRemindersEnabled = domain.eventRemindersEnabled,
            reminderMinutesBefore = domain.reminderMinutesBefore,
            notificationSound = domain.notificationSound,
            notificationVibration = domain.notificationVibration,

            // Privacy & Security
            twoFactorEnabled = domain.twoFactorEnabled,
            biometricEnabled = domain.biometricEnabled,
            incognitoMode = domain.incognitoMode,
            shareAnalytics = domain.shareAnalytics,
            shareCrashReports = domain.shareCrashReports,

            // Calendar & Sync
            syncEnabled = domain.syncEnabled,
            syncIntervalMinutes = domain.syncIntervalMinutes,
            syncOnlyOnWifi = domain.syncOnlyOnWifi,
            googleCalendarSyncEnabled = domain.googleCalendarSyncEnabled,
            outlookCalendarSyncEnabled = domain.outlookCalendarSyncEnabled,
            defaultCalendarId = domain.defaultCalendarId,

            // Default Values
            defaultTaskPriority = domain.defaultTaskPriority,
            defaultTaskCategory = domain.defaultTaskCategory,
            defaultReminderMinutes = domain.defaultReminderMinutes,
            defaultAppointmentDuration = domain.defaultAppointmentDuration,

            // AI & Productivity
            aiSuggestionsEnabled = domain.aiSuggestionsEnabled,
            smartSchedulingEnabled = domain.smartSchedulingEnabled,
            autoCategorizationEnabled = domain.autoCategorizationEnabled,
            productivityTrackingEnabled = domain.productivityTrackingEnabled,

            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: SettingsDto): SettingsEntity {
        return SettingsEntity(
            id = dto.id,
            userId = dto.userId,

            // Theme & Appearance
            theme = dto.theme,
            accentColor = dto.accentColor,
            fontSize = dto.fontSize,
            compactMode = dto.compactMode,
            reduceAnimations = dto.reduceAnimations,

            // Language & Localization
            language = dto.language,
            timezone = dto.timezone,
            dateFormat = dto.dateFormat,
            timeFormat = dto.timeFormat,
            weekStartDay = dto.weekStartDay,

            // Notifications
            pushNotificationsEnabled = dto.pushNotificationsEnabled,
            emailNotificationsEnabled = dto.emailNotificationsEnabled,
            inAppNotificationsEnabled = dto.inAppNotificationsEnabled,
            meetingRemindersEnabled = dto.meetingRemindersEnabled,
            taskRemindersEnabled = dto.taskRemindersEnabled,
            eventRemindersEnabled = dto.eventRemindersEnabled,
            reminderMinutesBefore = dto.reminderMinutesBefore,
            notificationSound = dto.notificationSound,
            notificationVibration = dto.notificationVibration,

            // Privacy & Security
            twoFactorEnabled = dto.twoFactorEnabled,
            biometricEnabled = dto.biometricEnabled,
            incognitoMode = dto.incognitoMode,
            shareAnalytics = dto.shareAnalytics,
            shareCrashReports = dto.shareCrashReports,

            // Calendar & Sync
            syncEnabled = dto.syncEnabled,
            syncIntervalMinutes = dto.syncIntervalMinutes,
            syncOnlyOnWifi = dto.syncOnlyOnWifi,
            googleCalendarSyncEnabled = dto.googleCalendarSyncEnabled,
            outlookCalendarSyncEnabled = dto.outlookCalendarSyncEnabled,
            defaultCalendarId = dto.defaultCalendarId,

            // Default Values
            defaultTaskPriority = dto.defaultTaskPriority,
            defaultTaskCategory = dto.defaultTaskCategory,
            defaultReminderMinutes = dto.defaultReminderMinutes,
            defaultAppointmentDuration = dto.defaultAppointmentDuration,

            // AI & Productivity
            aiSuggestionsEnabled = dto.aiSuggestionsEnabled,
            smartSchedulingEnabled = dto.smartSchedulingEnabled,
            autoCategorizationEnabled = dto.autoCategorizationEnabled,
            productivityTrackingEnabled = dto.productivityTrackingEnabled,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    fun toDto(entity: SettingsEntity): SettingsDto {
        return SettingsDto(
            id = entity.id,
            userId = entity.userId,

            // Theme & Appearance
            theme = entity.theme,
            accentColor = entity.accentColor,
            fontSize = entity.fontSize,
            compactMode = entity.compactMode,
            reduceAnimations = entity.reduceAnimations,

            // Language & Localization
            language = entity.language,
            timezone = entity.timezone,
            dateFormat = entity.dateFormat,
            timeFormat = entity.timeFormat,
            weekStartDay = entity.weekStartDay,

            // Notifications
            pushNotificationsEnabled = entity.pushNotificationsEnabled,
            emailNotificationsEnabled = entity.emailNotificationsEnabled,
            inAppNotificationsEnabled = entity.inAppNotificationsEnabled,
            meetingRemindersEnabled = entity.meetingRemindersEnabled,
            taskRemindersEnabled = entity.taskRemindersEnabled,
            eventRemindersEnabled = entity.eventRemindersEnabled,
            reminderMinutesBefore = entity.reminderMinutesBefore,
            notificationSound = entity.notificationSound,
            notificationVibration = entity.notificationVibration,

            // Privacy & Security
            twoFactorEnabled = entity.twoFactorEnabled,
            biometricEnabled = entity.biometricEnabled,
            incognitoMode = entity.incognitoMode,
            shareAnalytics = entity.shareAnalytics,
            shareCrashReports = entity.shareCrashReports,

            // Calendar & Sync
            syncEnabled = entity.syncEnabled,
            syncIntervalMinutes = entity.syncIntervalMinutes,
            syncOnlyOnWifi = entity.syncOnlyOnWifi,
            googleCalendarSyncEnabled = entity.googleCalendarSyncEnabled,
            outlookCalendarSyncEnabled = entity.outlookCalendarSyncEnabled,
            defaultCalendarId = entity.defaultCalendarId,

            // Default Values
            defaultTaskPriority = entity.defaultTaskPriority,
            defaultTaskCategory = entity.defaultTaskCategory,
            defaultReminderMinutes = entity.defaultReminderMinutes,
            defaultAppointmentDuration = entity.defaultAppointmentDuration,

            // AI & Productivity
            aiSuggestionsEnabled = entity.aiSuggestionsEnabled,
            smartSchedulingEnabled = entity.smartSchedulingEnabled,
            autoCategorizationEnabled = entity.autoCategorizationEnabled,
            productivityTrackingEnabled = entity.productivityTrackingEnabled,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // ─── DTO ↔ Domain ─────────────────────────────────────────────

    fun toDomain(dto: SettingsDto): Settings {
        return Settings(
            id = dto.id,
            userId = dto.userId,

            // Theme & Appearance
            theme = dto.theme,
            accentColor = dto.accentColor,
            fontSize = dto.fontSize,
            compactMode = dto.compactMode,
            reduceAnimations = dto.reduceAnimations,

            // Language & Localization
            language = dto.language,
            timezone = dto.timezone,
            dateFormat = dto.dateFormat,
            timeFormat = dto.timeFormat,
            weekStartDay = dto.weekStartDay,

            // Notifications
            pushNotificationsEnabled = dto.pushNotificationsEnabled,
            emailNotificationsEnabled = dto.emailNotificationsEnabled,
            inAppNotificationsEnabled = dto.inAppNotificationsEnabled,
            meetingRemindersEnabled = dto.meetingRemindersEnabled,
            taskRemindersEnabled = dto.taskRemindersEnabled,
            eventRemindersEnabled = dto.eventRemindersEnabled,
            reminderMinutesBefore = dto.reminderMinutesBefore,
            notificationSound = dto.notificationSound,
            notificationVibration = dto.notificationVibration,

            // Privacy & Security
            twoFactorEnabled = dto.twoFactorEnabled,
            biometricEnabled = dto.biometricEnabled,
            incognitoMode = dto.incognitoMode,
            shareAnalytics = dto.shareAnalytics,
            shareCrashReports = dto.shareCrashReports,

            // Calendar & Sync
            syncEnabled = dto.syncEnabled,
            syncIntervalMinutes = dto.syncIntervalMinutes,
            syncOnlyOnWifi = dto.syncOnlyOnWifi,
            googleCalendarSyncEnabled = dto.googleCalendarSyncEnabled,
            outlookCalendarSyncEnabled = dto.outlookCalendarSyncEnabled,
            defaultCalendarId = dto.defaultCalendarId,

            // Default Values
            defaultTaskPriority = dto.defaultTaskPriority,
            defaultTaskCategory = dto.defaultTaskCategory,
            defaultReminderMinutes = dto.defaultReminderMinutes,
            defaultAppointmentDuration = dto.defaultAppointmentDuration,

            // AI & Productivity
            aiSuggestionsEnabled = dto.aiSuggestionsEnabled,
            smartSchedulingEnabled = dto.smartSchedulingEnabled,
            autoCategorizationEnabled = dto.autoCategorizationEnabled,
            productivityTrackingEnabled = dto.productivityTrackingEnabled,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // ─── Update Request ───────────────────────────────────────────

    fun toUpdateRequest(domain: Settings): UpdateSettingsRequest {
        return UpdateSettingsRequest(
            // Theme & Appearance
            theme = domain.theme,
            accentColor = domain.accentColor,
            fontSize = domain.fontSize,
            compactMode = domain.compactMode,
            reduceAnimations = domain.reduceAnimations,

            // Language & Localization
            language = domain.language,
            timezone = domain.timezone,
            dateFormat = domain.dateFormat,
            timeFormat = domain.timeFormat,
            weekStartDay = domain.weekStartDay,

            // Notifications
            pushNotificationsEnabled = domain.pushNotificationsEnabled,
            emailNotificationsEnabled = domain.emailNotificationsEnabled,
            inAppNotificationsEnabled = domain.inAppNotificationsEnabled,
            meetingRemindersEnabled = domain.meetingRemindersEnabled,
            taskRemindersEnabled = domain.taskRemindersEnabled,
            eventRemindersEnabled = domain.eventRemindersEnabled,
            reminderMinutesBefore = domain.reminderMinutesBefore,
            notificationSound = domain.notificationSound,
            notificationVibration = domain.notificationVibration,

            // Privacy & Security
            twoFactorEnabled = domain.twoFactorEnabled,
            biometricEnabled = domain.biometricEnabled,
            incognitoMode = domain.incognitoMode,
            shareAnalytics = domain.shareAnalytics,
            shareCrashReports = domain.shareCrashReports,

            // Calendar & Sync
            syncEnabled = domain.syncEnabled,
            syncIntervalMinutes = domain.syncIntervalMinutes,
            syncOnlyOnWifi = domain.syncOnlyOnWifi,
            googleCalendarSyncEnabled = domain.googleCalendarSyncEnabled,
            outlookCalendarSyncEnabled = domain.outlookCalendarSyncEnabled,
            defaultCalendarId = domain.defaultCalendarId,

            // Default Values
            defaultTaskPriority = domain.defaultTaskPriority,
            defaultTaskCategory = domain.defaultTaskCategory,
            defaultReminderMinutes = domain.defaultReminderMinutes,
            defaultAppointmentDuration = domain.defaultAppointmentDuration,

            // AI & Productivity
            aiSuggestionsEnabled = domain.aiSuggestionsEnabled,
            smartSchedulingEnabled = domain.smartSchedulingEnabled,
            autoCategorizationEnabled = domain.autoCategorizationEnabled,
            productivityTrackingEnabled = domain.productivityTrackingEnabled
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<SettingsEntity>): List<Settings> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Settings>): List<SettingsEntity> {
        return domains.map { toEntity(it) }
    }
}