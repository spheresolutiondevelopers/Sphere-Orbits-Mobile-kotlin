package com.orbits.feature.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.core.theme.SphereTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsState()
    val isEditMode = formState.isEditMode

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            onSaveSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)) // Backdrop
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1C1C6A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF404090))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                                    RoundedCornerShape(11.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isEditMode) Icons.Default.Edit else Icons.Default.Add,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                if (isEditMode) "Edit task" else "New task",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Fill in the details below",
                                style = MaterialTheme.typography.labelSmall,
                                color = SphereTheme.colors.textTertiary
                            )
                        }
                    }
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFF1A0568), CircleShape)
                            .border(1.dp, Color(0xFF1E0A60), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = SphereTheme.colors.textSecondary, modifier = Modifier.size(14.dp))
                    }
                }

                HorizontalDivider(color = Color(0xFF1E0A60))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title
                    FormLabel(Icons.Default.Title, "Title *")
                    OutlinedTextField(
                        value = formState.title,
                        onValueChange = { viewModel.handleEvent(TaskEvent.FormTitleChanged(it)) },
                        placeholder = { Text("e.g., Sphere meeting", color = SphereTheme.colors.textTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape,
                        colors = formFieldColors()
                    )

                    // Category & Priority
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.weight(1f)) {
                            FormLabel(Icons.Default.Tag, "Category")
                            // Simplified select
                            OutlinedTextField(
                                value = formState.taskType,
                                onValueChange = { viewModel.handleEvent(TaskEvent.FormTaskTypeChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = CircleShape,
                                colors = formFieldColors(),
                                readOnly = false
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            FormLabel(Icons.Default.Bolt, "Priority")
                            OutlinedTextField(
                                value = formState.priorityLevel,
                                onValueChange = { viewModel.handleEvent(TaskEvent.FormPriorityChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = CircleShape,
                                colors = formFieldColors()
                            )
                        }
                    }

                    // Date & Time
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.weight(1f)) {
                            FormLabel(Icons.Default.Schedule, "Date *")
                            OutlinedTextField(
                                value = formState.dueDate ?: "",
                                onValueChange = { viewModel.handleEvent(TaskEvent.FormDueDateChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = CircleShape,
                                colors = formFieldColors()
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            FormLabel(Icons.Default.AccessTime, "Time")
                            OutlinedTextField(
                                value = formState.dueTime ?: "",
                                onValueChange = { viewModel.handleEvent(TaskEvent.FormDueTimeChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = CircleShape,
                                colors = formFieldColors()
                            )
                        }
                    }

                    // Description
                    FormLabel(Icons.Default.Notes, "Description")
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { viewModel.handleEvent(TaskEvent.FormDescriptionChanged(it)) },
                        placeholder = { Text("Optional notes...", color = SphereTheme.colors.textTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = formFieldColors()
                    )
                }

                // Footer
                HorizontalDivider(color = Color(0xFF1E0A60))
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141240)),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38368A))
                    ) {
                        Text("Cancel", color = SphereTheme.colors.textSecondary)
                    }
                    
                    Button(
                        onClick = { viewModel.handleEvent(TaskEvent.FormSubmit) },
                        modifier = Modifier.weight(2f),
                        enabled = formState.isFormValid && !formState.isLoading,
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Create Task", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
            fontWeight = FontWeight.Bold,
            color = SphereTheme.colors.textTertiary
        )
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF0C0340),
    unfocusedContainerColor = Color(0xFF0C0340),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color(0xFF5050A0)
)
