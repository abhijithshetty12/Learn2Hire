package com.learn2hire.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun login(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = auth.currentUser

                if (user != null) {
                    onResult(Result.success(user))
                } else {
                    onResult(
                        Result.failure(
                            Exception("Unable to retrieve user.")
                        )
                    )
                }
            } else {
                onResult(
                    Result.failure(
                        task.exception ?: Exception("Login failed.")
                    )
                )
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = auth.currentUser

                if (user != null) {
                    onResult(Result.success(user))
                } else {
                    onResult(
                        Result.failure(
                            Exception("Unable to retrieve user.")
                        )
                    )
                }
            } else {
                onResult(
                    Result.failure(
                        task.exception
                            ?: Exception("Registration failed.")
                    )
                )
            }
        }
    }

    override fun signInWithGoogle(
        idToken: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        val credential =
            GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        onResult(Result.success(user))
                    } else {
                        onResult(
                            Result.failure(
                                Exception("Unable to retrieve Google user.")
                            )
                        )
                    }
                } else {
                    onResult(
                        Result.failure(
                            task.exception
                                ?: Exception("Google sign-in failed.")
                        )
                    )
                }
            }
    }

    override fun logout() {
        auth.signOut()
    }
}