package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.StudyProgress
import kotlinx.coroutines.flow.Flow

interface StudyProgressRepository {

    fun observeStudyProgress(): Flow<List<StudyProgress>>

    suspend fun markTopicOpened(
        topicId: String,
        subjectId: String
    )

    suspend fun markTopicCompleted(
        topicId: String,
        subjectId: String
    )

    suspend fun markTopicIncomplete(
        topicId: String,
        subjectId: String
    )
}