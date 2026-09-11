package com.orbits.feature.meetings

import com.orbits.domain.meetings.Meeting

/**
 * UI state for the meetings feature.
 */
data class MeetingsUiState(
    val isLoading: Boolean = true,
    val meetings: List<Meeting> = emptyList(),
    val filteredMeetings: List<Meeting> = emptyList(),
    val selectedFilter: MeetingFilter = MeetingFilter.ALL,
    val selectedMeetingId: String? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * Meeting filter options.
 */
enum class MeetingFilter(val displayName: String) {
    ALL("All"),
    SCHEDULED("Scheduled"),
    LIVE("Live"),
    ENDED("Ended"),
    CANCELLED("Cancelled"),
    UPCOMING("Upcoming"),
    RECENT("Recent")
}

/**
 * Meeting action states.
 */
enum class MeetingActionState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}
