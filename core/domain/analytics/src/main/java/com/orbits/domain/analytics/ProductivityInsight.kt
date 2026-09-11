package com.orbits.domain.analytics

/**
 * A productivity insight generated from analytics data.
 */
data class ProductivityInsight(
    val title: String,
    val description: String,
    val type: String, // positive, negative, neutral
    val recommendation: String? = null,
    val metricValue: Double? = null,
    val metricLabel: String? = null
) {

    /**
     * Check if the insight is positive.
     */
    fun isPositive(): Boolean = type == "positive"

    /**
     * Check if the insight is negative.
     */
    fun isNegative(): Boolean = type == "negative"

    /**
     * Check if the insight is neutral.
     */
    fun isNeutral(): Boolean = type == "neutral"

    /**
     * Get the type as a display string.
     */
    fun getTypeDisplay(): String {
        return when (type) {
            "positive" -> "✅ Positive"
            "negative" -> "⚠️ Area for Improvement"
            "neutral" -> "💡 Neutral"
            else -> "💡 Insight"
        }
    }

    /**
     * Get the metric display string.
     */
    fun getMetricDisplay(): String? {
        if (metricValue == null || metricLabel == null) return null
        return "${String.format("%.1f", metricValue)} $metricLabel"
    }

    /**
     * Check if the insight has a recommendation.
     */
    fun hasRecommendation(): Boolean = recommendation != null
}
