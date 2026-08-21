package com.orbits.domain.calendar

/**
 * Recurrence rule for recurring events.
 * Follows the RRULE format from iCalendar RFC 5545.
 */
data class RecurrenceRule(
    val frequency: Frequency,
    val interval: Int = 1,
    val count: Int? = null,
    val until: String? = null,
    val byDay: List<Weekday>? = null,
    val byMonthDay: List<Int>? = null,
    val byMonth: List<Int>? = null,
    val byWeekNo: List<Int>? = null,
    val byYearDay: List<Int>? = null,
    val bySetPos: List<Int>? = null,
    val weekStart: Weekday = Weekday.MONDAY
) {

    enum class Frequency {
        SECONDLY, MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    enum class Weekday {
        MO, TU, WE, TH, FR, SA, SU;

        companion object {
            fun fromString(value: String): Weekday? {
                return values().find { it.name.equals(value, ignoreCase = true) }
            }
        }
    }

    /**
     * Convert to RRULE string.
     */
    fun toRRule(): String {
        val parts = mutableListOf<String>()
        parts.add("FREQ=$frequency")

        if (interval > 1) {
            parts.add("INTERVAL=$interval")
        }

        count?.let { parts.add("COUNT=$it") }
        until?.let { parts.add("UNTIL=$it") }

        byDay?.let { days ->
            parts.add("BYDAY=${days.joinToString(",")}")
        }

        byMonthDay?.let { days ->
            parts.add("BYMONTHDAY=${days.joinToString(",")}")
        }

        byMonth?.let { months ->
            parts.add("BYMONTH=${months.joinToString(",")}")
        }

        if (weekStart != Weekday.MONDAY) {
            parts.add("WKST=$weekStart")
        }

        return parts.joinToString(";")
    }

    /**
     * Parse RRULE string.
     */
    companion object {
        fun fromRRule(rrule: String): RecurrenceRule? {
            if (rrule.isBlank()) return null

            return try {
                val parts = rrule.split(";").associate {
                    val keyValue = it.split("=")
                    keyValue[0] to keyValue.getOrNull(1) ?: ""
                }

                val frequency = parts["FREQ"]?.let {
                    Frequency.valueOf(it)
                } ?: return null

                val interval = parts["INTERVAL"]?.toIntOrNull() ?: 1
                val count = parts["COUNT"]?.toIntOrNull()
                val until = parts["UNTIL"]

                val byDay = parts["BYDAY"]?.split(",")?.mapNotNull {
                    Weekday.fromString(it)
                }

                val byMonthDay = parts["BYMONTHDAY"]?.split(",")?.mapNotNull {
                    it.toIntOrNull()
                }

                val byMonth = parts["BYMONTH"]?.split(",")?.mapNotNull {
                    it.toIntOrNull()
                }

                val weekStart = parts["WKST"]?.let {
                    Weekday.fromString(it)
                } ?: Weekday.MONDAY

                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    count = count,
                    until = until,
                    byDay = byDay,
                    byMonthDay = byMonthDay,
                    byMonth = byMonth,
                    weekStart = weekStart
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}