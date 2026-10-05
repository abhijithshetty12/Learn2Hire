package com.learn2hire.app.ui.quiz

import com.learn2hire.app.data.model.QuizQuestion

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val answers: Map<String, Int> = emptyMap(),
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isSavingResult: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val totalQuestions: Int
        get() = questions.size
}
