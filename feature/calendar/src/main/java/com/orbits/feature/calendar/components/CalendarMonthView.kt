package com.orbits.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.calendar.CalendarEvent
import java.time.LocalDate

@Composable
fun CalendarMonthView(
    yearMonth: String, // YYYY-MM
    events: List<CalendarEvent>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val date = LocalDate.parse(yearMonth + "-01")
    val firstDayOfMonth = date.withDayOfMonth(1)
    val daysInMonth = date.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1=Monday, 7=Sunday

    // Build grid with empty slots for days before first day
    val totalSlots = ((firstDayOfWeek - 1) + daysInMonth + 6) / 7 * 7
    val slots = (0 until totalSlots).map { index ->
        val dayOffset = index - (firstDayOfWeek - 1)
        if (dayOffset in 0 until daysInMonth) {
            firstDayOfMonth.plusDays(dayOffset.toLong())
        } else {
            null
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header: day names
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(slots) { day ->
                if (day != null) {
                    CalendarDayCell(
                        date = day,
                        isSelected = day.toString() == selectedDate,
                        events = events.filter { it.startDateTime.startsWith(day.toString()) },
                        onDateSelected = { onDateSelected(day.toString()) }
                    )
                } else {
                    Box(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    events: List<CalendarEvent>,
    onDateSelected: () -> Unit
) {
    val isToday = date == LocalDate.now()
    val hasEvents = events.isNotEmpty()

    Surface(
        modifier = Modifier
            .size(40.dp)
            .clickable { onDateSelected() },
        shape = MaterialTheme.shapes.small,
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onBackground
                }
            )
            if (hasEvents && !isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(4.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
    }
}
