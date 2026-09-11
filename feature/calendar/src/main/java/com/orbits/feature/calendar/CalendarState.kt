package com.orbits.feature.calendar

import com.orbits.domain.calendar.CalendarEvent

/**
 * UI state for the calendar feature.
 */
data class CalendarUiState(
    val isLoading: Boolean = true,
    val currentDate: String = java.time.LocalDate.now().toString(),
    val viewMode: CalendarViewMode = CalendarViewMode.WEEK,
    val events: List<CalendarEvent> = emptyList(),
    val selectedEventId: String? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * Calendar view modes.
 */
enum class CalendarViewMode(val displayName: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    AGENDA("Agenda")
}

/**
 * Calendar filter options.
 */
enum class CalendarFilter(val displayName: String) {
    ALL("All"),
    UPCOMING("Upcoming"),
    PAST("Past"),
    TODAY("Today"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled")
}
