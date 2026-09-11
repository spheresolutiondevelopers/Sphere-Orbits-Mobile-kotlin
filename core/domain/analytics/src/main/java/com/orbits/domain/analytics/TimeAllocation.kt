package com.orbits.domain.analytics

/**
 * Time allocation by category.
 */
data class TimeAllocation(
    val category: String,
    val hours: Double,
    val percentage: Double,
    val color: String? = null
) {

    /**
     * Get the hours as a formatted string.
     */
    fun getFormattedHours(): String {
        return "${String.format("%.1f", hours)}h"
    }

    /**
     * Get the percentage as a formatted string.
     */
    fun getFormattedPercentage(): String {
        return "${String.format("%.1f", percentage)}%"
    }

    /**
     * Check if the allocation has a color.
     */
    fun hasColor(): Boolean = color != null

    /**
     * Get the category display name.
     */
    fun getCategoryDisplayName(): String {
        return category.replace("_", " ").replaceFirstChar { it.uppercase() }
    }

    /**
     * Get the minutes.
     */
    fun getMinutes(): Double = hours * 60

    /**
     * Get the hours and minutes as a formatted string.
     */
    fun getFormattedHoursAndMinutes(): String {
        val wholeHours = hours.toInt()
        val minutes = ((hours - wholeHours) * 60).toInt()
        return if (wholeHours > 0) {
            if (minutes > 0) "${wholeHours}h ${minutes}m" else "${wholeHours}h"
        } else {
            "${minutes}m"
        }
    }
}
