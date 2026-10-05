package com.learn2hire.app.ui.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn2hire.app.data.local.StudyContent
import com.learn2hire.app.data.model.TutorMessage
import com.learn2hire.app.data.model.StudentLearningProfile
import com.learn2hire.app.data.repository.LearningProfileRepository
import com.learn2hire.app.data.repository.LearningProfileRepositoryImpl
import com.learn2hire.app.data.repository.TutorRepository
import com.learn2hire.app.data.repository.TutorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.learn2hire.app.data.model.TutorChat
import kotlinx.coroutines.flow.catch

class TutorViewModel(
    private val repository: TutorRepository = TutorRepositoryImpl(),
    private val learningProfileRepository: LearningProfileRepository =
        LearningProfileRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorUiState())
    private val _learningProfile = MutableStateFlow(StudentLearningProfile())
    val uiState: StateFlow<TutorUiState> = _uiState.asStateFlow()
    init {
        observeChatHistory()
        observeLearningProfile()
    }
    private fun observeChatHistory() {
        viewModelScope.launch {
            repository
                .observeChats()
                .catch { error ->
                    android.util.Log.e(
                        "Learn2HireTutor",
                        "Failed to observe tutor chats",
                        error
                    )
                }
                .collect { chats ->
                    _uiState.update {
                        it.copy(chats = chats)
                    }
                }
        }
    }
    private fun observeLearningProfile() {
        viewModelScope.launch {
            learningProfileRepository
                .observeLearningProfile()
                .catch { error ->
                    android.util.Log.e(
                        "Learn2HireTutor",
                        "Failed to observe learning profile",
                        error
                    )
                }
                .collect { profile ->
                    _learningProfile.value = profile
                }
        }
    }

    fun openHistory() {
        _uiState.update {
            it.copy(isHistoryOpen = true)
        }
    }

    fun closeHistory() {
        _uiState.update {
            it.copy(isHistoryOpen = false)
        }
    }

    fun startNewChat() {
        val state = _uiState.value

        val welcomeMessage = if (state.topicName.isNotBlank()) {
            "I'm ready to help you with ${state.topicName}. " +
                    "Ask a question or choose one of the quick prompts."
        } else {
            "Hi! I'm your Learn2Hire AI Tutor. " +
                    "Ask me anything about your studies, concepts, exams, " +
                    "or interview preparation."
        }

        _uiState.update {
            it.copy(
                chatId = null,
                chatCreatedAt = 0L,
                messages = listOf(
                    assistantMessage(welcomeMessage)
                ),
                input = "",
                isLoading = false,
                isSaving = false,
                isHistoryOpen = false,
                errorMessage = null,
                lastFailedMessage = null
            )
        }
    }
    fun openChat(chat: TutorChat) {

        if (_uiState.value.isLoading) {
            return
        }

        _uiState.update {
            it.copy(
                chatId = chat.id,
                chatCreatedAt = chat.createdAt,
                topicId = chat.topicId,
                subjectId = chat.subjectId,
                subjectName = chat.subjectName,
                topicName = chat.topicName,
                messages = chat.messages,
                input = "",
                isLoading = false,
                isHistoryOpen = false,
                errorMessage = null,
                lastFailedMessage = null
            )
        }
    }
    fun deleteChat(chatId: String) {

        viewModelScope.launch {

            repository
                .deleteChat(chatId)
                .onSuccess {

                    if (_uiState.value.chatId == chatId) {
                        startNewChat()
                    }
                }
                .onFailure { error ->

                    android.util.Log.e(
                        "Learn2HireTutor",
                        "Failed to delete tutor chat",
                        error
                    )

                    _uiState.update {
                        it.copy(
                            errorMessage =
                                "Couldn't delete this conversation."
                        )
                    }
                }
        }
    }
    fun retryLastMessage() {
        val message =
            _uiState.value.lastFailedMessage ?: return

        sendMessage(
            message = message,
            addUserMessage = false
        )
    }

    fun dismissError() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                lastFailedMessage = null
            )
        }
    }

    private fun friendlyErrorMessage(
        error: Throwable
    ): String {

        val message = error.message.orEmpty()

        return when {
            message.contains(
                "high demand",
                ignoreCase = true
            ) -> {
                "AI Tutor is busy right now. Please try again."
            }

            message.contains(
                "unavailable",
                ignoreCase = true
            ) -> {
                "AI Tutor is temporarily unavailable. Please try again."
            }

            message.contains(
                "quota",
                ignoreCase = true
            ) -> {
                "AI Tutor usage limit has been reached. Please try again later."
            }

            message.contains(
                "network",
                ignoreCase = true
            ) -> {
                "Check your internet connection and try again."
            }

            else -> {
                message.ifBlank {
                    "AI Tutor couldn't generate a response. Please try again."
                }
            }
        }
    }

    fun initialize(topicId: String?) {

        if (
            _uiState.value.topicId == topicId &&
            _uiState.value.messages.isNotEmpty()
        ) {
            return
        }

        if (topicId == null) {
            _uiState.update { current ->
                TutorUiState(
                    chats = current.chats,
                    messages = listOf(
                        assistantMessage(
                            "Hi! I'm your Learn2Hire AI Tutor. Ask me anything about your studies, concepts, exams, or interview preparation."
                        )
                    )
                )
            }
            return
        }

        val topic = StudyContent.getTopic(topicId)

        if (topic == null) {
            _uiState.value = TutorUiState(
                topicId = topicId,
                errorMessage = "Topic not found."
            )
            return
        }

        val subject = StudyContent.subjects
            .firstOrNull { it.id == topic.subjectId }

        _uiState.value = TutorUiState(
            topicId = topicId,
            subjectId = topic.subjectId,
            subjectName = subject?.name.orEmpty(),
            topicName = topic.name,
            messages = listOf(
                assistantMessage(
                    "I'm ready to help you with ${topic.name}. Ask a question or choose one of the quick prompts."
                )
            )
        )
    }

    fun updateInput(value: String) {
        _uiState.update {
            it.copy(
                input = value,
                errorMessage = null
            )
        }
    }

    fun sendMessage(
        message: String? = null,
        addUserMessage: Boolean = true
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        val text = message
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: _uiState.value.input.trim()

        if (text.isBlank()) {
            return
        }

        val conversationBeforeRequest =
            _uiState.value.messages

        val userMessage = TutorMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            text = text,
            timestamp = System.currentTimeMillis()
        )

        _uiState.update { state ->
            state.copy(
                messages =
                    if (addUserMessage) {
                        state.messages + userMessage
                    } else {
                        state.messages
                    },
                input = "",
                isLoading = true,
                errorMessage = null,
                lastFailedMessage = null
            )
        }

        viewModelScope.launch {

            val result = repository.generateResponse(
                systemContext = buildSystemContext(),
                conversation =
                    conversationBeforeRequest.map {
                        it.role to it.text
                    },
                userMessage = text
            )

            result
                .onSuccess { generation ->

                    _uiState.update {
                        it.copy(
                            messages = it.messages +
                                    assistantMessage(generation.text),
                            isLoading = false,
                            errorMessage = null,
                            lastFailedMessage = null
                        )
                    }

                    persistConversation()
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                friendlyErrorMessage(error),
                            lastFailedMessage = text
                        )
                    }
                }
        }
    }



    fun explainSimply() {
        sendMessage(
            "Explain this topic in simple language " +
                    "as if I am learning it for the first time."
        )
    }

    fun giveExample() {
        sendMessage(
            "Give me a clear practical example of this topic " +
                    "and explain it step by step."
        )
    }

    fun examAnswer() {
        sendMessage(
            "Give me an exam-ready answer for this topic. " +
                    "Keep it structured, concise, and easy to write in an exam."
        )
    }

    fun quizMe() {
        sendMessage(
            "Quiz me on this topic. Ask one question at a time " +
                    "and wait for my answer before explaining it."
        )
    }

    private fun buildSystemContext(): String {

        val topicId = _uiState.value.topicId

        if (topicId == null) {
            return """
            You are Learn2Hire AI Tutor.

            You help an engineering student understand academic
            concepts, prepare for examinations, practice problems,
            and prepare for technical interviews.

            Be accurate, educational, clear, and concise.

            When explaining difficult concepts:
            - begin with a simple explanation
            - give an example when useful
            - use steps when appropriate
            - use formulas only when necessary
            - distinguish assumptions from facts
            - do not invent course material

            If the student requests an exam answer, provide a
            structured answer suitable for writing in an
            engineering examination.

            Format responses for a mobile learning application.

            Use:
            - short paragraphs
            - concise numbered steps when appropriate
            - bullet points when useful
            - bold text only for important terms
            - short section headings for longer answers

            Avoid excessively long responses unless the student
            explicitly asks for detail.
        """.trimIndent()
        }

        val topic = StudyContent.getTopic(topicId)

        if (topic == null) {
            return """
            You are Learn2Hire AI Tutor.

            Help the student understand academic concepts clearly.

            Keep responses concise and suitable for a mobile
            learning application.
        """.trimIndent()
        }

        val learningContext = buildLearningContext(topicId)

        return """
        You are Learn2Hire AI Tutor.

        The student is currently studying:

        Subject:
        ${_uiState.value.subjectName}

        Topic:
        ${topic.name}

        Topic description:
        ${topic.description}

        Lesson material:
        ${topic.content}

        Key concepts:
        ${topic.keyConcepts.joinToString(", ")}

        $learningContext

        Use the provided lesson as the primary context when the
        student's question is about this topic.

        Explain concepts clearly and accurately.

        If the student asks for an exam-ready answer, provide a
        structured and concise engineering-exam style answer.

        If the student asks to be quizzed, ask one question at a
        time and wait for their response.

        If the provided lesson does not contain enough information
        to answer something, clearly indicate when you are adding
        general knowledge beyond the lesson.

        Format responses for a mobile learning application.

        Use:
        - short paragraphs
        - concise numbered steps when appropriate
        - bullet points when useful
        - bold text only for important terms
        - short section headings for longer answers

        Avoid excessively long responses unless the student
        explicitly asks for detail.
    """.trimIndent()
    }
    private fun buildLearningContext(topicId: String): String {
        val profile = _learningProfile.value
        val performance = profile.topicPerformance
            .firstOrNull { it.topicId == topicId }

        val practice = profile.topicPracticePerformance
            .firstOrNull { it.topicId == topicId }

        if (performance == null || performance.attempts == 0) {
            val practiceContext = if (practice != null) {
                """
                - AI practice sessions: ${practice.sessions}
                - Latest AI practice score: ${practice.latestScore}%
                - Average AI practice score: ${practice.averageScore}%
                """.trimIndent()
            } else {
                "- No AI practice sessions recorded yet."
            }

            return """
            Student learning context:
            - This topic has not been assessed through a fixed quiz yet.
            - Official mastery level is therefore not assessed.
            $practiceContext
            - Treat AI practice only as a training signal, not as the official mastery score.
            - Do not assume the student's mastery level.
            - Explain clearly and check understanding when useful.
            """.trimIndent()
        }

        val strategy = when (performance.level.name) {
            "WEAK" -> """
                - Reinforce foundational ideas before advanced details.
                - Prefer simple examples and step-by-step explanations.
                - Ask short understanding checks when useful.
                - Never label or describe the student as weak.
            """.trimIndent()

            "DEVELOPING" -> """
                - Build on what the student already understands.
                - Clarify likely gaps with examples.
                - Gradually increase difficulty.
                - Use short understanding checks when useful.
            """.trimIndent()

            "STRONG" -> """
                - Avoid over-explaining basic ideas unless requested.
                - Use deeper applications and challenging examples when useful.
                - Encourage connections to related concepts and interviews.
            """.trimIndent()

            else -> """
                - Explain clearly and check understanding when useful.
            """.trimIndent()
        }

        val practiceContext = if (practice != null) {
            """
            - AI practice sessions: ${practice.sessions}
            - Latest AI practice score: ${practice.latestScore}%
            - Average AI practice score: ${practice.averageScore}%
            """.trimIndent()
        } else {
            "- AI practice: no sessions yet"
        }

        return """
        Student learning context for this topic:
        - Quiz attempts: ${performance.attempts}
        - Average quiz score: ${performance.averageScore}%
        - Latest quiz score: ${performance.latestScore}%
        - Learning level: ${performance.level.name}
        $practiceContext

        Personalization instructions:
        $strategy

        Use quiz performance as the authoritative assessment signal.
        Use AI practice only as a secondary training/improvement signal.
        Use this performance data only to adapt teaching style.
        Do not mention scores or learning level unless the student asks.
        """.trimIndent()
    }

    private suspend fun persistConversation() {

        val state = _uiState.value
        if (state.messages.none { it.role == "user" }) return

        val now = System.currentTimeMillis()
        val firstUserMessage = state.messages
            .firstOrNull { it.role == "user" }
            ?.text
            .orEmpty()

        val title = if (state.topicName.isNotBlank()) {
            "${state.topicName} Tutor"
        } else {
            firstUserMessage.take(45).ifBlank { "AI Tutor" }
        }

        val currentChatId = state.chatId

        if (currentChatId == null) {
            val createdAt = if (state.chatCreatedAt != 0L) state.chatCreatedAt else now

            val chat = TutorChat(
                title = title,
                topicId = state.topicId,
                subjectId = state.subjectId,
                subjectName = state.subjectName,
                topicName = state.topicName,
                messages = state.messages,
                createdAt = createdAt,
                updatedAt = now
            )

            repository.createChat(chat)
                .onSuccess { chatId ->
                    _uiState.update {
                        it.copy(chatId = chatId, chatCreatedAt = createdAt)
                    }
                }
                .onFailure { error ->
                    android.util.Log.e("Learn2HireTutor", "Failed to create tutor chat", error)
                }
        } else {
            val createdAt = state.chatCreatedAt
                .takeIf { it != 0L }
                ?: state.messages.minOfOrNull { it.timestamp }?.takeIf { it != 0L }
                ?: now

            val chat = TutorChat(
                id = currentChatId,
                title = title,
                topicId = state.topicId,
                subjectId = state.subjectId,
                subjectName = state.subjectName,
                topicName = state.topicName,
                messages = state.messages,
                createdAt = createdAt,
                updatedAt = now
            )

            repository.saveChat(chat)
                .onFailure { error ->
                    android.util.Log.e("Learn2HireTutor", "Failed to update tutor chat", error)
                }

            if (state.chatCreatedAt == 0L) {
                _uiState.update { it.copy(chatCreatedAt = createdAt) }
            }
        }
    }

    private fun assistantMessage(
        text: String
    ): TutorMessage {

        return TutorMessage(
            id = UUID.randomUUID().toString(),
            role = "assistant",
            text = text,
            timestamp = System.currentTimeMillis()
        )
    }
}
