package com.learn2hire.app.data.model

data class InterviewAiFeedback(
    val whatWentWell: String = "",
    val whatToImprove: String = "",
    val improvedAnswer: String = "",
    val usedFallback: Boolean = false
)
