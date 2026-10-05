package com.learn2hire.app.data.model

data class PlannerTask(
    val id: String = "",
    val title: String = "",
    val daysLeft: Int = 0,
    val estimatedMinutes: Int = 0,
    val priority: String = "Normal",
    val completed: Boolean = false,
    val createdAt: Long = 0L,
    val completedAt: Long? = null
)
