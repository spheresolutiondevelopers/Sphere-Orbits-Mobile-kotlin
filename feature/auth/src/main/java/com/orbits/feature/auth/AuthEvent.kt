package com.orbits.feature.auth

/**
 * UI events for authentication screens.
 */
sealed class AuthEvent {
    // ─── Login Events ─────────────────────────────────────────────

    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data object TogglePasswordVisibility : AuthEvent()
    data object Login : AuthEvent()
    data object NavigateToSignup : AuthEvent()
    data object NavigateToForgotPassword : AuthEvent()
    data class LoginWithGoogle(val idToken: String) : AuthEvent()
    data class LoginWithMicrosoft(val idToken: String) : AuthEvent()
    data object SkipAuth : AuthEvent()
    data object DismissError : AuthEvent()

    // ─── Signup Events ────────────────────────────────────────────

    data class SignupEmailChanged(val email: String) : AuthEvent()
    data class SignupPasswordChanged(val password: String) : AuthEvent()
    data class SignupConfirmPasswordChanged(val password: String) : AuthEvent()
    data class SignupDisplayNameChanged(val displayName: String) : AuthEvent()
    data class SignupFirstNameChanged(val firstName: String) : AuthEvent()
    data class SignupLastNameChanged(val lastName: String) : AuthEvent()
    data object ToggleSignupPasswordVisibility : AuthEvent()
    data object ToggleSignupConfirmPasswordVisibility : AuthEvent()
    data object ToggleTermsAccepted : AuthEvent()
    data object Register : AuthEvent()
    data object NavigateToLogin : AuthEvent()
    data object DismissSignupError : AuthEvent()
    data class SignupWithGoogle(val idToken: String) : AuthEvent()
    data class SignupWithMicrosoft(val idToken: String) : AuthEvent()
}
