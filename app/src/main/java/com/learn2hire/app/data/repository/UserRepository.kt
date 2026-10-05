package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.UserProfile

interface UserRepository {

    fun createUserIfNeeded(
        profile: UserProfile,
        onResult: (Result<Unit>) -> Unit
    )

    fun getUserProfile(
        uid: String,
        onResult: (Result<UserProfile>) -> Unit
    )

    fun updateUserProfile(
        profile: UserProfile,
        onResult: (Result<Unit>) -> Unit
    )
}