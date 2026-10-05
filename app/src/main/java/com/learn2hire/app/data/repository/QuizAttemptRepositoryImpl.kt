package com.learn2hire.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.learn2hire.app.data.model.QuizAttempt
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class QuizAttemptRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : QuizAttemptRepository {

    private fun userId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User is not authenticated")

    private fun attemptsCollection() =
        firestore.collection("users")
            .document(userId())
            .collection("quizAttempts")

    override suspend fun saveAttempt(attempt: QuizAttempt) {
        val document = attemptsCollection().document()
        document.set(attempt.copy(id = document.id)).await()
    }

    override fun observeAttempts(): Flow<List<QuizAttempt>> = callbackFlow {
        val user = auth.currentUser

        if (user == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(user.uid)
            .collection("quizAttempts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val attempts = snapshot?.documents
                    ?.mapNotNull { it.toObject(QuizAttempt::class.java) }
                    ?.sortedByDescending { it.completedAt }
                    ?: emptyList()

                trySend(attempts)
            }

        awaitClose { listener.remove() }
    }
}
