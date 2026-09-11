package com.orbits.feature.meetings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.domain.meetings.Meeting
import com.orbits.domain.meetings.MeetingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MeetingsUiState())
    val state: StateFlow<MeetingsUiState> = _state.asStateFlow()

    private var allMeetings: List<Meeting> = emptyList()

    init {
        loadMeetings()
    }

    fun handleEvent(event: MeetingEvent) {
        when (event) {
            MeetingEvent.LoadMeetings -> loadMeetings()
            MeetingEvent.Refresh -> refresh()
            is MeetingEvent.SelectFilter -> applyFilter(event.filter)
            is MeetingEvent.SelectMeeting -> selectMeeting(event.meetingId)
            is MeetingEvent.NavigateToMeetingDetail -> { /* Navigation handled by NavGraph */ }
            MeetingEvent.NavigateToCreate -> { /* Navigation handled by NavGraph */ }
            MeetingEvent.DismissError -> dismissError()
            is MeetingEvent.JoinMeeting -> joinMeeting(event.meetingId)
            is MeetingEvent.UpdateMeetingStatus -> updateMeetingStatus(event.meetingId, event.status)
        }
    }

    private fun loadMeetings() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                meetingRepository.getMeetings().collect { meetings ->
                    allMeetings = meetings
                    applyFilterToState(_state.value.selectedFilter)
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load meetings"
                    )
                }
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadMeetings()
        _state.update { it.copy(isRefreshing = false) }
    }

    private fun applyFilter(filter: MeetingFilter) {
        _state.update { it.copy(selectedFilter = filter) }
        applyFilterToState(filter)
    }

    private fun applyFilterToState(filter: MeetingFilter) {
        val filtered = when (filter) {
            MeetingFilter.ALL -> allMeetings
            MeetingFilter.SCHEDULED -> allMeetings.filter { it.status == "scheduled" }
            MeetingFilter.LIVE -> allMeetings.filter { it.status == "live" }
            MeetingFilter.ENDED -> allMeetings.filter { it.status == "ended" }
            MeetingFilter.CANCELLED -> allMeetings.filter { it.status == "cancelled" }
            MeetingFilter.UPCOMING -> allMeetings.filter { it.isUpcoming() }
            MeetingFilter.RECENT -> allMeetings.filter { it.isPast() }
        }
        _state.update { it.copy(filteredMeetings = filtered) }
    }

    private fun selectMeeting(meetingId: String) {
        _state.update { it.copy(selectedMeetingId = meetingId) }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun joinMeeting(meetingId: String) {
        viewModelScope.launch {
            val result = meetingRepository.joinMeeting(meetingId)
            when (result) {
                is Result.Success -> {
                    // Open meeting link
                    Logger.d("Meeting", "Join meeting: ${result.data}")
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to join meeting"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun updateMeetingStatus(meetingId: String, status: String) {
        viewModelScope.launch {
            val result = meetingRepository.updateMeetingStatus(meetingId, status)
            when (result) {
                is Result.Success -> loadMeetings()
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update meeting status"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }
}
