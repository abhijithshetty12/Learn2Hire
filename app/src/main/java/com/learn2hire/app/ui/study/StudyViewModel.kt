package com.learn2hire.app.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn2hire.app.data.local.StudyContent
import com.learn2hire.app.data.repository.StudyProgressRepository
import com.learn2hire.app.data.repository.StudyProgressRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyViewModel(
    private val repository: StudyProgressRepository =
        StudyProgressRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StudyUiState(
            subjects = StudyContent.subjects,
            topics = StudyContent.topics
        )
    )

    val uiState: StateFlow<StudyUiState> =
        _uiState.asStateFlow()

    init {
        observeProgress()
    }

    private fun observeProgress() {
        viewModelScope.launch {
            try {
                repository
                    .observeStudyProgress()
                    .collect { progress ->

                        val subjectsWithProgress =
                            StudyContent.subjects.map { subject ->

                                val completed =
                                    progress.count {
                                        it.subjectId == subject.id &&
                                                it.completed
                                    }

                                val total =
                                    StudyContent
                                        .getTopicsForSubject(subject.id)
                                        .size

                                subject.copy(
                                    progress = if (total > 0) {
                                        completed.toFloat() /
                                                total.toFloat()
                                    } else {
                                        0f
                                    },
                                    completedTopics = completed,
                                    totalTopics = total
                                )
                            }

                        _uiState.value =
                            _uiState.value.copy(
                                subjects = subjectsWithProgress,
                                topics = StudyContent.topics,
                                progress = progress,
                                isLoading = false,
                                errorMessage = null
                            )
                    }
            } catch (exception: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to load study progress"
                    )
            }
        }
    }

    fun markTopicOpened(
        topicId: String,
        subjectId: String
    ) {
        viewModelScope.launch {
            try {
                repository.markTopicOpened(
                    topicId = topicId,
                    subjectId = subjectId
                )
            } catch (exception: Exception) {
                setError(
                    exception.message
                        ?: "Unable to update topic"
                )
            }
        }
    }

    fun markTopicCompleted(
        topicId: String,
        subjectId: String
    ) {
        viewModelScope.launch {
            try {
                repository.markTopicCompleted(
                    topicId = topicId,
                    subjectId = subjectId
                )
            } catch (exception: Exception) {
                setError(
                    exception.message
                        ?: "Unable to complete topic"
                )
            }
        }
    }

    fun markTopicIncomplete(
        topicId: String,
        subjectId: String
    ) {
        viewModelScope.launch {
            try {
                repository.markTopicIncomplete(
                    topicId = topicId,
                    subjectId = subjectId
                )
            } catch (exception: Exception) {
                setError(
                    exception.message
                        ?: "Unable to update topic"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }

    private fun setError(message: String) {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = message
            )
    }
}