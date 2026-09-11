package com.orbits.domain.analytics

/**
 * Complete dashboard statistics.
 */
data class DashboardStats(
    val today: ProductivityStats,
    val week: ProductivityStats,
    val month: ProductivityStats,
    val insights: List<ProductivityInsight> = emptyList(),
    val taskTrend: TaskCompletionTrend,
    val categoryBreakdown: List<CategoryBreakdown>,
    val timeAllocation: List<TimeAllocation>
) {

    /**
     * Get the most important insight.
     */
    fun getTopInsight(): ProductivityInsight? {
        return insights.firstOrNull()
    }

    /**
     * Get all positive insights.
     */
    fun getPositiveInsights(): List<ProductivityInsight> {
        return insights.filter { it.isPositive() }
    }

    /**
     * Get all negative insights.
     */
    fun getNegativeInsights(): List<ProductivityInsight> {
        return insights.filter { it.isNegative() }
    }

    /**
     * Get all neutral insights.
     */
    fun getNeutralInsights(): List<ProductivityInsight> {
        return insights.filter { it.isNeutral() }
    }

    /**
     * Get the top category from the breakdown.
     */
    fun getTopCategory(): CategoryBreakdown? {
        return categoryBreakdown.maxByOrNull { it.count }
    }

    /**
     * Get the top time allocation.
     */
    fun getTopTimeAllocation(): TimeAllocation? {
        return timeAllocation.maxByOrNull { it.hours }
    }

    /**
     * Get the total tasks completed across all periods.
     */
    fun getTotalTasksCompleted(): Int {
        return today.tasksCompleted + week.tasksCompleted + month.tasksCompleted
    }

    /**
     * Get the overall productivity score (average of all periods).
     */
    fun getOverallProductivityScore(): Int {
        val scores = listOf(today.productivityScore, week.productivityScore, month.productivityScore)
        return if (scores.isNotEmpty()) (scores.average()).toInt() else 0
    }

    /**
     * Get the trend direction across periods.
     */
    fun getOverallTrend(): String {
        val scores = listOf(today.productivityScore, week.productivityScore, month.productivityScore)
            .filter { it > 0 }

        return when {
            scores.size < 2 -> "stable"
            scores.last() > scores.first() -> "up"
            scores.last() < scores.first() -> "down"
            else -> "stable"
        }
    }
}
