package com.orbits.domain.calendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Helper for working with recurrence rules.
 */
object RecurrenceHelper {

    /**
     * Generate the next occurrence of a recurring event.
     * @param recurrenceRule The recurrence rule
     * @param currentStart The current event start date/time
     * @param currentEnd The current event end date/time
     * @return The next occurrence as a pair of start and end, or null if no more occurrences
     */
    fun getNextOccurrence(
        recurrenceRule: RecurrenceRule,
        currentStart: String,
        currentEnd: String
    ): Pair<String, String>? {
        return try {
            val start = LocalDateTime.parse(currentStart)
            val end = LocalDateTime.parse(currentEnd)
            val duration = java.time.Duration.between(start, end)

            val nextStart = when (recurrenceRule.frequency) {
                RecurrenceRule.Frequency.DAILY -> start.plusDays(recurrenceRule.interval.toLong())
                RecurrenceRule.Frequency.WEEKLY -> start.plusWeeks(recurrenceRule.interval.toLong())
                RecurrenceRule.Frequency.MONTHLY -> start.plusMonths(recurrenceRule.interval.toLong())
                RecurrenceRule.Frequency.YEARLY -> start.plusYears(recurrenceRule.interval.toLong())
                else -> start // MINUTELY, HOURLY, SECONDLY not supported for event recurrences
            }

            val nextEnd = nextStart.plus(duration)

            // Check if we've exceeded the count or until date
            // For simplicity, we don't check these constraints here

            Pair(
                nextStart.toString(),
                nextEnd.toString()
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generate a list of occurrence dates for a recurring event.
     * @param recurrenceRule The recurrence rule
     * @param startDate The start date of the first occurrence
     * @param count The number of occurrences to generate
     * @return List of occurrence dates
     */
    fun generateOccurrences(
        recurrenceRule: RecurrenceRule,
        startDate: String,
        count: Int = 10
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var current = LocalDate.parse(startDate)

        repeat(count) {
            dates.add(current)
            current = when (recurrenceRule.frequency) {
                RecurrenceRule.Frequency.DAILY -> current.plusDays(recurrenceRule.interval.toLong())
                RecurrenceRule.Frequency.WEEKLY -> current.plusWeeks(recurrenceRule.interval.toLong())
                RecurrenceRule.Frequency.MONTHLY -> current.plusMonths(recurrenceRule.interval.toLong())
                RecurrenceRule.Frequency.YEARLY -> current.plusYears(recurrenceRule.interval.toLong())
                else -> current // Not supported
            }
        }

        return dates
    }

    /**
     * Check if a date is a valid occurrence of a recurring event.
     * @param recurrenceRule The recurrence rule
     * @param startDate The start date of the first occurrence
     * @param checkDate The date to check
     * @return true if the date is a valid occurrence
     */
    fun isValidOccurrence(
        recurrenceRule: RecurrenceRule,
        startDate: String,
        checkDate: String
    ): Boolean {
        if (checkDate < startDate) return false

        val start = LocalDate.parse(startDate)
        val check = LocalDate.parse(checkDate)
        val daysBetween = java.time.Period.between(start, check).days

        return when (recurrenceRule.frequency) {
            RecurrenceRule.Frequency.DAILY -> daysBetween % recurrenceRule.interval == 0
            RecurrenceRule.Frequency.WEEKLY -> {
                val weeksBetween = daysBetween / 7
                val remainderDays = daysBetween % 7
                remainderDays == 0 && weeksBetween % recurrenceRule.interval == 0
            }
            RecurrenceRule.Frequency.MONTHLY -> {
                val monthsBetween = (check.year - start.year) * 12 + (check.monthValue - start.monthValue)
                monthsBetween % recurrenceRule.interval == 0 && check.dayOfMonth == start.dayOfMonth
            }
            RecurrenceRule.Frequency.YEARLY -> {
                val yearsBetween = check.year - start.year
                yearsBetween % recurrenceRule.interval == 0 && check.dayOfYear == start.dayOfYear
            }
            else -> false
        }
    }
}