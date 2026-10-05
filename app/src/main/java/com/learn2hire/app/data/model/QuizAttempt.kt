package com.learn2hire.app.data.model

data class QuizAttempt(
    val id: String = "",
    val topicId: String = "",
    val subjectId: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val percentage: Int = 0,
    val answers: Map<String, Int> = emptyMap(),
    val completedAt: Long = 0L
)
