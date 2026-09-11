package com.orbits.feature.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.calendar.CalendarEvent

@Composable
fun CalendarAgendaView(
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedEvents = events.sortedBy { it.startDateTime }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedEvents) { event ->
            CalendarEventItem(
                event = event,
                onClick = { onEventClick(event) },
                showDate = true
            )
        }
    }
}
