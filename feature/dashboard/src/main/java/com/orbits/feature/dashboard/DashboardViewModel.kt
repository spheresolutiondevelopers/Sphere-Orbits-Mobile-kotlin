package com.orbits.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import com.orbits.domain.analytics.AnalyticsRepository
import com.orbits.domain.analytics.DashboardStats
import com.orbits.domain.analytics.ProductivityInsight
import com.orbits.domain.appointments.Appointment
import com.orbits.domain.appointments.AppointmentRepository
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.meetings.Meeting
import com.orbits.domain.meetings.MeetingRepository
import com.orbits.domain.tasks.Task
import com.orbits.domain.tasks.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val appointmentRepository: AppointmentRepository,
    private val meetingRepository: MeetingRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun handleEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.Refresh -> loadDashboard()
            DashboardEvent.DismissError -> dismissError()
            is DashboardEvent.SelectDate -> selectDate(event.date)
            DashboardEvent.NavigateToTasks -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToCalendar -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToMeetings -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToAppointments -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToChat -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToNotes -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToAnalytics -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.NavigateToSettings -> { /* Navigation handled by NavGraph */ }
            is DashboardEvent.NavigateToTaskDetail -> { /* Navigation handled by NavGraph */ }
            is DashboardEvent.QuickActionSelected -> handleQuickAction(event.action)
            DashboardEvent.ViewAllTasks -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.ViewAllAppointments -> { /* Navigation handled by NavGraph */ }
            DashboardEvent.ViewAllMeetings -> { /* Navigation handled by NavGraph */ }
        }
    }

    private fun loadDashboard() {
        _state.update { it.copy(isLoading = true, refreshError = null) }

        viewModelScope.launch {
            try {
                // Load all dashboard data in parallel
                val currentUser = authRepository.getCurrentUser()
                val statsResult = analyticsRepository.getDashboardStats()
                val todayDate = LocalDate.now().toString()
                val todayEnd = LocalDate.now().plusDays(1).toString()

                // Load today's tasks
                val todayTasks = taskRepository.getTasksInDateRange(todayDate, todayEnd).first()
                    .filter { !it.isCompleted() && !it.isDeleted }

                // Load upcoming tasks (next 7 days)
                val upcomingTasks = taskRepository.getUpcomingTasks().first()
                    .filter { !it.isCompleted() && !it.isDeleted && !it.isOverdue() }

                // Load today's appointments
                val todayAppointments = appointmentRepository.getAppointmentsForDate(todayDate).first()
                    .filter { it.status != "cancelled" && it.status != "completed" }

                // Load upcoming appointments
                val upcomingAppointments = appointmentRepository.getAppointmentsInDateRange(
                    todayDate, LocalDate.now().plusDays(7).toString()
                ).first()
                    .filter { it.status != "cancelled" && it.status != "completed" && it.startDateTime > nowUtc() }

                // Load live meetings
                val liveMeetings = meetingRepository.getLiveMeetings().first()

                // Load upcoming meetings
                val upcomingMeetings = meetingRepository.getUpcomingMeetings().first()
                    .filter { it.status != "cancelled" }

                // Get top insight from stats
                val topInsight = (statsResult as? Result.Success)?.data?.insights?.firstOrNull()

                _state.update {
                    it.copy(
                        isLoading = false,
                        user = currentUser,
                        dashboardStats = (statsResult as? Result.Success)?.data,
                        todayTasks = todayTasks.take(5),
                        upcomingTasks = upcomingTasks.take(8),
                        todayAppointments = todayAppointments.take(3),
                        upcomingAppointments = upcomingAppointments.take(5),
                        liveMeetings = liveMeetings,
                        upcomingMeetings = upcomingMeetings.take(3),
                        topInsight = topInsight,
                        refreshError = null
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        refreshError = e.message ?: "Failed to load dashboard"
                    )
                }
            }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(refreshError = null) }
    }

    private fun selectDate(date: String) {
        _state.update { it.copy(selectedDate = date) }
        // Reload tasks for the selected date
        viewModelScope.launch {
            try {
                val selectedTasks = taskRepository.getTasksInDateRange(date, date).first()
                    .filter { !it.isCompleted() && !it.isDeleted }
                _state.update { it.copy(todayTasks = selectedTasks.take(5)) }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun handleQuickAction(action: QuickAction) {
        when (action) {
            QuickAction.TASK -> handleEvent(DashboardEvent.NavigateToTasks)
            QuickAction.APPOINTMENT -> handleEvent(DashboardEvent.NavigateToAppointments)
            QuickAction.MEETING -> handleEvent(DashboardEvent.NavigateToMeetings)
            QuickAction.NOTE -> handleEvent(DashboardEvent.NavigateToNotes)
        }
    }
}
