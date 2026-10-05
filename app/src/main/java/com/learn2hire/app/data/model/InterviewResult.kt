package com.learn2hire.app.data.model

data class InterviewResult(
    val questionId: String = "",
    val question: String = "",
    val answer: String = "",
    val matchedKeyPoints: List<String> = emptyList(),
    val missedKeyPoints: List<String> = emptyList(),
    val aiWhatWentWell: String = "",
    val aiWhatToImprove: String = "",
    val aiImprovedAnswer: String = "",
    val usedAiFallback: Boolean = false
)
