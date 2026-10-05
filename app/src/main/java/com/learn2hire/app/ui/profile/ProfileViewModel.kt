package com.learn2hire.app.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.learn2hire.app.data.repository.UserRepository
import com.learn2hire.app.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val repository: UserRepository = UserRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {

        val user =
            FirebaseAuth.getInstance().currentUser

        if (user == null) {
            _uiState.value = ProfileUiState(
                isLoading = false,
                errorMessage = "Not signed in."
            )
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        repository.getUserProfile(user.uid) { result ->

            result.onSuccess { profile ->

                _uiState.value = ProfileUiState(
                    profile = profile,
                    editedName = profile.name,
                    editedCourse = profile.course,
                    editedSemester = profile.semester,
                    isLoading = false
                )
            }

            result.onFailure { error ->

                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage =
                        error.message
                            ?: "Unable to load profile."
                )
            }
        }
    }

    fun startEditing() {

        val profile =
            _uiState.value.profile ?: return

        _uiState.update {
            it.copy(
                editedName = profile.name,
                editedCourse = profile.course,
                editedSemester = profile.semester,
                isEditing = true,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun cancelEditing() {

        val profile =
            _uiState.value.profile ?: return

        _uiState.update {
            it.copy(
                editedName = profile.name,
                editedCourse = profile.course,
                editedSemester = profile.semester,
                isEditing = false,
                errorMessage = null
            )
        }
    }

    fun updateName(value: String) {
        _uiState.update {
            it.copy(editedName = value)
        }
    }

    fun updateCourse(value: String) {
        _uiState.update {
            it.copy(editedCourse = value)
        }
    }

    fun updateSemester(value: String) {
        _uiState.update {
            it.copy(editedSemester = value)
        }
    }

    fun saveProfile() {

        val state = _uiState.value
        val currentProfile =
            state.profile ?: return

        if (state.editedName.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage =
                        "Enter your name."
                )
            }
            return
        }

        val updatedProfile =
            currentProfile.copy(
                name = state.editedName.trim(),
                course = state.editedCourse.trim(),
                semester = state.editedSemester.trim()
            )

        _uiState.update {
            it.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )
        }

        repository.updateUserProfile(
            updatedProfile
        ) { result ->

            result.onSuccess {

                _uiState.update {
                    it.copy(
                        profile = updatedProfile,
                        isSaving = false,
                        isEditing = false,
                        successMessage =
                            "Profile updated."
                    )
                }
            }

            result.onFailure { error ->

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage =
                            error.message
                                ?: "Unable to update profile."
                    )
                }
            }
        }
    }
}