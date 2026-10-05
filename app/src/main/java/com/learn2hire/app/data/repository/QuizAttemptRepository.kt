package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.QuizAttempt
import kotlinx.coroutines.flow.Flow

interface QuizAttemptRepository {
    suspend fun saveAttempt(attempt: QuizAttempt)
    fun observeAttempts(): Flow<List<QuizAttempt>>
}
