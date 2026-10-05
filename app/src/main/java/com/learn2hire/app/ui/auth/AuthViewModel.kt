package com.learn2hire.app.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.learn2hire.app.data.model.UserProfile
import com.learn2hire.app.data.repository.AuthRepository
import com.learn2hire.app.data.repository.AuthRepositoryImpl
import com.learn2hire.app.data.repository.UserRepository
import com.learn2hire.app.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val authRepository: AuthRepository =
        AuthRepositoryImpl(),

    private val userRepository: UserRepository =
        UserRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isAuthenticated =
                authRepository.isUserLoggedIn()
        )
    )

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password,
                errorMessage = null
            )
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                email = "",
                password = "",
                confirmPassword = "",
                errorMessage = null,
                isLoading = false
            )
        }
    }

    fun login() {
        val state = _uiState.value

        if (
            state.email.isBlank() ||
            state.password.isBlank()
        ) {
            showError("Enter your email and password.")
            return
        }

        setLoading()

        authRepository.login(
            state.email,
            state.password
        ) { result ->

            result.onSuccess { user ->
                ensureProfile(
                    user = user,
                    provider = "password"
                )
            }.onFailure { error ->
                showError(
                    error.message ?: "Unable to sign in."
                )
            }
        }
    }

    fun register() {
        val state = _uiState.value

        if (
            state.email.isBlank() ||
            state.password.isBlank() ||
            state.confirmPassword.isBlank()
        ) {
            showError("Complete all fields.")
            return
        }

        if (state.password.length < 6) {
            showError(
                "Password must contain at least 6 characters."
            )
            return
        }

        if (state.password != state.confirmPassword) {
            showError("Passwords do not match.")
            return
        }

        setLoading()

        authRepository.register(
            state.email,
            state.password
        ) { result ->

            result.onSuccess { user ->
                ensureProfile(
                    user = user,
                    provider = "password"
                )
            }.onFailure { error ->
                showError(
                    error.message
                        ?: "Unable to create account."
                )
            }
        }
    }

    fun signInWithGoogleToken(idToken: String) {

        setLoading()

        authRepository.signInWithGoogle(idToken) { result ->

            result.onSuccess { user ->
                ensureProfile(
                    user = user,
                    provider = "google"
                )
            }.onFailure { error ->
                showError(
                    error.message
                        ?: "Google sign-in failed."
                )
            }
        }
    }

    fun setGoogleError(message: String) {
        showError(message)
    }

    fun logout() {
        authRepository.logout()

        _uiState.value =
            AuthUiState(isAuthenticated = false)
    }

    private fun ensureProfile(
        user: FirebaseUser,
        provider: String
    ) {
        val profile = UserProfile(
            uid = user.uid,
            name = user.displayName.orEmpty(),
            email = user.email.orEmpty(),
            photoUrl =
                user.photoUrl?.toString().orEmpty(),
            course = "",
            semester = "",
            authProvider = provider,
            createdAt = System.currentTimeMillis()
        )

        userRepository.createUserIfNeeded(profile) { result ->

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->

                authRepository.logout()

                showError(
                    error.message
                        ?: "Unable to create your profile."
                )
            }
        }
    }

    private fun setLoading() {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }
}