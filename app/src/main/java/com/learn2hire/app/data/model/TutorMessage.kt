package com.learn2hire.app.data.model

data class TutorMessage(
    val id: String = "",
    val role: String = "user",
    val text: String = "",
    val timestamp: Long = 0L
)