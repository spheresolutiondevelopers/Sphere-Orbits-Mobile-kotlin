package com.orbits.domain.analytics

/**
 * Pure domain model for an analytics event.
 * Contains NO Android dependencies — safe for KMP.
 */
data class AnalyticsEvent(
    val id: String,
    val userId: String,
    val eventType: String, // task_created, task_completed, appointment_scheduled, meeting_joined, etc.
    val eventCategory: String = "user_action", // user_action, system, performance
    val eventSubtype: String? = null,
    val entityId: String? = null, // ID of the entity this event relates to
    val entityType: String? = null, // task, appointment, meeting, etc.
    val value: Double? = null, // Numeric value (e.g., task priority, duration, etc.)
    val metadata: Map<String, String> = emptyMap(),
    val sessionId: String? = null,
    val eventTimestamp: String,
    val createdAt: String
) {

    /**
     * Check if the event is a user action.
     */
    fun isUserAction(): Boolean = eventCategory == "user_action"

    /**
     * Check if the event is a system event.
     */
    fun isSystemEvent(): Boolean = eventCategory == "system"

    /**
     * Check if the event is a performance event.
     */
    fun isPerformanceEvent(): Boolean = eventCategory == "performance"

    /**
     * Check if the event is related to tasks.
     */
    fun isTaskEvent(): Boolean = entityType == "task"

    /**
     * Check if the event is related to appointments.
     */
    fun isAppointmentEvent(): Boolean = entityType == "appointment"

    /**
     * Check if the event is related to meetings.
     */
    fun isMeetingEvent(): Boolean = entityType == "meeting"

    /**
     * Check if the event is related to notes.
     */
    fun isNoteEvent(): Boolean = entityType == "note"

    /**
     * Get the event type display name.
     */
    fun getEventTypeDisplayName(): String {
        return when (eventType) {
            "task_created" -> "Task Created"
            "task_completed" -> "Task Completed"
            "task_deleted" -> "Task Deleted"
            "task_updated" -> "Task Updated"
            "appointment_created" -> "Appointment Created"
            "appointment_completed" -> "Appointment Completed"
            "appointment_cancelled" -> "Appointment Cancelled"
            "meeting_joined" -> "Meeting Joined"
            "meeting_created" -> "Meeting Created"
            "meeting_ended" -> "Meeting Ended"
            "note_created" -> "Note Created"
            "note_updated" -> "Note Updated"
            "note_deleted" -> "Note Deleted"
            "focus_time" -> "Focus Time"
            "break_time" -> "Break Time"
            "user_login" -> "User Login"
            "user_logout" -> "User Logout"
            "user_registered" -> "User Registered"
            "app_launch" -> "App Launched"
            "app_foreground" -> "App Foreground"
            "app_background" -> "App Background"
            "sync_started" -> "Sync Started"
            "sync_completed" -> "Sync Completed"
            "sync_failed" -> "Sync Failed"
            else -> eventType.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get the event category display name.
     */
    fun getEventCategoryDisplayName(): String {
        return when (eventCategory) {
            "user_action" -> "User Action"
            "system" -> "System"
            "performance" -> "Performance"
            else -> eventCategory.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get the formatted event timestamp.
     */
    fun getFormattedTimestamp(): String {
        return try {
            val instant = java.time.Instant.parse(eventTimestamp)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")
                .format(date)
        } catch (e: Exception) {
            eventTimestamp
        }
    }

    /**
     * Get the formatted event date.
     */
    fun getFormattedDate(): String {
        return try {
            val instant = java.time.Instant.parse(eventTimestamp)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(date)
        } catch (e: Exception) {
            eventTimestamp
        }
    }

    /**
     * Check if the event has a numeric value.
     */
    fun hasValue(): Boolean = value != null

    /**
     * Get the event's value as a formatted string.
     */
    fun getFormattedValue(): String? {
        if (value == null) return null
        return when (eventType) {
            "focus_time", "break_time" -> "${String.format("%.1f", value)} min"
            else -> value.toString()
        }
    }

    /**
     * Get metadata value by key.
     */
    fun getMetadata(key: String): String? = metadata[key]

    /**
     * Check if the event has metadata.
     */
    fun hasMetadata(): Boolean = metadata.isNotEmpty()

    /**
     * Get the session duration if available (from metadata).
     */
    fun getSessionDuration(): Double? {
        return metadata["duration"]?.toDoubleOrNull()
    }
}
