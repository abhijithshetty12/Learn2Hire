package com.learn2hire.app.data.model

data class Subject(
    val id: String,
    val name: String,
    val description: String,
    val progress: Float = 0f,
    val completedTopics: Int = 0,
    val totalTopics: Int = 0
)