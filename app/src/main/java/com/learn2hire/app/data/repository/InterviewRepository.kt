package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.InterviewAiFeedback
import com.learn2hire.app.data.model.InterviewQuestion
import com.learn2hire.app.data.model.InterviewSession

interface InterviewRepository {
    fun getQuestions(
        role: String,
        type: String
    ): List<InterviewQuestion>

    suspend fun generateFeedback(
        role: String,
        type: String,
        question: InterviewQuestion,
        answer: String,
        matchedKeyPoints: List<String>,
        missedKeyPoints: List<String>
    ): InterviewAiFeedback

    suspend fun saveSession(session: InterviewSession)
}
