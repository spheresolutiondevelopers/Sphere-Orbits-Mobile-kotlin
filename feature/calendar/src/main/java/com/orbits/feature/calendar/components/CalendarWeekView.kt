package com.orbits.feature.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.calendar.CalendarEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalendarWeekView(
    startDate: String, // YYYY-MM-DD
    events: List<CalendarEvent>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val start = LocalDate.parse(startDate)
    val days = (0..6).map { start.plusDays(it.toLong()) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Week header with dates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { date ->
                val isSelected = date.toString() == selectedDate
                val isToday = date == LocalDate.now()
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("E")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onDateSelected(date.toString()) },
                        shape = MaterialTheme.shapes.small,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isToday -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onBackground
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Events for the week
        val dayEvents = events.groupBy { it.startDateTime.substring(0, 10) }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            days.forEach { date ->
                val dateKey = date.toString()
                val dayEventsList = dayEvents[dateKey] ?: emptyList()

                item {
                    CalendarDayEventsRow(
                        date = dateKey,
                        events = dayEventsList,
                        onEventClick = { /* handled by parent */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayEventsRow(
    date: String,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
    if (events.isEmpty()) {
        Text(
            text = "No events",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    } else {
        events.forEach { event ->
            CalendarEventItem(
                event = event,
                onClick = { onEventClick(event) }
            )
        }
    }
}
