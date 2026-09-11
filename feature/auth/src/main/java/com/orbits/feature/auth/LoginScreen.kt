package com.orbits.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.auth.components.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loginState by viewModel.loginState.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Handle success navigation
    if (loginState.isLoggedIn) {
        onLoginSuccess()
    }

    AuthScreenContainer(
        title = "Welcome Back",
        subtitle = "Sign in to continue to Sphere Schedule",
        modifier = modifier
    ) {
        // ─── Email Field ──────────────────────────────────────────
        AuthTextField(
            value = loginState.email,
            onValueChange = { viewModel.handleLoginEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            isError = loginState.emailError != null,
            errorMessage = loginState.emailError,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )

        // ─── Password Field ──────────────────────────────────────
        AuthTextField(
            value = loginState.password,
            onValueChange = { viewModel.handleLoginEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            isPassword = true,
            isError = loginState.passwordError != null,
            errorMessage = loginState.passwordError,
            isPasswordVisible = loginState.isPasswordVisible,
            onTogglePasswordVisibility = {
                viewModel.handleLoginEvent(AuthEvent.TogglePasswordVisibility)
            }
        )

        // ─── Forgot Password ─────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    viewModel.handleLoginEvent(AuthEvent.NavigateToForgotPassword)
                }
            )
        }

        // ─── Error Message ──────────────────────────────────────
        loginState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // ─── Login Button ────────────────────────────────────────
        AuthButton(
            text = "Sign In",
            onClick = { viewModel.handleLoginEvent(AuthEvent.Login) },
            enabled = loginState.isFormValid && !loginState.isLoading,
            isLoading = loginState.isLoading
        )

        // ─── Divider ─────────────────────────────────────────────
        AuthDivider()

        // ─── Social Login ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Google button (simplified)
            OutlinedButton(
                onClick = { /* TODO: Google Sign-In */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Google")
            }

            // Microsoft button (simplified)
            OutlinedButton(
                onClick = { /* TODO: Microsoft Sign-In */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Microsoft")
            }
        }

        // ─── Skip Login ──────────────────────────────────────────
        TextButton(
            onClick = { viewModel.handleLoginEvent(AuthEvent.SkipAuth) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Skip and use offline")
        }

        // ─── Sign Up Link ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    viewModel.handleLoginEvent(AuthEvent.NavigateToSignup)
                    onNavigateToSignup()
                }
            )
        }
    }
}
