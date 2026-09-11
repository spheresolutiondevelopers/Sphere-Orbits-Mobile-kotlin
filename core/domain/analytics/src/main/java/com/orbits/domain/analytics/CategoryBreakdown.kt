package com.orbits.domain.analytics

/**
 * Category breakdown for tasks or events.
 */
data class CategoryBreakdown(
    val category: String,
    val count: Int,
    val percentage: Double,
    val color: String? = null
) {

    /**
     * Get the percentage as a formatted string.
     */
    fun getFormattedPercentage(): String {
        return "${String.format("%.1f", percentage)}%"
    }

    /**
     * Check if the category has a color.
     */
    fun hasColor(): Boolean = color != null

    /**
     * Get the category display name.
     */
    fun getCategoryDisplayName(): String {
        return category.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}
