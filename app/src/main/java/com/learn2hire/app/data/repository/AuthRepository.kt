package com.learn2hire.app.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    val currentUser: FirebaseUser?

    fun isUserLoggedIn(): Boolean

    fun login(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    )

    fun register(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    )

    fun signInWithGoogle(
        idToken: String,
        onResult: (Result<FirebaseUser>) -> Unit
    )

    fun logout()
}