package com.orbits.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.domain.calendar.CalendarRepository
import com.orbits.domain.calendar.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        loadEvents()
    }

    fun handleEvent(event: CalendarEventAction) {
        when (event) {
            is CalendarEventAction.LoadEvents -> loadEvents()
            is CalendarEventAction.ChangeDate -> changeDate(event.date)
            is CalendarEventAction.ChangeViewMode -> changeViewMode(event.mode)
            is CalendarEventAction.SelectEvent -> selectEvent(event.eventId)
            is CalendarEventAction.Refresh -> refresh()
            is CalendarEventAction.DismissError -> dismissError()
        }
    }

    private fun loadEvents() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val date = LocalDate.parse(_state.value.currentDate)
                val startDate = when (_state.value.viewMode) {
                    CalendarViewMode.DAY -> date.toString()
                    CalendarViewMode.WEEK -> {
                        val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
                        startOfWeek.toString()
                    }
                    CalendarViewMode.MONTH -> {
                        date.withDayOfMonth(1).toString()
                    }
                    CalendarViewMode.AGENDA -> date.toString()
                }

                val endDate = when (_state.value.viewMode) {
                    CalendarViewMode.DAY -> date.toString()
                    CalendarViewMode.WEEK -> {
                        val endOfWeek = date.plusDays(7 - date.dayOfWeek.value.toLong())
                        endOfWeek.toString()
                    }
                    CalendarViewMode.MONTH -> {
                        date.withDayOfMonth(date.lengthOfMonth()).toString()
                    }
                    CalendarViewMode.AGENDA -> date.plusDays(30).toString()
                }

                calendarRepository.getEventsInDateRange(startDate, endDate)
                    .collect { events ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                events = events,
                                errorMessage = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load events"
                    )
                }
            }
        }
    }

    private fun changeDate(date: String) {
        _state.update { it.copy(currentDate = date) }
        loadEvents()
    }

    private fun changeViewMode(mode: CalendarViewMode) {
        _state.update { it.copy(viewMode = mode) }
        loadEvents()
    }

    private fun selectEvent(eventId: String) {
        _state.update { it.copy(selectedEventId = eventId) }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadEvents()
        _state.update { it.copy(isRefreshing = false) }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // ─── UI Events ──────────────────────────────────────────────

    sealed class CalendarEventAction {
        data object LoadEvents : CalendarEventAction()
        data class ChangeDate(val date: String) : CalendarEventAction()
        data class ChangeViewMode(val mode: CalendarViewMode) : CalendarEventAction()
        data class SelectEvent(val eventId: String) : CalendarEventAction()
        data object Refresh : CalendarEventAction()
        data object DismissError : CalendarEventAction()
    }
}
