package com.orbits.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.auth.components.*

@Composable
fun SignupScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val signupState by viewModel.signupState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // Handle success navigation
    if (signupState.isRegistered) {
        onSignupSuccess()
    }

    AuthScreenContainer(
        title = "Create Account",
        subtitle = "Get started with Sphere Schedule",
        modifier = modifier
    ) {
        // ─── Display Name ────────────────────────────────────────
        AuthTextField(
            value = signupState.displayName,
            onValueChange = { viewModel.handleSignupEvent(AuthEvent.SignupDisplayNameChanged(it)) },
            label = "Display Name (Optional)",
            isError = signupState.displayNameError != null,
            errorMessage = signupState.displayNameError
        )

        // ─── First Name ──────────────────────────────────────────
        AuthTextField(
            value = signupState.firstName,
            onValueChange = { viewModel.handleSignupEvent(AuthEvent.SignupFirstNameChanged(it)) },
            label = "First Name (Optional)"
        )

        // ─── Last Name ───────────────────────────────────────────
        AuthTextField(
            value = signupState.lastName,
            onValueChange = { viewModel.handleSignupEvent(AuthEvent.SignupLastNameChanged(it)) },
            label = "Last Name (Optional)"
        )

        // ─── Email ───────────────────────────────────────────────
        AuthTextField(
            value = signupState.email,
            onValueChange = { viewModel.handleSignupEvent(AuthEvent.SignupEmailChanged(it)) },
            label = "Email",
            isError = signupState.emailError != null,
            errorMessage = signupState.emailError,
            keyboardType = KeyboardType.Email
        )

        // ─── Password ────────────────────────────────────────────
        AuthTextField(
            value = signupState.password,
            onValueChange = { viewModel.handleSignupEvent(AuthEvent.SignupPasswordChanged(it)) },
            label = "Password",
            isPassword = true,
            isError = signupState.passwordError != null,
            errorMessage = signupState.passwordError,
            isPasswordVisible = signupState.isPasswordVisible,
            onTogglePasswordVisibility = {
                viewModel.handleSignupEvent(AuthEvent.ToggleSignupPasswordVisibility)
            },
            supportingText = "Must be at least 8 characters"
        )

        // ─── Confirm Password ────────────────────────────────────
        AuthTextField(
            value = signupState.confirmPassword,
            onValueChange = { viewModel.handleSignupEvent(AuthEvent.SignupConfirmPasswordChanged(it)) },
            label = "Confirm Password",
            isPassword = true,
            isError = signupState.confirmPasswordError != null,
            errorMessage = signupState.confirmPasswordError,
            isPasswordVisible = signupState.isConfirmPasswordVisible,
            onTogglePasswordVisibility = {
                viewModel.handleSignupEvent(AuthEvent.ToggleSignupConfirmPasswordVisibility)
            }
        )

        // ─── Terms and Conditions ────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = signupState.acceptedTerms,
                onCheckedChange = {
                    viewModel.handleSignupEvent(AuthEvent.ToggleTermsAccepted)
                }
            )
            Text(
                text = "I agree to the Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ─── Error Message ──────────────────────────────────────
        signupState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // ─── Register Button ─────────────────────────────────────
        AuthButton(
            text = "Create Account",
            onClick = { viewModel.handleSignupEvent(AuthEvent.Register) },
            enabled = signupState.isFormValid && !signupState.isLoading,
            isLoading = signupState.isLoading
        )

        // ─── Divider ─────────────────────────────────────────────
        AuthDivider()

        // ─── Social Signup ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO: Google Sign-Up */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Google")
            }
            OutlinedButton(
                onClick = { /* TODO: Microsoft Sign-Up */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Microsoft")
            }
        }

        // ─── Skip Signup ──────────────────────────────────────────
        TextButton(
            onClick = { viewModel.handleSignupEvent(AuthEvent.SkipAuth) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Skip and use offline")
        }

        // ─── Login Link ──────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    viewModel.handleSignupEvent(AuthEvent.NavigateToLogin)
                    onNavigateToLogin()
                }
            )
        }
    }
}
