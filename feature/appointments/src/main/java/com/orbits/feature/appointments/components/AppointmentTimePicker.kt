package com.orbits.feature.appointments.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun AppointmentTimePicker(
    startDateTime: String,
    endDateTime: String,
    onStartDateTimeChange: (String) -> Unit,
    onEndDateTimeChange: (String) -> Unit,
    allDayEvent: Boolean,
    onAllDayToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All-day toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "All day event",
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = allDayEvent,
                onCheckedChange = onAllDayToggle
            )
        }

        // Start and end times
        if (!allDayEvent) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startDateTime,
                    onValueChange = onStartDateTimeChange,
                    label = { Text("Start (YYYY-MM-DD HH:MM)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(LocalDate.now().toString() + " 09:00") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = endDateTime,
                    onValueChange = onEndDateTimeChange,
                    label = { Text("End (YYYY-MM-DD HH:MM)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(LocalDate.now().toString() + " 10:00") },
                    singleLine = true
                )
            }
        } else {
            OutlinedTextField(
                value = startDateTime,
                onValueChange = onStartDateTimeChange,
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(LocalDate.now().toString()) },
                singleLine = true
            )
        }
    }
}
