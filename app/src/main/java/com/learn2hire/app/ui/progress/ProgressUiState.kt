package com.learn2hire.app.ui.progress

data class SubjectProgressUi(
    val subjectId: String = "",
    val subjectName: String = "",
    val completedTopics: Int = 0,
    val totalTopics: Int = 0,
    val progress: Int = 0
)

data class RecentActivityUi(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val timestamp: Long = 0L,
    val type: ActivityType = ActivityType.STUDY
)

enum class ActivityType {
    STUDY,
    QUIZ,
    PLANNER,
    INTERVIEW,
    AI_PRACTICE
}


data class TopicPerformanceUi(
    val topicId: String = "",
    val topicName: String = "",
    val subjectName: String = "",
    val averageScore: Int? = null,
    val latestScore: Int? = null,
    val attempts: Int = 0,
    val level: String = "Not Assessed",
    val practiceSessions: Int = 0,
    val practiceAverageScore: Int? = null,
    val practiceLatestScore: Int? = null,
    val practiceTrend: String = ""
)

data class ProgressUiState(
    val overallProgress: Int = 0,
    val completedTopics: Int = 0,
    val totalTopics: Int = 10,
    val studyProgress: Int = 0,
    val averageQuizAccuracy: Int = 0,
    val bestQuizAccuracy: Int = 0,
    val quizzesCompleted: Int = 0,
    val aiPracticeSessions: Int = 0,
    val averageAiPracticeAccuracy: Int = 0,
    val bestAiPracticeAccuracy: Int = 0,
    val plannerTasks: Int = 0,
    val completedPlannerTasks: Int = 0,
    val plannerCompletion: Int = 0,
    val plannerSessions: Int = 0,
    val interviewSessions: Int = 0,
    val averageInterviewCoverage: Int = 0,
    val bestInterviewCoverage: Int = 0,
    val subjectProgress: List<SubjectProgressUi> = emptyList(),
    val topicPerformance: List<TopicPerformanceUi> = emptyList(),
    val recentActivity: List<RecentActivityUi> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
