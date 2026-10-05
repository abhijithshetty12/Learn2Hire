package com.learn2hire.app.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn2hire.app.data.local.StudyContent
import com.learn2hire.app.data.model.QuizAttempt
import com.learn2hire.app.data.repository.QuizAttemptRepository
import com.learn2hire.app.data.repository.QuizAttemptRepositoryImpl
import com.learn2hire.app.data.repository.QuizRepository
import com.learn2hire.app.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val attemptRepository: QuizAttemptRepository = QuizAttemptRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var loadedTopicId: String? = null

    fun loadQuiz(topicId: String) {
        if (loadedTopicId == topicId && _uiState.value.questions.isNotEmpty()) return
        loadedTopicId = topicId
        _uiState.value = QuizUiState(
            questions = quizRepository.getQuestionsForTopic(topicId)
        )
    }

    fun selectAnswer(index: Int) {
        val state = _uiState.value
        if (state.isFinished || state.isSavingResult) return
        _uiState.value = state.copy(
            selectedAnswerIndex = index,
            errorMessage = null
        )
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state.isFinished || state.isSavingResult) return

        val question = state.currentQuestion ?: return
        val selected = state.selectedAnswerIndex ?: return
        val updatedAnswers = state.answers + (question.id to selected)
        val newScore = if (selected == question.correctAnswerIndex) state.score + 1 else state.score
        val isLast = state.currentQuestionIndex == state.questions.lastIndex

        if (!isLast) {
            _uiState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedAnswerIndex = null,
                answers = updatedAnswers,
                score = newScore,
                errorMessage = null
            )
            return
        }

        saveFinalAttempt(
            state = state.copy(answers = updatedAnswers),
            finalScore = newScore
        )
    }

    private fun saveFinalAttempt(state: QuizUiState, finalScore: Int) {
        val topicId = loadedTopicId ?: return
        val total = state.questions.size
        if (total <= 0) return

        _uiState.value = state.copy(
            score = finalScore,
            isSavingResult = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val subjectId = StudyContent.getTopic(topicId)?.subjectId.orEmpty()
                attemptRepository.saveAttempt(
                    QuizAttempt(
                        topicId = topicId,
                        subjectId = subjectId,
                        score = finalScore,
                        total = total,
                        percentage = (finalScore * 100) / total,
                        answers = state.answers,
                        completedAt = System.currentTimeMillis()
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isSavingResult = false,
                    isFinished = true,
                    errorMessage = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSavingResult = false,
                    isFinished = false,
                    errorMessage = exception.message ?: "Unable to save quiz result"
                )
            }
        }
    }

    fun resetQuiz() {
        _uiState.value = QuizUiState(questions = _uiState.value.questions)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
