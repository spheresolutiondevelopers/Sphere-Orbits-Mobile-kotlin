package com.orbits.domain.calendar

/**
 * Pure domain model for a calendar event.
 * Contains NO Android dependencies — safe for KMP.
 */
data class CalendarEvent(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val startDateTime: String,
    val endDateTime: String,
    val allDayEvent: Boolean = false,
    val location: String? = null,
    val isVirtual: Boolean = false,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val status: String = "scheduled", // scheduled, confirmed, cancelled, completed, rescheduled
    val reminderMinutesBefore: Int = 15,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null, // RRULE format
    val calendarColor: String? = "#2196F3",
    val notes: String? = null,
    val externalEventId: String? = null,
    val externalSyncStatus: String = "not_synced",
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the event is in the future.
     */
    fun isFuture(): Boolean {
        return try {
            val start = java.time.Instant.parse(startDateTime)
            start.isAfter(java.time.Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if the event is currently happening.
     */
    fun isOngoing(): Boolean {
        return try {
            val start = java.time.Instant.parse(startDateTime)
            val end = java.time.Instant.parse(endDateTime)
            val now = java.time.Instant.now()
            now.isAfter(start) && now.isBefore(end)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if the event is in the past.
     */
    fun isPast(): Boolean {
        return try {
            val end = java.time.Instant.parse(endDateTime)
            end.isBefore(java.time.Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the duration of the event in minutes.
     */
    fun getDurationMinutes(): Int {
        return try {
            val start = java.time.Instant.parse(startDateTime)
            val end = java.time.Instant.parse(endDateTime)
            java.time.Duration.between(start, end).toMinutes().toInt()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get the event's date as a formatted string.
     */
    fun getFormattedDate(): String {
        return try {
            val date = java.time.LocalDate.parse(startDateTime.substring(0, 10))
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(date)
        } catch (e: Exception) {
            startDateTime
        }
    }

    /**
     * Get the event's time as a formatted string.
     */
    fun getFormattedTime(): String {
        if (allDayEvent) return "All Day"
        return try {
            val time = java.time.LocalTime.parse(startDateTime.substring(11, 16))
            java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                .format(time)
        } catch (e: Exception) {
            startDateTime
        }
    }

    /**
     * Get the event's status label.
     */
    fun getStatusLabel(): String {
        return when (status) {
            "scheduled" -> "Scheduled"
            "confirmed" -> "Confirmed"
            "cancelled" -> "Cancelled"
            "completed" -> "Completed"
            "rescheduled" -> "Rescheduled"
            else -> status.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Check if the event is cancellable.
     */
    fun isCancellable(): Boolean {
        return status !in listOf("cancelled", "completed")
    }

    /**
     * Check if the event is editable.
     */
    fun isEditable(): Boolean {
        return status !in listOf("cancelled", "completed")
    }

    /**
     * Get the meeting platform display name.
     */
    fun getMeetingPlatformDisplayName(): String? {
        return when (meetingPlatform) {
            "zoom" -> "Zoom"
            "teams" -> "Microsoft Teams"
            "google_meet" -> "Google Meet"
            "custom" -> "Custom"
            else -> meetingPlatform
        }
    }
}