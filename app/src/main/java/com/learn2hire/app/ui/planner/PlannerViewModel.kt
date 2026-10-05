package com.learn2hire.app.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn2hire.app.data.model.PlannerTask
import com.learn2hire.app.data.model.StudySession
import com.learn2hire.app.data.repository.PlannerRepository
import com.learn2hire.app.data.repository.PlannerRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlannerViewModel(
    private val repository: PlannerRepository = PlannerRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            try {
                repository.observeTasks().collect { tasks ->
                    _uiState.value = _uiState.value.copy(
                        tasks = tasks,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Unable to load planner tasks"
                )
            }
        }
    }

    fun updateAvailableMinutes(value: String) {
        _uiState.value = _uiState.value.copy(
            availableMinutes = value.filter { it.isDigit() }
        )
    }

    fun updateTaskTitle(value: String) {
        _uiState.value = _uiState.value.copy(taskTitle = value)
    }

    fun updateDaysLeft(value: String) {
        _uiState.value = _uiState.value.copy(
            daysLeft = value.filter { it.isDigit() }
        )
    }

    fun updateEstimatedMinutes(value: String) {
        _uiState.value = _uiState.value.copy(
            estimatedMinutes = value.filter { it.isDigit() }
        )
    }

    fun addTask() {
        val state = _uiState.value
        val title = state.taskTitle.trim()
        val days = state.daysLeft.toIntOrNull()
        val estimated = state.estimatedMinutes.toIntOrNull()

        if (title.isEmpty() || days == null || days < 0 || estimated == null || estimated <= 0) {
            _uiState.value = state.copy(
                errorMessage = "Enter a task name, valid days left, and estimated minutes."
            )
            return
        }

        val priority = when {
            days <= 2 -> "High"
            days <= 5 -> "Medium"
            else -> "Normal"
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                repository.addTask(
                    PlannerTask(
                        title = title,
                        daysLeft = days,
                        estimatedMinutes = estimated,
                        priority = priority,
                        createdAt = System.currentTimeMillis()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    taskTitle = "",
                    daysLeft = "",
                    estimatedMinutes = "",
                    generatedPlan = emptyList(),
                    isSaving = false
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = exception.message ?: "Unable to save task"
                )
            }
        }
    }

    fun removeTask(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(id)
                _uiState.value = _uiState.value.copy(
                    generatedPlan = emptyList(),
                    errorMessage = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = exception.message ?: "Unable to delete task"
                )
            }
        }
    }

    fun toggleTaskCompleted(task: PlannerTask) {
        viewModelScope.launch {
            try {
                repository.setTaskCompleted(task.id, !task.completed)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = exception.message ?: "Unable to update task"
                )
            }
        }
    }

    fun generatePlan() {
        val state = _uiState.value
        val available = state.availableMinutes.toIntOrNull() ?: return
        val activeTasks = state.tasks.filterNot { it.completed }

        if (available <= 0 || activeTasks.isEmpty()) {
            _uiState.value = state.copy(
                errorMessage = if (activeTasks.isEmpty()) {
                    "Add an unfinished task before generating a plan."
                } else {
                    "Enter your available study time."
                }
            )
            return
        }

        val sortedTasks = activeTasks.sortedWith(
            compareBy<PlannerTask> { priorityRank(it.priority) }
                .thenBy { it.daysLeft }
                .thenByDescending { it.estimatedMinutes }
        )

        val sessions = mutableListOf<StudySession>()
        var remainingTime = available

        for (task in sortedTasks) {
            if (remainingTime <= 0) break

            val suggestedTime = when (task.priority) {
                "High" -> minOf(task.estimatedMinutes, 40)
                "Medium" -> minOf(task.estimatedMinutes, 30)
                else -> minOf(task.estimatedMinutes, 20)
            }

            val allocatedTime = minOf(suggestedTime, remainingTime)

            if (allocatedTime > 0) {
                sessions.add(
                    StudySession(
                        title = task.title,
                        durationMinutes = allocatedTime
                    )
                )
                remainingTime -= allocatedTime
            }
        }

        if (remainingTime > 0) {
            for (task in sortedTasks) {
                if (remainingTime <= 0) break

                val alreadyAllocated = sessions
                    .filter { it.title == task.title }
                    .sumOf { it.durationMinutes }

                val taskRemaining = task.estimatedMinutes - alreadyAllocated
                if (taskRemaining <= 0) continue

                val extraTime = minOf(taskRemaining, remainingTime)
                if (extraTime <= 0) continue

                val existingIndex = sessions.indexOfFirst { it.title == task.title }

                if (existingIndex >= 0) {
                    val existing = sessions[existingIndex]
                    sessions[existingIndex] = existing.copy(
                        durationMinutes = existing.durationMinutes + extraTime
                    )
                } else {
                    sessions.add(
                        StudySession(
                            title = task.title,
                            durationMinutes = extraTime
                        )
                    )
                }

                remainingTime -= extraTime
            }
        }

        _uiState.value = state.copy(
            generatedPlan = sessions,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                repository.savePlan(sessions)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = exception.message ?: "Plan generated, but could not be saved"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun priorityRank(priority: String): Int {
        return when (priority) {
            "High" -> 0
            "Medium" -> 1
            else -> 2
        }
    }
}
