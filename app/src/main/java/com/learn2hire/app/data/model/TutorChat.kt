package com.learn2hire.app.data.model

data class TutorChat(
    val id: String = "",
    val title: String = "New Chat",
    val topicId: String? = null,
    val subjectId: String? = null,
    val subjectName: String = "",
    val topicName: String = "",
    val messages: List<TutorMessage> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)