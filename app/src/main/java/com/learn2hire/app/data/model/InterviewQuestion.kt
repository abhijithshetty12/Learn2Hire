package com.learn2hire.app.data.model

data class InterviewQuestion(
    val id: String = "",
    val role: String = "",
    val type: String = "",
    val question: String = "",
    val keyPoints: List<String> = emptyList()
)
