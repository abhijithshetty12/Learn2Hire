package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.PlannerTask
import com.learn2hire.app.data.model.StudySession
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    fun observeTasks(): Flow<List<PlannerTask>>
    fun observeSessions(): Flow<List<StudySession>>
    suspend fun addTask(task: PlannerTask)
    suspend fun deleteTask(taskId: String)
    suspend fun setTaskCompleted(taskId: String, completed: Boolean)
    suspend fun savePlan(sessions: List<StudySession>)
}
