package com.orbits.feature.events

data class EventState(
    val isLoading: Boolean = false,
    val events: List<String> = emptyList()
)
