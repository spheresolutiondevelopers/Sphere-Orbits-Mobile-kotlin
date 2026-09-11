package com.orbits.domain.analytics

/**
 * Helper class for calculating productivity scores.
 */
object ProductivityScoreCalculator {

    /**
     * Calculate productivity score based on multiple factors.
     * @param completionRate Task completion rate (0-100)
     * @param focusHours Total focus hours
     * @param daysActive Number of active days
     * @param tasksCompleted Number of tasks completed
     * @param tasksCreated Number of tasks created
     * @param appointmentsAttended Number of appointments attended
     * @param meetingsAttended Number of meetings attended
     * @return Productivity score (0-100)
     */
    fun calculate(
        completionRate: Double,
        focusHours: Double,
        daysActive: Int,
        tasksCompleted: Int,
        tasksCreated: Int,
        appointmentsAttended: Int,
        meetingsAttended: Int
    ): Int {
        // Weight factors
        val completionWeight = 0.35
        val focusWeight = 0.20
        val taskWeight = 0.25
        val engagementWeight = 0.20

        // Normalize completion rate (already 0-100)
        val completionScore = completionRate.coerceIn(0.0, 100.0)

        // Normalize focus hours (max 8 hours per day)
        val maxFocusHours = daysActive.toDouble() * 8.0
        val focusScore = if (maxFocusHours > 0) {
            (focusHours / maxFocusHours * 100).coerceIn(0.0, 100.0)
        } else {
            0.0
        }

        // Normalize tasks (max 10 tasks per day)
        val maxTasks = daysActive * 10
        val taskScore = if (maxTasks > 0) {
            ((tasksCompleted.toDouble() / maxTasks) * 100).coerceIn(0.0, 100.0)
        } else {
            0.0
        }

        // Normalize engagement (max 5 appointments + 5 meetings per week)
        val maxEngagement = daysActive * 2
        val engagementScore = if (maxEngagement > 0) {
            ((appointmentsAttended + meetingsAttended).toDouble() / maxEngagement * 100).coerceIn(0.0, 100.0)
        } else {
            0.0
        }

        // Calculate weighted average
        val rawScore = (completionScore * completionWeight) +
                (focusScore * focusWeight) +
                (taskScore * taskWeight) +
                (engagementScore * engagementWeight)

        return rawScore.toInt().coerceIn(0, 100)
    }

    /**
     * Calculate productivity score for a period.
     */
    fun calculateForPeriod(stats: ProductivityStats): Int {
        return calculate(
            completionRate = stats.tasksCompletionRate,
            focusHours = stats.totalFocusHours,
            daysActive = calculateDaysActive(stats.startDate, stats.endDate),
            tasksCompleted = stats.tasksCompleted,
            tasksCreated = stats.tasksCreated,
            appointmentsAttended = stats.appointmentsAttended,
            meetingsAttended = stats.meetingsAttended
        )
    }

    private fun calculateDaysActive(startDate: String, endDate: String): Int {
        return try {
            val start = java.time.LocalDate.parse(startDate)
            val end = java.time.LocalDate.parse(endDate)
            java.time.Period.between(start, end).days + 1
        } catch (e: Exception) {
            1
        }
    }

    /**
     * Get the score level description.
     */
    fun getScoreLevel(score: Int): String {
        return when {
            score >= 90 -> "Exceptional"
            score >= 75 -> "Excellent"
            score >= 60 -> "Good"
            score >= 40 -> "Fair"
            score >= 20 -> "Needs Improvement"
            else -> "Low"
        }
    }

    /**
     * Get the score color code.
     */
    fun getScoreColor(score: Int): String {
        return when {
            score >= 75 -> "#2DD4A0" // Green
            score >= 50 -> "#FFD166" // Yellow
            else -> "#FF6B6B" // Red
        }
    }

    /**
     * Get the score emoji.
     */
    fun getScoreEmoji(score: Int): String {
        return when {
            score >= 75 -> "🌟"
            score >= 50 -> "💪"
            else -> "📉"
        }
    }
}
