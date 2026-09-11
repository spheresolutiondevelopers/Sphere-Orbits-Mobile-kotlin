package com.orbits.domain.appointments

/**
 * Pure domain model for an appointment.
 * Contains NO Android dependencies — safe for KMP.
 *
 * Appointments are time-specific commitments with one or more participants.
 * They can be personal, business, medical, or general appointments.
 * Unlike Meetings, Appointments are not linked to Tasks.
 */
data class Appointment(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val appointmentType: String = "general", // general, doctor, business, personal, meeting
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
     * Check if the appointment is in the future.
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
     * Check if the appointment is currently happening.
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
     * Check if the appointment is in the past.
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
     * Get the duration of the appointment in minutes.
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
     * Get the appointment's date as a formatted string.
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
     * Get the appointment's time as a formatted string.
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
     * Get the appointment's date and time as a formatted string.
     */
    fun getFormattedDateTime(): String {
        if (allDayEvent) return getFormattedDate()
        return "${getFormattedDate()} at ${getFormattedTime()}"
    }

    /**
     * Get the appointment's status label.
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
     * Check if the appointment is cancellable.
     */
    fun isCancellable(): Boolean {
        return status !in listOf("cancelled", "completed")
    }

    /**
     * Check if the appointment is editable.
     */
    fun isEditable(): Boolean {
        return status !in listOf("cancelled", "completed")
    }

    /**
     * Get the appointment type display name.
     */
    fun getAppointmentTypeDisplayName(): String {
        return when (appointmentType) {
            "general" -> "General"
            "doctor" -> "Doctor"
            "business" -> "Business"
            "personal" -> "Personal"
            "meeting" -> "Meeting"
            else -> appointmentType.replaceFirstChar { it.uppercase() }
        }
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

    /**
     * Check if the appointment conflicts with another time slot.
     */
    fun conflictsWith(otherStart: String, otherEnd: String): Boolean {
        return try {
            val start = java.time.Instant.parse(startDateTime)
            val end = java.time.Instant.parse(endDateTime)
            val otherStartInst = java.time.Instant.parse(otherStart)
            val otherEndInst = java.time.Instant.parse(otherEnd)
            !(end <= otherStartInst || start >= otherEndInst)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the duration in hours (for reporting).
     */
    fun getDurationHours(): Double {
        return getDurationMinutes() / 60.0
    }
}
