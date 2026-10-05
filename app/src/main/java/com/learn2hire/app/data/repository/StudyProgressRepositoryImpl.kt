package com.learn2hire.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.learn2hire.app.data.model.StudyProgress
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class StudyProgressRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : StudyProgressRepository {

    private fun getUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User is not authenticated")
    }

    private fun progressCollection() =
        firestore
            .collection("users")
            .document(getUserId())
            .collection("studyProgress")

    override fun observeStudyProgress(): Flow<List<StudyProgress>> = callbackFlow {

        val user = auth.currentUser

        if (user == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore
            .collection("users")
            .document(user.uid)
            .collection("studyProgress")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val progress = snapshot
                    ?.documents
                    ?.mapNotNull { document ->
                        document.toObject(StudyProgress::class.java)
                    }
                    ?: emptyList()

                trySend(progress)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun markTopicOpened(
        topicId: String,
        subjectId: String
    ) {
        val document = progressCollection().document(topicId)
        val snapshot = document.get().await()

        if (snapshot.exists()) {
            document.update(
                mapOf(
                    "lastOpenedAt" to System.currentTimeMillis()
                )
            ).await()
        } else {
            val progress = StudyProgress(
                topicId = topicId,
                subjectId = subjectId,
                completed = false,
                progress = 0f,
                lastOpenedAt = System.currentTimeMillis(),
                completedAt = null
            )

            document.set(progress).await()
        }
    }

    override suspend fun markTopicCompleted(
        topicId: String,
        subjectId: String
    ) {
        val now = System.currentTimeMillis()

        val progress = StudyProgress(
            topicId = topicId,
            subjectId = subjectId,
            completed = true,
            progress = 1f,
            lastOpenedAt = now,
            completedAt = now
        )

        progressCollection()
            .document(topicId)
            .set(progress)
            .await()
    }

    override suspend fun markTopicIncomplete(
        topicId: String,
        subjectId: String
    ) {
        val now = System.currentTimeMillis()

        val progress = StudyProgress(
            topicId = topicId,
            subjectId = subjectId,
            completed = false,
            progress = 0f,
            lastOpenedAt = now,
            completedAt = null
        )

        progressCollection()
            .document(topicId)
            .set(progress)
            .await()
    }
}