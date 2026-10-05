package com.learn2hire.app.data.model

data class StudyProgress(
    val topicId: String = "",
    val subjectId: String = "",
    val completed: Boolean = false,
    val progress: Float = 0f,
    val lastOpenedAt: Long = 0L,
    val completedAt: Long? = null
)