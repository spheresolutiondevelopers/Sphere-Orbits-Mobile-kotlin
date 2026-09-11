package com.orbits.feature.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.calendar.components.*

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onNavigateToEventDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val calendarAction = viewModel::handleEvent

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── View Toggle ────────────────────────────────────
        CalendarViewToggle(
            selectedMode = state.viewMode,
            onModeSelected = { mode ->
                calendarAction(CalendarViewModel.CalendarEventAction.ChangeViewMode(mode))
            }
        )

        // ─── Date Navigation ───────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val date = java.time.LocalDate.parse(state.currentDate)
                val newDate = when (state.viewMode) {
                    CalendarViewMode.DAY -> date.minusDays(1)
                    CalendarViewMode.WEEK -> date.minusWeeks(1)
                    CalendarViewMode.MONTH -> date.minusMonths(1)
                    CalendarViewMode.AGENDA -> date.minusDays(7)
                }
                calendarAction(CalendarViewModel.CalendarEventAction.ChangeDate(newDate.toString()))
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }

            Text(
                text = formatDateRange(state.currentDate, state.viewMode),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = {
                val date = java.time.LocalDate.parse(state.currentDate)
                val newDate = when (state.viewMode) {
                    CalendarViewMode.DAY -> date.plusDays(1)
                    CalendarViewMode.WEEK -> date.plusWeeks(1)
                    CalendarViewMode.MONTH -> date.plusMonths(1)
                    CalendarViewMode.AGENDA -> date.plusDays(7)
                }
                calendarAction(CalendarViewModel.CalendarEventAction.ChangeDate(newDate.toString()))
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }

        // ─── Error Message ──────────────────────────────────
        if (state.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { calendarAction(CalendarViewModel.CalendarEventAction.DismissError) }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }

        // ─── Content based on view mode ────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            when (state.viewMode) {
                CalendarViewMode.DAY -> {
                    CalendarDayView(
                        date = state.currentDate,
                        events = state.events,
                        onEventClick = { event ->
                            onNavigateToEventDetail(event.id)
                        }
                    )
                }
                CalendarViewMode.WEEK -> {
                    CalendarWeekView(
                        startDate = state.currentDate,
                        events = state.events,
                        selectedDate = state.currentDate,
                        onDateSelected = { date ->
                            calendarAction(CalendarViewModel.CalendarEventAction.ChangeDate(date))
                        }
                    )
                }
                CalendarViewMode.MONTH -> {
                    CalendarMonthView(
                        yearMonth = state.currentDate.substring(0, 7),
                        events = state.events,
                        selectedDate = state.currentDate,
                        onDateSelected = { date ->
                            calendarAction(CalendarViewModel.CalendarEventAction.ChangeDate(date))
                        }
                    )
                }
                CalendarViewMode.AGENDA -> {
                    CalendarAgendaView(
                        events = state.events,
                        onEventClick = { event ->
                            onNavigateToEventDetail(event.id)
                        }
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private fun formatDateRange(date: String, mode: CalendarViewMode): String {
    val localDate = java.time.LocalDate.parse(date)
    return when (mode) {
        CalendarViewMode.DAY -> localDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"))
        CalendarViewMode.WEEK -> {
            val start = localDate.minusDays(localDate.dayOfWeek.value.toLong() - 1)
            val end = start.plusDays(6)
            "${start.format(java.time.format.DateTimeFormatter.ofPattern("MMM d"))} - ${end.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
        }
        CalendarViewMode.MONTH -> localDate.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))
        CalendarViewMode.AGENDA -> "Upcoming Events"
    }
}
