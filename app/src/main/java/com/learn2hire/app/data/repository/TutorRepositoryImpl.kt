package com.learn2hire.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.learn2hire.app.data.model.TutorChat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TutorRepositoryImpl : TutorRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val model = Firebase
        .ai(
            backend = GenerativeBackend.googleAI()
        )
        .generativeModel(
            modelName = "gemini-3.8-flash"
        )

    override suspend fun generateResponse(
        systemContext: String,
        conversation: List<Pair<String, String>>,
        userMessage: String
    ): Result<TutorGenerationResult> {

        return try {

            val conversationText = conversation
                .takeLast(10)
                .joinToString("\n\n") { (role, message) ->
                    "${role.uppercase()}: $message"
                }

            val prompt = buildString {

                appendLine(systemContext)
                appendLine()

                if (conversationText.isNotBlank()) {
                    appendLine("Previous conversation:")
                    appendLine(conversationText)
                    appendLine()
                }

                appendLine("Student:")
                appendLine(userMessage)
                appendLine()

                appendLine("Tutor:")
            }

            val response = model.generateContent(prompt)

            val text = response.text?.trim()

            if (text.isNullOrBlank()) {
                Result.failure(
                    Exception("Gemini returned an empty response.")
                )
            } else {
                Result.success(TutorGenerationResult(text = text, usedFallback = false))
            }

        } catch (e: Exception) {

            android.util.Log.e(
                "Learn2HireAI",
                "Gemini request failed",
                e
            )

            val fallback = buildLocalFallback(
                systemContext = systemContext,
                conversation = conversation,
                userMessage = userMessage
            )

            if (fallback.isNotBlank()) {
                Result.success(
                    TutorGenerationResult(
                        text = fallback,
                        usedFallback = true
                    )
                )
            } else {
                Result.failure(
                    Exception(
                        e.message
                            ?: "${e::class.java.simpleName}: Unknown AI error",
                        e
                    )
                )
            }
        }
    }


    private fun buildLocalFallback(
        systemContext: String,
        conversation: List<Pair<String, String>>,
        userMessage: String
    ): String {
        val topic = extractSection(systemContext, "Topic:", "Topic description:")
        val description = extractSection(systemContext, "Topic description:", "Lesson material:")
        val lesson = extractSection(systemContext, "Lesson material:", "Key concepts:")
        val conceptsText = extractSection(systemContext, "Key concepts:", "Student learning context")
        val concepts = conceptsText
            .lineSequence()
            .firstOrNull()
            .orEmpty()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val question = userMessage.lowercase()

        if (topic.isBlank() && lesson.isBlank()) {
            return "AI Tutor is temporarily unavailable. Open a study topic and I can still help using its saved lesson material."
        }

        return when {
            question.contains("quiz") -> {
                val concept = concepts.firstOrNull() ?: topic
                "Here's a study-material question:\n\n**Question:** Explain **$concept** in your own words and give one example.\n\nSend your answer and I'll help you review it."
            }

            question.contains("exam") -> {
                buildString {
                    append("## $topic\n\n")
                    if (description.isNotBlank()) append("$description\n\n")
                    append("**Key points:**\n")
                    concepts.take(5).forEach { append("- $it\n") }
                    if (lesson.isNotBlank()) {
                        append("\n**Exam-ready explanation:**\n")
                        append(compactLesson(lesson))
                    }
                }.trim()
            }

            question.contains("example") -> {
                buildString {
                    append("For **$topic**, use the lesson's main ideas as the basis:\n\n")
                    concepts.take(4).forEach { append("- **$it**\n") }
                    if (lesson.isNotBlank()) {
                        append("\n")
                        append(compactLesson(lesson))
                    }
                    append("\n\nGemini is temporarily unavailable, so this response is grounded in your saved Learn2Hire study material.")
                }.trim()
            }

            question.contains("simple") || question.contains("explain") || question.contains("what is") -> {
                buildString {
                    append("## $topic\n\n")
                    if (description.isNotBlank()) append("$description\n\n")
                    if (concepts.isNotEmpty()) {
                        append("**Key concepts:** ${concepts.take(5).joinToString(", ")}.\n\n")
                    }
                    if (lesson.isNotBlank()) append(compactLesson(lesson))
                    append("\n\n*Study-material fallback: Gemini is temporarily unavailable.*")
                }.trim()
            }

            else -> {
                buildString {
                    append("I can still help with **$topic** using the saved lesson material.\n\n")
                    if (lesson.isNotBlank()) append(compactLesson(lesson))
                    if (concepts.isNotEmpty()) {
                        append("\n\n**Key concepts:** ${concepts.take(5).joinToString(", ")}.")
                    }
                    append("\n\nGemini is temporarily unavailable, so I won't invent information beyond the saved lesson.")
                }.trim()
            }
        }
    }

    private fun compactLesson(text: String): String =
        text.trim()
            .replace(Regex("\\n{3,}"), "\n\n")
            .take(1800)

    private fun extractSection(
        text: String,
        startMarker: String,
        endMarker: String
    ): String {
        val start = text.indexOf(startMarker)
        if (start < 0) return ""
        val contentStart = start + startMarker.length
        val end = text.indexOf(endMarker, contentStart)
        return if (end >= 0) {
            text.substring(contentStart, end).trim()
        } else {
            text.substring(contentStart).trim()
        }
    }

    override suspend fun createChat(
        chat: TutorChat
    ): Result<String> {

        return try {

            val uid = requireUserId()

            val document = firestore
                .collection("users")
                .document(uid)
                .collection("tutorChats")
                .document()

            val chatWithId = chat.copy(
                id = document.id
            )

            document
                .set(chatWithId)
                .await()

            Result.success(document.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveChat(
        chat: TutorChat
    ): Result<Unit> {

        return try {

            val uid = requireUserId()

            require(chat.id.isNotBlank()) {
                "Chat ID cannot be empty."
            }

            firestore
                .collection("users")
                .document(uid)
                .collection("tutorChats")
                .document(chat.id)
                .set(chat)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChat(
        chatId: String
    ): Result<Unit> {

        return try {

            val uid = requireUserId()

            firestore
                .collection("users")
                .document(uid)
                .collection("tutorChats")
                .document(chatId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeChats(): Flow<List<TutorChat>> =
        callbackFlow {

            val uid = auth.currentUser?.uid

            if (uid == null) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val listener = firestore
                .collection("users")
                .document(uid)
                .collection("tutorChats")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val chats = snapshot
                        ?.documents
                        ?.mapNotNull { document ->
                            document.toObject(TutorChat::class.java)
                        }
                        ?.sortedByDescending {
                            it.updatedAt
                        }
                        ?: emptyList()

                    trySend(chats)
                }

            awaitClose {
                listener.remove()
            }
        }

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException(
                "User must be signed in."
            )
    }
}