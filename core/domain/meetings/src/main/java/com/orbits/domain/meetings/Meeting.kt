package com.orbits.domain.meetings

/**
 * Pure domain model for a meeting.
 * Contains NO Android dependencies — safe for KMP.
 *
 * Meetings are scheduled virtual or in-person gatherings with agendas,
 * minutes, and action items. Each meeting is linked to a Task.
 */
data class Meeting(
    val id: String,
    val taskId: String,
    val organizerUserId: String,
    val title: String,
    val description: String? = null,
    val startDateTime: String,
    val endDateTime: String,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null, // zoom, teams, google_meet, custom
    val meetingId: String? = null, // Platform-specific meeting ID
    val passcode: String? = null,
    val agenda: String? = null,
    val minutes: String? = null,
    val actionItems: List<ActionItem> = emptyList(),
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null, // RRULE format
    val status: String = "scheduled", // scheduled, live, ended, cancelled
    val recordingUrl: String? = null,
    val transcriptUrl: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the meeting is currently live.
     */
    fun isLive(): Boolean = status == "live"

    /**
     * Check if the meeting is scheduled for the future.
     */
    fun isUpcoming(): Boolean {
        if (status != "scheduled") return false
        return try {
            val start = java.time.Instant.parse(startDateTime)
            start.isAfter(java.time.Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if the meeting is in the past.
     */
    fun isPast(): Boolean {
        if (status == "ended" || status == "cancelled") return true
        return try {
            val end = java.time.Instant.parse(endDateTime)
            end.isBefore(java.time.Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the duration in minutes.
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
     * Get the meeting's status label.
     */
    fun getStatusLabel(): String {
        return when (status) {
            "scheduled" -> "Scheduled"
            "live" -> "Live"
            "ended" -> "Ended"
            "cancelled" -> "Cancelled"
            else -> status.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get the meeting's platform display name.
     */
    fun getPlatformDisplayName(): String? {
        return when (meetingPlatform) {
            "zoom" -> "Zoom"
            "teams" -> "Microsoft Teams"
            "google_meet" -> "Google Meet"
            "custom" -> "Custom"
            else -> meetingPlatform
        }
    }

    /**
     * Check if the meeting is editable.
     */
    fun isEditable(): Boolean = status in listOf("scheduled")

    /**
     * Check if the meeting is cancellable.
     */
    fun isCancellable(): Boolean = status in listOf("scheduled", "live")

    /**
     * Get the meeting's date as a formatted string.
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
     * Get the meeting's time as a formatted string.
     */
    fun getFormattedTime(): String {
        return try {
            val time = java.time.LocalTime.parse(startDateTime.substring(11, 16))
            java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                .format(time)
        } catch (e: Exception) {
            startDateTime
        }
    }

    /**
     * Get the meeting's date and time as a formatted string.
     */
    fun getFormattedDateTime(): String {
        return "${getFormattedDate()} at ${getFormattedTime()}"
    }

    /**
     * Count the number of action items.
     */
    fun getActionItemCount(): Int = actionItems.size

    /**
     * Count the number of completed action items.
     */
    fun getCompletedActionItemCount(): Int = actionItems.count { it.status == "completed" }

    /**
     * Get the completion percentage of action items.
     */
    fun getActionItemCompletionPercentage(): Int {
        if (actionItems.isEmpty()) return 0
        return (getCompletedActionItemCount().toDouble() / actionItems.size * 100).toInt()
    }
}

/**
 * Action Item — a task derived from a meeting.
 */
data class ActionItem(
    val id: String,
    val meetingId: String,
    val description: String,
    val assignedTo: String? = null,
    val dueDate: String? = null,
    val status: String = "pending", // pending, in_progress, completed, cancelled
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the action item is completed.
     */
    fun isCompleted(): Boolean = status == "completed"

    /**
     * Get the status label.
     */
    fun getStatusLabel(): String {
        return when (status) {
            "pending" -> "Pending"
            "in_progress" -> "In Progress"
            "completed" -> "Completed"
            "cancelled" -> "Cancelled"
            else -> status.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Check if the action item is overdue.
     */
    fun isOverdue(): Boolean {
        if (isCompleted()) return false
        if (dueDate == null) return false
        return try {
            val due = java.time.LocalDate.parse(dueDate)
            val now = java.time.LocalDate.now()
            due.isBefore(now)
        } catch (e: Exception) {
            false
        }
    }
}
