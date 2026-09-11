package com.orbits.feature.dashboard

import com.orbits.domain.analytics.DashboardStats
import com.orbits.domain.analytics.ProductivityInsight
import com.orbits.domain.tasks.Task
import com.orbits.domain.appointments.Appointment
import com.orbits.domain.meetings.Meeting
import com.orbits.domain.auth.AuthUser

/**
 * UI state for the dashboard screen.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val user: AuthUser? = null,
    val dashboardStats: DashboardStats? = null,
    val todayTasks: List<Task> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val todayAppointments: List<Appointment> = emptyList(),
    val upcomingAppointments: List<Appointment> = emptyList(),
    val liveMeetings: List<Meeting> = emptyList(),
    val upcomingMeetings: List<Meeting> = emptyList(),
    val topInsight: ProductivityInsight? = null,
    val refreshError: String? = null,
    val selectedDate: String = java.time.LocalDate.now().toString()
)

/**
 * Quick action types.
 */
enum class QuickAction(val displayName: String, val icon: String) {
    TASK("New Task", "📝"),
    APPOINTMENT("Schedule", "📅"),
    MEETING("Meet", "👥"),
    NOTE("Quick Note", "📌")
}
