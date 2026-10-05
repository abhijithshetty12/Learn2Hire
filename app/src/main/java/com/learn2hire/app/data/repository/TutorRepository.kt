package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.TutorChat
import kotlinx.coroutines.flow.Flow

data class TutorGenerationResult(
    val text: String,
    val usedFallback: Boolean = false
)

interface TutorRepository {

    suspend fun generateResponse(
        systemContext: String,
        conversation: List<Pair<String, String>>,
        userMessage: String
    ): Result<TutorGenerationResult>

    suspend fun createChat(
        chat: TutorChat
    ): Result<String>

    suspend fun saveChat(
        chat: TutorChat
    ): Result<Unit>

    suspend fun deleteChat(
        chatId: String
    ): Result<Unit>

    fun observeChats(): Flow<List<TutorChat>>
}