package com.orbits.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.profileState.collectAsState()

    // Handle success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSaveSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── Display Name ────────────────────────────────────
            OutlinedTextField(
                value = state.displayName,
                onValueChange = {
                    viewModel.handleEvent(SettingsEvent.ProfileDisplayNameChanged(it))
                },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.displayNameError != null,
                supportingText = {
                    if (state.displayNameError != null) {
                        Text(state.displayNameError ?: "")
                    }
                },
                singleLine = true
            )

            // ─── First Name ─────────────────────────────────────
            OutlinedTextField(
                value = state.firstName,
                onValueChange = {
                    viewModel.handleEvent(SettingsEvent.ProfileFirstNameChanged(it))
                },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ─── Last Name ──────────────────────────────────────
            OutlinedTextField(
                value = state.lastName,
                onValueChange = {
                    viewModel.handleEvent(SettingsEvent.ProfileLastNameChanged(it))
                },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ─── Email ──────────────────────────────────────────
            OutlinedTextField(
                value = state.email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true
            )

            // ─── Phone Number ──────────────────────────────────
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = {
                    viewModel.handleEvent(SettingsEvent.ProfilePhoneNumberChanged(it))
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.phoneNumberError != null,
                supportingText = {
                    if (state.phoneNumberError != null) {
                        Text(state.phoneNumberError ?: "")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true
            )

            // ─── Error Message ──────────────────────────────────
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
