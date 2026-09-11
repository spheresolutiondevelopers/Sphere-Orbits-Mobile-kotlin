package com.orbits.domain.analytics

/**
 * Productivity statistics for a time period.
 */
data class ProductivityStats(
    val tasksCompleted: Int,
    val tasksCreated: Int,
    val tasksOverdue: Int,
    val tasksCompletionRate: Double, // 0-100
    val appointmentsAttended: Int,
    val appointmentsScheduled: Int,
    val meetingsAttended: Int,
    val meetingsScheduled: Int,
    val totalFocusHours: Double,
    val averageTaskCompletionTime: Double, // in hours
    val productivityScore: Int, // 0-100
    val trend: String, // up, down, stable
    val period: String, // daily, weekly, monthly, custom
    val startDate: String,
    val endDate: String
) {

    /**
     * Get the total tasks handled.
     */
    fun getTotalTasks(): Int = tasksCreated

    /**
     * Get the total appointments handled.
     */
    fun getTotalAppointments(): Int = appointmentsScheduled

    /**
     * Get the total meetings handled.
     */
    fun getTotalMeetings(): Int = meetingsScheduled

    /**
     * Get the task completion rate as a formatted string.
     */
    fun getFormattedCompletionRate(): String {
        return "${String.format("%.1f", tasksCompletionRate)}%"
    }

    /**
     * Get the total focus hours as a formatted string.
     */
    fun getFormattedFocusHours(): String {
        return "${String.format("%.1f", totalFocusHours)}h"
    }

    /**
     * Get the productivity score as a formatted string.
     */
    fun getFormattedProductivityScore(): String {
        return "$productivityScore"
    }

    /**
     * Get the trend as a display string.
     */
    fun getTrendDisplay(): String {
        return when (trend) {
            "up" -> "↑ Improving"
            "down" -> "↓ Declining"
            "stable" -> "→ Stable"
            else -> "→ Stable"
        }
    }

    /**
     * Check if the trend is positive.
     */
    fun isTrendPositive(): Boolean = trend == "up"

    /**
     * Check if the trend is negative.
     */
    fun isTrendNegative(): Boolean = trend == "down"

    /**
     * Get the period display name.
     */
    fun getPeriodDisplayName(): String {
        return when (period) {
            "daily" -> "Today"
            "weekly" -> "This Week"
            "monthly" -> "This Month"
            else -> "Custom Range"
        }
    }

    /**
     * Get the date range as a formatted string.
     */
    fun getFormattedDateRange(): String {
        return try {
            val start = java.time.LocalDate.parse(startDate)
            val end = java.time.LocalDate.parse(endDate)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")

            if (start == end) {
                formatter.format(start)
            } else {
                "${formatter.format(start)} - ${formatter.format(end)}"
            }
        } catch (e: Exception) {
            "$startDate - $endDate"
        }
    }

    /**
     * Get the average task completion time as a formatted string.
     */
    fun getFormattedAverageCompletionTime(): String {
        val hours = averageTaskCompletionTime
        val wholeHours = hours.toInt()
        val minutes = ((hours - wholeHours) * 60).toInt()

        return when {
            wholeHours > 0 && minutes > 0 -> "${wholeHours}h ${minutes}m"
            wholeHours > 0 -> "${wholeHours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    /**
     * Get the tasks completed per day.
     */
    fun getTasksCompletedPerDay(): Double {
        val days = try {
            val start = java.time.LocalDate.parse(startDate)
            val end = java.time.LocalDate.parse(endDate)
            java.time.Period.between(start, end).days + 1
        } catch (e: Exception) {
            1
        }
        return tasksCompleted.toDouble() / days
    }
}
