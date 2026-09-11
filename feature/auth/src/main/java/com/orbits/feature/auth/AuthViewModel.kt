package com.orbits.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.extensions.isValidEmail
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.auth.LoginUseCase
import com.orbits.domain.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ─── Login State ───────────────────────────────────────────────

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    // ─── Signup State ─────────────────────────────────────────────

    private val _signupState = MutableStateFlow(SignupUiState())
    val signupState: StateFlow<SignupUiState> = _signupState.asStateFlow()

    // ─── Auth State ───────────────────────────────────────────────

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // ─── Login Actions ─────────────────────────────────────────────

    fun handleLoginEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> updateLoginEmail(event.email)
            is AuthEvent.PasswordChanged -> updateLoginPassword(event.password)
            AuthEvent.TogglePasswordVisibility -> toggleLoginPasswordVisibility()
            AuthEvent.Login -> login()
            AuthEvent.SkipAuth -> skipAuth()
            AuthEvent.NavigateToSignup -> navigateToSignup()
            AuthEvent.NavigateToForgotPassword -> navigateToForgotPassword()
            is AuthEvent.LoginWithGoogle -> loginWithGoogle(event.idToken)
            is AuthEvent.LoginWithMicrosoft -> loginWithMicrosoft(event.idToken)
            AuthEvent.DismissError -> dismissLoginError()
            else -> { /* Not a login event */ }
        }
    }

    private fun updateLoginEmail(email: String) {
        _loginState.update { state ->
            state.copy(
                email = email,
                emailError = if (email.isNotBlank() && !email.isValidEmail()) {
                    "Invalid email format"
                } else null
            )
        }
    }

    private fun updateLoginPassword(password: String) {
        _loginState.update { state ->
            state.copy(
                password = password,
                passwordError = if (password.isNotBlank() && password.length < 8) {
                    "Password must be at least 8 characters"
                } else null
            )
        }
    }

    private fun toggleLoginPasswordVisibility() {
        _loginState.update { state ->
            state.copy(isPasswordVisible = !state.isPasswordVisible)
        }
    }

    private fun login() {
        val state = _loginState.value

        // Validate form
        if (!state.isFormValid) {
            _loginState.update { it.copy(errorMessage = "Please fix the errors above") }
            return
        }

        _loginState.update { it.copy(isLoading = true, errorMessage = null) }
        _authState.update { AuthState.Loading }

        viewModelScope.launch {
            val result = loginUseCase(state.email, state.password)

            _loginState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _loginState.update { it.copy(isLoggedIn = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Login failed"
                    _authState.update { AuthState.Error(message) }
                    _loginState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun loginWithGoogle(idToken: String) {
        _loginState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = loginUseCase.loginWithOAuth("google", idToken)

            _loginState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _loginState.update { it.copy(isLoggedIn = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Google login failed"
                    _authState.update { AuthState.Error(message) }
                    _loginState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun loginWithMicrosoft(idToken: String) {
        _loginState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = loginUseCase.loginWithOAuth("microsoft", idToken)

            _loginState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _loginState.update { it.copy(isLoggedIn = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Microsoft login failed"
                    _authState.update { AuthState.Error(message) }
                    _loginState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun navigateToSignup() {
        // Navigation is handled by the NavGraph
    }

    private fun navigateToForgotPassword() {
        // Navigation is handled by the NavGraph
    }

    private fun dismissLoginError() {
        _loginState.update { it.copy(errorMessage = null) }
        _authState.update { AuthState.Idle }
    }

    private fun skipAuth() {
        _authState.update { AuthState.Loading }
        _loginState.update { it.copy(isLoading = true, errorMessage = null) }
        _signupState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.createLocalUser()
            
            _loginState.update { it.copy(isLoading = false) }
            _signupState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _loginState.update { it.copy(isLoggedIn = true) }
                    _signupState.update { it.copy(isRegistered = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Failed to enter guest mode"
                    _authState.update { AuthState.Error(message) }
                    _loginState.update { it.copy(errorMessage = message) }
                    _signupState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    // ─── Signup Actions ────────────────────────────────────────────

    fun handleSignupEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SignupEmailChanged -> updateSignupEmail(event.email)
            is AuthEvent.SignupPasswordChanged -> updateSignupPassword(event.password)
            is AuthEvent.SignupConfirmPasswordChanged -> updateSignupConfirmPassword(event.password)
            is AuthEvent.SignupDisplayNameChanged -> updateSignupDisplayName(event.displayName)
            is AuthEvent.SignupFirstNameChanged -> updateSignupFirstName(event.firstName)
            is AuthEvent.SignupLastNameChanged -> updateSignupLastName(event.lastName)
            AuthEvent.ToggleSignupPasswordVisibility -> toggleSignupPasswordVisibility()
            AuthEvent.ToggleSignupConfirmPasswordVisibility -> toggleSignupConfirmPasswordVisibility()
            AuthEvent.ToggleTermsAccepted -> toggleTermsAccepted()
            AuthEvent.Register -> register()
            AuthEvent.SkipAuth -> skipAuth()
            AuthEvent.NavigateToLogin -> navigateToLogin()
            AuthEvent.DismissSignupError -> dismissSignupError()
            is AuthEvent.SignupWithGoogle -> signupWithGoogle(event.idToken)
            is AuthEvent.SignupWithMicrosoft -> signupWithMicrosoft(event.idToken)
            else -> { /* Not a signup event */ }
        }
    }

    private fun updateSignupEmail(email: String) {
        _signupState.update { state ->
            state.copy(
                email = email,
                emailError = if (email.isNotBlank() && !email.isValidEmail()) {
                    "Invalid email format"
                } else null
            )
        }
    }

    private fun updateSignupPassword(password: String) {
        val confirmPassword = _signupState.value.confirmPassword
        _signupState.update { state ->
            state.copy(
                password = password,
                passwordError = if (password.isNotBlank() && password.length < 8) {
                    "Password must be at least 8 characters"
                } else null,
                confirmPasswordError = if (confirmPassword.isNotBlank() && password != confirmPassword) {
                    "Passwords do not match"
                } else null
            )
        }
    }

    private fun updateSignupConfirmPassword(password: String) {
        val currentPassword = _signupState.value.password
        _signupState.update { state ->
            state.copy(
                confirmPassword = password,
                confirmPasswordError = if (password.isNotBlank() && currentPassword != password) {
                    "Passwords do not match"
                } else null
            )
        }
    }

    private fun updateSignupDisplayName(displayName: String) {
        _signupState.update { state ->
            state.copy(
                displayName = displayName,
                displayNameError = if (displayName.isNotBlank() && displayName.length > 100) {
                    "Display name must be 100 characters or less"
                } else null
            )
        }
    }

    private fun updateSignupFirstName(firstName: String) {
        _signupState.update { state ->
            state.copy(firstName = firstName)
        }
    }

    private fun updateSignupLastName(lastName: String) {
        _signupState.update { state ->
            state.copy(lastName = lastName)
        }
    }

    private fun toggleSignupPasswordVisibility() {
        _signupState.update { state ->
            state.copy(isPasswordVisible = !state.isPasswordVisible)
        }
    }

    private fun toggleSignupConfirmPasswordVisibility() {
        _signupState.update { state ->
            state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
        }
    }

    private fun toggleTermsAccepted() {
        _signupState.update { state ->
            state.copy(acceptedTerms = !state.acceptedTerms)
        }
    }

    private fun register() {
        val state = _signupState.value

        if (!state.isFormValid) {
            _signupState.update { it.copy(errorMessage = "Please fix the errors above") }
            return
        }

        _signupState.update { it.copy(isLoading = true, errorMessage = null) }
        _authState.update { AuthState.Loading }

        viewModelScope.launch {
            val result = registerUseCase(
                email = state.email,
                password = state.password,
                displayName = state.displayName.takeIf { it.isNotBlank() },
                firstName = state.firstName.takeIf { it.isNotBlank() },
                lastName = state.lastName.takeIf { it.isNotBlank() }
            )

            _signupState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _signupState.update { it.copy(isRegistered = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Registration failed"
                    _authState.update { AuthState.Error(message) }
                    _signupState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun signupWithGoogle(idToken: String) {
        // Similar to login with Google but for signup
        // For simplicity, we use the same flow
        _signupState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // In production, use a separate OAuth signup flow
            val result = registerUseCase(
                email = "google_user@example.com",
                password = "oauth_token"
            )

            _signupState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _signupState.update { it.copy(isRegistered = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Google signup failed"
                    _authState.update { AuthState.Error(message) }
                    _signupState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun signupWithMicrosoft(idToken: String) {
        // Same as above but for Microsoft
        _signupState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = registerUseCase(
                email = "microsoft_user@example.com",
                password = "oauth_token"
            )

            _signupState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _authState.update { AuthState.Success(result.data) }
                    _signupState.update { it.copy(isRegistered = true) }
                }
                is Result.Error -> {
                    val message = result.exception.message ?: "Microsoft signup failed"
                    _authState.update { AuthState.Error(message) }
                    _signupState.update { it.copy(errorMessage = message) }
                }
                Result.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun navigateToLogin() {
        // Navigation is handled by the NavGraph
    }

    private fun dismissSignupError() {
        _signupState.update { it.copy(errorMessage = null) }
        _authState.update { AuthState.Idle }
    }

    // ─── Public Methods ────────────────────────────────────────────

    fun resetState() {
        _loginState.value = LoginUiState()
        _signupState.value = SignupUiState()
        _authState.value = AuthState.Idle
    }

    fun clearLoginState() {
        _loginState.value = LoginUiState()
    }

    fun clearSignupState() {
        _signupState.value = SignupUiState()
    }
}
