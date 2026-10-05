package com.learn2hire.app.data.model

data class Topic(
    val id: String,
    val subjectId: String,
    val name: String,
    val description: String,
    val estimatedMinutes: Int,
    val content: String,
    val keyConcepts: List<String>,
    val completed: Boolean = false
)