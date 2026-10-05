package com.learn2hire.app.ui.study

import com.learn2hire.app.data.model.StudyProgress
import com.learn2hire.app.data.model.Subject
import com.learn2hire.app.data.model.Topic

data class StudyUiState(
    val subjects: List<Subject> = emptyList(),
    val topics: List<Topic> = emptyList(),
    val progress: List<StudyProgress> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {

    fun isTopicCompleted(topicId: String): Boolean {
        return progress
            .firstOrNull { it.topicId == topicId }
            ?.completed == true
    }

    fun getTopicProgress(topicId: String): Float {
        return progress
            .firstOrNull { it.topicId == topicId }
            ?.progress ?: 0f
    }

    fun getCompletedTopicsForSubject(subjectId: String): Int {
        return progress.count {
            it.subjectId == subjectId && it.completed
        }
    }

    fun getSubjectProgress(
        subjectId: String,
        totalTopics: Int
    ): Float {
        if (totalTopics <= 0) {
            return 0f
        }

        return getCompletedTopicsForSubject(subjectId).toFloat() /
                totalTopics.toFloat()
    }
}