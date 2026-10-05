package com.learn2hire.app.ui.planner

import com.learn2hire.app.data.model.PlannerTask
import com.learn2hire.app.data.model.StudySession

data class PlannerUiState(
    val availableMinutes: String = "90",
    val taskTitle: String = "",
    val daysLeft: String = "",
    val estimatedMinutes: String = "",
    val tasks: List<PlannerTask> = emptyList(),
    val generatedPlan: List<StudySession> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
