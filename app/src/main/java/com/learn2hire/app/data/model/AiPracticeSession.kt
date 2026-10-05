package com.learn2hire.app.data.model

data class AiPracticeSession(
    val id: String = "",
    val topicId: String = "",
    val subjectId: String = "",
    val difficulty: String = "Standard",
    val score: Int = 0,
    val total: Int = 0,
    val percentage: Int = 0,
    val generatedAt: Long = 0L,
    val completedAt: Long = 0L
)
