package com.learn2hire.app.ui.interview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn2hire.app.data.model.InterviewAiFeedback
import com.learn2hire.app.data.model.InterviewResult
import com.learn2hire.app.data.model.InterviewSession
import com.learn2hire.app.data.repository.InterviewRepository
import com.learn2hire.app.data.repository.InterviewRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InterviewViewModel(
    private val repository: InterviewRepository = InterviewRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterviewUiState())

    private val _aiFeedback = MutableStateFlow<InterviewAiFeedback?>(null)
    val aiFeedback: StateFlow<InterviewAiFeedback?> = _aiFeedback.asStateFlow()

    private val _isAiFeedbackLoading = MutableStateFlow(false)
    val isAiFeedbackLoading: StateFlow<Boolean> = _isAiFeedbackLoading.asStateFlow()
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    fun selectRole(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun selectType(type: String) {
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    fun updateAnswer(answer: String) {
        if (_uiState.value.feedbackVisible) return
        _uiState.value = _uiState.value.copy(answer = answer)
    }

    fun startInterview() {
        val state = _uiState.value

        if (state.selectedRole.isBlank() || state.selectedType.isBlank()) return

        val questions = repository.getQuestions(
            role = state.selectedRole,
            type = state.selectedType
        )

        _uiState.value = state.copy(
            questions = questions,
            currentQuestionIndex = 0,
            answer = "",
            feedbackVisible = false,
            matchedKeyPoints = emptyList(),
            missedKeyPoints = emptyList(),
            results = emptyList(),
            isFinished = false,
            isSavingResult = false,
            isSessionSaved = false,
            errorMessage = null
        )
    }

    fun submitAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val answer = state.answer.trim()
        if (answer.isEmpty()) return

        val normalizedAnswer = answer.lowercase()

        val matched = question.keyPoints.filter { keyPoint ->
            normalizedAnswer.contains(keyPoint.lowercase())
        }

        val missed = question.keyPoints.filterNot { keyPoint ->
            normalizedAnswer.contains(keyPoint.lowercase())
        }

        val result = InterviewResult(
            questionId = question.id,
            question = question.question,
            answer = answer,
            matchedKeyPoints = matched,
            missedKeyPoints = missed
        )

        _uiState.value = state.copy(
            feedbackVisible = true,
            matchedKeyPoints = matched,
            missedKeyPoints = missed,
            results = state.results + result,
            errorMessage = null
        )

        _aiFeedback.value = null
        _isAiFeedbackLoading.value = true

        viewModelScope.launch {
            val feedback = repository.generateFeedback(
                role = state.selectedRole,
                type = state.selectedType,
                question = question,
                answer = answer,
                matchedKeyPoints = matched,
                missedKeyPoints = missed
            )

            _aiFeedback.value = feedback
            _isAiFeedbackLoading.value = false

            _uiState.value = _uiState.value.copy(
                results = _uiState.value.results.map { existing ->
                    if (existing.questionId == question.id) {
                        existing.copy(
                            aiWhatWentWell = feedback.whatWentWell,
                            aiWhatToImprove = feedback.whatToImprove,
                            aiImprovedAnswer = feedback.improvedAnswer,
                            usedAiFallback = feedback.usedFallback
                        )
                    } else {
                        existing
                    }
                }
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.feedbackVisible || state.currentQuestionIndex >= state.questions.lastIndex) {
            return
        }

        _aiFeedback.value = null
        _isAiFeedbackLoading.value = false

        _uiState.value = state.copy(
            currentQuestionIndex = state.currentQuestionIndex + 1,
            answer = "",
            feedbackVisible = false,
            matchedKeyPoints = emptyList(),
            missedKeyPoints = emptyList(),
            errorMessage = null
        )
    }

    fun finishInterview(onSaved: () -> Unit) {
        val state = _uiState.value

        if (!state.feedbackVisible || state.isSavingResult || state.isSessionSaved) return
        if (state.currentQuestionIndex != state.questions.lastIndex) return

        val totalKeyPoints = state.results.sumOf {
            it.matchedKeyPoints.size + it.missedKeyPoints.size
        }
        val matchedKeyPoints = state.results.sumOf {
            it.matchedKeyPoints.size
        }
        val percentage =
            if (totalKeyPoints == 0) 0
            else (matchedKeyPoints * 100) / totalKeyPoints

        _uiState.value = state.copy(
            isSavingResult = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                repository.saveSession(
                    InterviewSession(
                        role = state.selectedRole,
                        type = state.selectedType,
                        questionsCompleted = state.results.size,
                        matchedKeyPoints = matchedKeyPoints,
                        totalKeyPoints = totalKeyPoints,
                        coveragePercentage = percentage,
                        results = state.results,
                        completedAt = System.currentTimeMillis()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isFinished = true,
                    isSavingResult = false,
                    isSessionSaved = true,
                    errorMessage = null
                )

                onSaved()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSavingResult = false,
                    errorMessage = exception.message ?: "Unable to save interview session"
                )
            }
        }
    }

    fun resetInterview() {
        _aiFeedback.value = null
        _isAiFeedbackLoading.value = false
        _uiState.value = InterviewUiState()
    }
}
