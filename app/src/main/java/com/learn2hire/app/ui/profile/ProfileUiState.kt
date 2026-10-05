package com.learn2hire.app.ui.profile

import com.learn2hire.app.data.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,

    val editedName: String = "",
    val editedCourse: String = "",
    val editedSemester: String = "",

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,

    val errorMessage: String? = null,
    val successMessage: String? = null
)