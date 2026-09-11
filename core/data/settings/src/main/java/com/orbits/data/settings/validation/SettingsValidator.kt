package com.orbits.data.settings.validation

import com.orbits.domain.settings.Settings

object SettingsValidator {

    fun validate(settings: Settings): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Theme
        if (settings.theme !in listOf("light", "dark", "system")) {
            errors.add(ValidationError("theme", "Invalid theme. Must be: light, dark, or system"))
        }

        // Font Size
        if (settings.fontSize !in listOf("small", "medium", "large", "x_large")) {
            errors.add(ValidationError("fontSize", "Invalid font size"))
        }

        // Language
        if (!settings.language.matches(Regex("^[a-z]{2}(-[A-Z]{2})?$"))) {
            errors.add(ValidationError("language", "Invalid language format. Use ISO 639-1 format (e.g., 'en', 'en-US')"))
        }

        // Date Format
        if (settings.dateFormat.isBlank()) {
            errors.add(ValidationError("dateFormat", "Date format cannot be empty"))
        }

        // Time Format
        if (settings.timeFormat !in listOf("12h", "24h")) {
            errors.add(ValidationError("timeFormat", "Invalid time format. Must be: 12h or 24h"))
        }

        // Week Start Day
        if (settings.weekStartDay !in listOf("monday", "sunday", "saturday")) {
            errors.add(ValidationError("weekStartDay", "Invalid week start day"))
        }

        // Reminder Minutes
        if (settings.reminderMinutesBefore !in 0..1440) {
            errors.add(ValidationError("reminderMinutesBefore", "Reminder minutes must be between 0 and 1440"))
        }

        // Sync Interval
        if (settings.syncIntervalMinutes !in 5..1440) {
            errors.add(ValidationError("syncIntervalMinutes", "Sync interval must be between 5 and 1440 minutes"))
        }

        // Default Reminder Minutes
        if (settings.defaultReminderMinutes !in 0..1440) {
            errors.add(ValidationError("defaultReminderMinutes", "Default reminder minutes must be between 0 and 1440"))
        }

        // Default Appointment Duration
        if (settings.defaultAppointmentDuration !in 15..480) {
            errors.add(ValidationError("defaultAppointmentDuration", "Default appointment duration must be between 15 and 480 minutes"))
        }

        // Accent Color
        val accentColor = settings.accentColor
        if (accentColor != null) {
            if (!accentColor.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                errors.add(ValidationError("accentColor", "Invalid accent color format. Must be #RRGGBB"))
            }
        }

        // Default Task Priority
        if (settings.defaultTaskPriority !in listOf("low", "medium", "high", "critical")) {
            errors.add(ValidationError("defaultTaskPriority", "Invalid default task priority"))
        }

        // Default Task Category
        if (settings.defaultTaskCategory !in listOf("general", "meeting", "reminder", "deadline", "event")) {
            errors.add(ValidationError("defaultTaskCategory", "Invalid default task category"))
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateTheme(theme: String): Boolean {
        return theme in listOf("light", "dark", "system")
    }

    fun validateLanguage(language: String): Boolean {
        return language.matches(Regex("^[a-z]{2}(-[A-Z]{2})?$"))
    }

    fun validateTimeFormat(timeFormat: String): Boolean {
        return timeFormat in listOf("12h", "24h")
    }
}

data class ValidationError(
    val field: String,
    val message: String
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>
)