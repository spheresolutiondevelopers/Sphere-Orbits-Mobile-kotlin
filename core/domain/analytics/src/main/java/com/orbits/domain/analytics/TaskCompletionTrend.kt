package com.orbits.domain.analytics

/**
 * Task completion trend data for charting.
 */
data class TaskCompletionTrend(
    val labels: List<String>, // dates or periods
    val completed: List<Int>, // number of tasks completed per period
    val created: List<Int>, // number of tasks created per period
    val overdue: List<Int> // number of tasks overdue per period
) {

    /**
     * Check if the trend data is valid.
     */
    fun isValid(): Boolean {
        return labels.isNotEmpty() &&
                completed.size == labels.size &&
                created.size == labels.size &&
                overdue.size == labels.size
    }

    /**
     * Get the total number of data points.
     */
    fun getDataPointCount(): Int = labels.size

    /**
     * Get the maximum value among all series.
     */
    fun getMaxValue(): Int {
        val allValues = completed + created + overdue
        return if (allValues.isEmpty()) 0 else allValues.maxOrNull() ?: 0
    }

    /**
     * Get the total completed tasks over the period.
     */
    fun getTotalCompleted(): Int = completed.sum()

    /**
     * Get the total created tasks over the period.
     */
    fun getTotalCreated(): Int = created.sum()

    /**
     * Get the total overdue tasks over the period.
     */
    fun getTotalOverdue(): Int = overdue.sum()

    /**
     * Get the completion ratio for each period.
     */
    fun getCompletionRatios(): List<Double> {
        return labels.indices.map { i ->
            if (created[i] > 0) {
                completed[i].toDouble() / created[i]
            } else {
                0.0
            }
        }
    }

    /**
     * Get the average completion rate.
     */
    fun getAverageCompletionRate(): Double {
        val ratios = getCompletionRatios()
        return if (ratios.isEmpty()) 0.0 else ratios.average()
    }

    /**
     * Get the best performing period.
     */
    fun getBestPeriod(): String? {
        val ratios = getCompletionRatios()
        val maxIndex = ratios.indexOf(ratios.maxOrNull() ?: return null)
        return if (maxIndex >= 0 && maxIndex < labels.size) labels[maxIndex] else null
    }

    /**
     * Get the worst performing period.
     */
    fun getWorstPeriod(): String? {
        val ratios = getCompletionRatios()
        val minIndex = ratios.indexOf(ratios.minOrNull() ?: return null)
        return if (minIndex >= 0 && minIndex < labels.size) labels[minIndex] else null
    }
}
