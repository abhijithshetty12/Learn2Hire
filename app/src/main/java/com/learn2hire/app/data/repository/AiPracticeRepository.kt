package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.AiPracticeQuestion
import com.learn2hire.app.data.model.AiPracticeSession

data class AiPracticeGenerationResult(
    val questions: List<AiPracticeQuestion>,
    val usedFallback: Boolean = false
)

interface AiPracticeRepository {
    suspend fun generateQuestions(
        topicName: String,
        subjectName: String,
        lessonContent: String,
        keyConcepts: List<String>,
        difficulty: String,
        count: Int = 5
    ): Result<AiPracticeGenerationResult>

    suspend fun saveSession(
        session: AiPracticeSession
    ): Result<String>
}
