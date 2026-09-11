package com.orbits.feature.events

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(EventState())
    val state: StateFlow<EventState> = _state
}
