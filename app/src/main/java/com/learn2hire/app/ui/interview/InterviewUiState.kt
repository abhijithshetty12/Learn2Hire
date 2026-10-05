package com.learn2hire.app.ui.interview

import com.learn2hire.app.data.model.InterviewQuestion
import com.learn2hire.app.data.model.InterviewResult

data class InterviewUiState(
    val selectedRole: String = "",
    val selectedType: String = "",
    val questions: List<InterviewQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answer: String = "",
    val feedbackVisible: Boolean = false,
    val matchedKeyPoints: List<String> = emptyList(),
    val missedKeyPoints: List<String> = emptyList(),
    val results: List<InterviewResult> = emptyList(),
    val isFinished: Boolean = false,
    val isSavingResult: Boolean = false,
    val isSessionSaved: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: InterviewQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val totalQuestions: Int
        get() = questions.size

    val totalKeyPoints: Int
        get() = results.sumOf {
            it.matchedKeyPoints.size + it.missedKeyPoints.size
        }

    val totalMatchedKeyPoints: Int
        get() = results.sumOf { it.matchedKeyPoints.size }

    val coveragePercentage: Int
        get() = if (totalKeyPoints == 0) 0
        else (totalMatchedKeyPoints * 100) / totalKeyPoints
}
