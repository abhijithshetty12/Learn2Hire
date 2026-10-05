package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.StudentLearningProfile
import kotlinx.coroutines.flow.Flow

interface LearningProfileRepository {
    fun observeLearningProfile(): Flow<StudentLearningProfile>
}
