package com.learn2hire.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.learn2hire.app.data.model.UserProfile

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) : UserRepository {

    override fun createUserIfNeeded(
        profile: UserProfile,
        onResult: (Result<Unit>) -> Unit
    ) {
        val document = firestore
            .collection("users")
            .document(profile.uid)

        document.get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {
                    onResult(Result.success(Unit))
                } else {
                    document.set(profile)
                        .addOnSuccessListener {
                            onResult(Result.success(Unit))
                        }
                        .addOnFailureListener { error ->
                            onResult(Result.failure(error))
                        }
                }
            }
            .addOnFailureListener { error ->
                onResult(Result.failure(error))
            }
    }

    override fun getUserProfile(
        uid: String,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val profile =
                    snapshot.toObject(UserProfile::class.java)

                if (profile != null) {
                    onResult(Result.success(profile))
                } else {
                    onResult(
                        Result.failure(
                            Exception("User profile not found.")
                        )
                    )
                }
            }
            .addOnFailureListener { error ->
                onResult(Result.failure(error))
            }
    }

    override fun updateUserProfile(
        profile: UserProfile,
        onResult: (Result<Unit>) -> Unit
    ) {
        firestore
            .collection("users")
            .document(profile.uid)
            .set(profile)
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { error ->
                onResult(Result.failure(error))
            }
    }
}