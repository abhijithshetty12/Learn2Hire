package com.learn2hire.app.ui.practice

import com.learn2hire.app.data.model.AiPracticeQuestion

data class AiPracticeUiState(
    val topicId: String = "",
    val topicName: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val difficulty: String = "Standard",
    val questions: List<AiPracticeQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val score: Int = 0,
    val errorMessage: String? = null
) {
    val currentQuestion: AiPracticeQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val percentage: Int
        get() = if (questions.isEmpty()) 0
        else ((score * 100f) / questions.size).toInt()
}
