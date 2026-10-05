package com.learn2hire.app.data.model

data class InterviewSession(
    val id: String = "",
    val role: String = "",
    val type: String = "",
    val questionsCompleted: Int = 0,
    val matchedKeyPoints: Int = 0,
    val totalKeyPoints: Int = 0,
    val coveragePercentage: Int = 0,
    val results: List<InterviewResult> = emptyList(),
    val completedAt: Long = 0L
)
