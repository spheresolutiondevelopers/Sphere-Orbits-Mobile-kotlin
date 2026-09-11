package com.orbits.domain.analytics

/**
 * Analytics event types.
 */
enum class AnalyticsEventType(val displayName: String, val category: AnalyticsEventCategory) {
    // ─── Task Events ──────────────────────────────────────────────
    TASK_CREATED("Task Created", AnalyticsEventCategory.TASK),
    TASK_COMPLETED("Task Completed", AnalyticsEventCategory.TASK),
    TASK_DELETED("Task Deleted", AnalyticsEventCategory.TASK),
    TASK_UPDATED("Task Updated", AnalyticsEventCategory.TASK),

    // ─── Appointment Events ──────────────────────────────────────
    APPOINTMENT_CREATED("Appointment Created", AnalyticsEventCategory.APPOINTMENT),
    APPOINTMENT_COMPLETED("Appointment Completed", AnalyticsEventCategory.APPOINTMENT),
    APPOINTMENT_CANCELLED("Appointment Cancelled", AnalyticsEventCategory.APPOINTMENT),

    // ─── Meeting Events ──────────────────────────────────────────
    MEETING_JOINED("Meeting Joined", AnalyticsEventCategory.MEETING),
    MEETING_CREATED("Meeting Created", AnalyticsEventCategory.MEETING),
    MEETING_ENDED("Meeting Ended", AnalyticsEventCategory.MEETING),

    // ─── Note Events ─────────────────────────────────────────────
    NOTE_CREATED("Note Created", AnalyticsEventCategory.NOTE),
    NOTE_UPDATED("Note Updated", AnalyticsEventCategory.NOTE),
    NOTE_DELETED("Note Deleted", AnalyticsEventCategory.NOTE),

    // ─── Time Tracking ───────────────────────────────────────────
    FOCUS_TIME("Focus Time", AnalyticsEventCategory.FOCUS),
    BREAK_TIME("Break Time", AnalyticsEventCategory.FOCUS),

    // ─── User Events ─────────────────────────────────────────────
    USER_LOGIN("User Login", AnalyticsEventCategory.USER),
    USER_LOGOUT("User Logout", AnalyticsEventCategory.USER),
    USER_REGISTERED("User Registered", AnalyticsEventCategory.USER),

    // ─── App Events ──────────────────────────────────────────────
    APP_LAUNCH("App Launched", AnalyticsEventCategory.APP),
    APP_FOREGROUND("App Foreground", AnalyticsEventCategory.APP),
    APP_BACKGROUND("App Background", AnalyticsEventCategory.APP),

    // ─── Sync Events ─────────────────────────────────────────────
    SYNC_STARTED("Sync Started", AnalyticsEventCategory.SYSTEM),
    SYNC_COMPLETED("Sync Completed", AnalyticsEventCategory.SYSTEM),
    SYNC_FAILED("Sync Failed", AnalyticsEventCategory.SYSTEM);

    companion object {
        fun fromString(value: String): AnalyticsEventType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): AnalyticsEventType? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Analytics event categories.
 */
enum class AnalyticsEventCategory(val displayName: String) {
    TASK("Task"),
    APPOINTMENT("Appointment"),
    MEETING("Meeting"),
    NOTE("Note"),
    FOCUS("Focus"),
    USER("User"),
    APP("App"),
    SYSTEM("System"),
    PERFORMANCE("Performance");

    companion object {
        fun fromString(value: String): AnalyticsEventCategory? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
