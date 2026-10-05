package com.learn2hire.app.ui.tutor

import com.learn2hire.app.data.model.TutorChat
import com.learn2hire.app.data.model.TutorMessage

data class TutorUiState(
    val chatId: String? = null,
    val chatCreatedAt: Long = 0L,
    val topicId: String? = null,
    val subjectId: String? = null,
    val subjectName: String = "",
    val topicName: String = "",
    val messages: List<TutorMessage> = emptyList(),
    val chats: List<TutorChat> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val errorMessage: String? = null,
    val lastFailedMessage: String? = null
)