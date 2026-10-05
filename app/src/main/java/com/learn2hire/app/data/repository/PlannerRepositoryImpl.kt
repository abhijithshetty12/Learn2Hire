package com.learn2hire.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.learn2hire.app.data.model.PlannerTask
import com.learn2hire.app.data.model.StudySession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlannerRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlannerRepository {

    private fun userDocument() =
        firestore.collection("users")
            .document(requireNotNull(auth.currentUser?.uid) { "User is not signed in" })

    override fun observeTasks(): Flow<List<PlannerTask>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = firestore.collection("users")
            .document(uid)
            .collection("plannerTasks")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents
                    ?.mapNotNull { it.toObject(PlannerTask::class.java) }
                    .orEmpty()

                trySend(tasks)
            }

        awaitClose { registration.remove() }
    }

    override fun observeSessions(): Flow<List<StudySession>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = firestore.collection("users")
            .document(uid)
            .collection("plannerSessions")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val sessions = snapshot?.documents
                    ?.mapNotNull { it.toObject(StudySession::class.java) }
                    .orEmpty()

                trySend(sessions)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun addTask(task: PlannerTask) {
        val document = userDocument()
            .collection("plannerTasks")
            .document()

        document.set(task.copy(id = document.id)).await()
    }

    override suspend fun deleteTask(taskId: String) {
        userDocument()
            .collection("plannerTasks")
            .document(taskId)
            .delete()
            .await()
    }

    override suspend fun setTaskCompleted(taskId: String, completed: Boolean) {
        userDocument()
            .collection("plannerTasks")
            .document(taskId)
            .update(
                mapOf(
                    "completed" to completed,
                    "completedAt" to if (completed) System.currentTimeMillis() else null
                )
            )
            .await()
    }

    override suspend fun savePlan(sessions: List<StudySession>) {
        if (sessions.isEmpty()) return

        val collection = userDocument().collection("plannerSessions")
        val batch = firestore.batch()
        val now = System.currentTimeMillis()

        sessions.forEachIndexed { index, session ->
            val document = collection.document()
            batch.set(
                document,
                session.copy(
                    id = document.id,
                    createdAt = now + index
                )
            )
        }

        batch.commit().await()
    }
}
