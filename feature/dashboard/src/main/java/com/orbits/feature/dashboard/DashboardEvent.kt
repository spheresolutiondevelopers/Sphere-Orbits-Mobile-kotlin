package com.orbits.feature.dashboard

/**
 * UI events for the dashboard screen.
 */
sealed class DashboardEvent {
    data object Refresh : DashboardEvent()
    data object NavigateToTasks : DashboardEvent()
    data object NavigateToCalendar : DashboardEvent()
    data object NavigateToMeetings : DashboardEvent()
    data object NavigateToAppointments : DashboardEvent()
    data object NavigateToChat : DashboardEvent()
    data object NavigateToNotes : DashboardEvent()
    data object NavigateToAnalytics : DashboardEvent()
    data object NavigateToSettings : DashboardEvent()
    data class NavigateToTaskDetail(val taskId: String) : DashboardEvent()
    data object DismissError : DashboardEvent()
    data class SelectDate(val date: String) : DashboardEvent()
    data class QuickActionSelected(val action: QuickAction) : DashboardEvent()
    data object ViewAllTasks : DashboardEvent()
    data object ViewAllAppointments : DashboardEvent()
    data object ViewAllMeetings : DashboardEvent()
}
