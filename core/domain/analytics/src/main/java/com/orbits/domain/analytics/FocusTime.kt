package com.orbits.domain.analytics

/**
 * Focus time data for a specific day.
 */
data class FocusTime(
    val date: String,
    val hours: Double,
    val tasksCompleted: Int,
    val focusScore: Int // 0-100
) {

    /**
     * Get the hours as a formatted string.
     */
    fun getFormattedHours(): String {
        return "${String.format("%.1f", hours)}h"
    }

    /**
     * Get the focus score as a formatted string.
     */
    fun getFormattedFocusScore(): String {
        return "$focusScore"
    }

    /**
     * Get the date as a formatted string.
     */
    fun getFormattedDate(): String {
        return try {
            val dateObj = java.time.LocalDate.parse(date)
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(dateObj)
        } catch (e: Exception) {
            date
        }
    }

    /**
     * Get the focus score level.
     */
    fun getFocusLevel(): String {
        return when {
            focusScore >= 80 -> "Excellent"
            focusScore >= 60 -> "Good"
            focusScore >= 40 -> "Fair"
            focusScore >= 20 -> "Needs Work"
            else -> "Poor"
        }
    }

    /**
     * Check if the focus score is excellent.
     */
    fun isExcellent(): Boolean = focusScore >= 80

    /**
     * Check if the focus score is good.
     */
    fun isGood(): Boolean = focusScore in 60..79

    /**
     * Check if the focus time is empty.
     */
    fun isEmpty(): Boolean = hours == 0.0

    /**
     * Get the minutes.
     */
    fun getMinutes(): Double = hours * 60

    /**
     * Get the tasks completed per hour.
     */
    fun getTasksPerHour(): Double {
        return if (hours > 0) tasksCompleted.toDouble() / hours else 0.0
    }
}
