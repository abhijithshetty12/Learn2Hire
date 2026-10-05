package com.learn2hire.app.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn2hire.app.data.local.StudyContent
import com.learn2hire.app.data.model.AiPracticeSession
import com.learn2hire.app.data.repository.AiPracticeRepository
import com.learn2hire.app.data.repository.AiPracticeRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiPracticeViewModel(
    private val repository: AiPracticeRepository = AiPracticeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiPracticeUiState())
    val uiState: StateFlow<AiPracticeUiState> = _uiState.asStateFlow()

    private var generatedAt: Long = 0L

    fun initialize(
        topicId: String,
        difficulty: String
    ) {
        if (
            _uiState.value.topicId == topicId &&
            _uiState.value.questions.isNotEmpty()
        ) {
            return
        }

        val topic = StudyContent.getTopic(topicId)
        if (topic == null) {
            _uiState.value = AiPracticeUiState(
                topicId = topicId,
                isLoading = false,
                errorMessage = "Topic not found."
            )
            return
        }

        val subject = StudyContent.getSubject(topic.subjectId)

        _uiState.value = AiPracticeUiState(
            topicId = topic.id,
            topicName = topic.name,
            subjectId = topic.subjectId,
            subjectName = subject?.name.orEmpty(),
            difficulty = difficulty,
            isLoading = true
        )

        viewModelScope.launch {
            generatedAt = System.currentTimeMillis()

            repository.generateQuestions(
                topicName = topic.name,
                subjectName = subject?.name.orEmpty(),
                lessonContent = topic.content,
                keyConcepts = topic.keyConcepts,
                difficulty = difficulty,
                count = 5
            ).onSuccess { generation ->
                _uiState.update {
                    it.copy(
                        questions = generation.questions,
                        currentQuestionIndex = 0,
                        selectedAnswers = emptyMap(),
                        isLoading = false,
                        isComplete = false,
                        score = 0,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                android.util.Log.e(
                    "Learn2HireAI",
                    "AI practice and local fallback both failed",
                    error
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Practice could not be prepared for this topic."
                    )
                }
            }
        }
    }

    fun selectAnswer(index: Int) {
        val question = _uiState.value.currentQuestion ?: return

        _uiState.update { state ->
            state.copy(
                selectedAnswers = state.selectedAnswers +
                        (question.id to index)
            )
        }
    }

    fun next() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return

        if (state.selectedAnswers[question.id] == null) return

        if (state.currentQuestionIndex < state.questions.lastIndex) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1
                )
            }
        } else {
            finish()
        }
    }

    fun retry() {
        val state = _uiState.value
        initializeFresh(
            topicId = state.topicId,
            difficulty = state.difficulty
        )
    }

    private fun initializeFresh(
        topicId: String,
        difficulty: String
    ) {
        _uiState.value = AiPracticeUiState()
        initialize(topicId, difficulty)
    }

    private fun finish() {
        val state = _uiState.value

        val score = state.questions.count { question ->
            state.selectedAnswers[question.id] ==
                    question.correctAnswerIndex
        }

        _uiState.update {
            it.copy(
                isComplete = true,
                score = score
            )
        }

        viewModelScope.launch {
            val completed = _uiState.value

            repository.saveSession(
                AiPracticeSession(
                    topicId = completed.topicId,
                    subjectId = completed.subjectId,
                    difficulty = completed.difficulty,
                    score = score,
                    total = completed.questions.size,
                    percentage = completed.percentage,
                    generatedAt = generatedAt,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
