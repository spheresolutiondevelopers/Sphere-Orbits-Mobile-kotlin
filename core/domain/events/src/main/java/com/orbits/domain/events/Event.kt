package com.orbits.domain.events

/**
 * Pure domain model for an event.
 * Contains NO Android dependencies — safe for KMP.
 *
 * Events are social/organizational events like conferences, workshops,
 * weddings, parties, etc. They differ from CalendarEvent which represents
 * calendar appointments.
 */
data class Event(
    val id: String,
    val userId: String,
    val categoryId: String? = null,
    val taskId: String? = null,
    val name: String,
    val description: String? = null,
    val format: String? = null, // In-Person, Virtual, Hybrid
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String = "planned", // planned, ongoing, completed, cancelled
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String = "USD",
    val maxAttendees: Int? = null,
    val isPublic: Boolean = false,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the event is currently happening.
     */
    fun isOngoing(): Boolean {
        if (status != "planned" && status != "ongoing") return false
        if (startDateTime == null || endDateTime == null) return false
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
     * Check if the event is upcoming.
     */
    fun isUpcoming(): Boolean {
        if (status != "planned") return false
        if (startDateTime == null) return false
        return try {
            val start = java.time.Instant.parse(startDateTime)
            start.isAfter(java.time.Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if the event is in the past.
     */
    fun isPast(): Boolean {
        if (status == "completed") return true
        if (endDateTime == null) return false
        return try {
            val end = java.time.Instant.parse(endDateTime)
            end.isBefore(java.time.Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the event's status label.
     */
    fun getStatusLabel(): String {
        return when (status) {
            "planned" -> "Planned"
            "ongoing" -> "Ongoing"
            "completed" -> "Completed"
            "cancelled" -> "Cancelled"
            else -> status.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get the event's format display name.
     */
    fun getFormatDisplayName(): String {
        return when (format) {
            "In-Person" -> "In-Person"
            "Virtual" -> "Virtual"
            "Hybrid" -> "Hybrid"
            else -> format ?: "TBD"
        }
    }

    /**
     * Get the event's date as a formatted string.
     */
    fun getFormattedDate(): String? {
        if (startDateTime == null) return null
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
    fun getFormattedTime(): String? {
        if (startDateTime == null) return null
        return try {
            val time = java.time.LocalTime.parse(startDateTime.substring(11, 16))
            java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                .format(time)
        } catch (e: Exception) {
            startDateTime
        }
    }

    /**
     * Get the event's date range as a formatted string.
     */
    fun getFormattedDateRange(): String? {
        if (startDateTime == null) return null
        if (endDateTime == null) return getFormattedDate()

        return try {
            val startDate = java.time.LocalDate.parse(startDateTime.substring(0, 10))
            val endDate = java.time.LocalDate.parse(endDateTime.substring(0, 10))
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")

            if (startDate == endDate) {
                formatter.format(startDate)
            } else {
                "${formatter.format(startDate)} - ${formatter.format(endDate)}"
            }
        } catch (e: Exception) {
            startDateTime
        }
    }

    /**
     * Check if the event can be edited.
     */
    fun isEditable(): Boolean {
        return status !in listOf("completed", "cancelled")
    }

    /**
     * Check if the event can be cancelled.
     */
    fun isCancellable(): Boolean {
        return status !in listOf("completed", "cancelled")
    }

    /**
     * Get the event's budget as a formatted string.
     */
    fun getFormattedBudget(): String? {
        if (budget == null) return null
        return when (currency) {
            "USD" -> "$${String.format("%.2f", budget)}"
            "EUR" -> "€${String.format("%.2f", budget)}"
            "GBP" -> "£${String.format("%.2f", budget)}"
            "KES" -> "KSh ${String.format("%.2f", budget)}"
            else -> "${String.format("%.2f", budget)} $currency"
        }
    }

    /**
     * Get the percentage of max attendees (if maxAttendees is set).
     */
    fun getAttendeePercentage(confirmedCount: Int): Int {
        if (maxAttendees == null || maxAttendees == 0) return 0
        return ((confirmedCount.toDouble() / maxAttendees) * 100).toInt().coerceIn(0, 100)
    }
}
