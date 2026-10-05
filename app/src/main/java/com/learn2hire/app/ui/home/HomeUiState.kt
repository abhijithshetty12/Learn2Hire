package com.learn2hire.app.ui.home


enum class HomeRecommendationAction {
    REVIEW_TOPIC,
    TAKE_QUIZ,
    ASK_AI,
    INTERVIEW,
    PLANNER,
    AI_PRACTICE,
    STUDY
}

data class HomeRecommendationUi(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val actionLabel: String = "",
    val action: HomeRecommendationAction = HomeRecommendationAction.STUDY,
    val topicId: String? = null,
    val priority: Int = 0
)

data class HomeUiState(
    val userName: String = "",
    val completedTopics: Int = 0,
    val totalTopics: Int = 10,
    val overallProgress: Int = 0,
    val continueTopicId: String? = null,
    val continueTopicName: String = "",
    val continueSubjectName: String = "",
    val continueTopicProgress: Int = 0,
    val plannerTasks: List<HomePlannerTaskUi> = emptyList(),
    val latestQuizAccuracy: Int? = null,
    val latestInterviewCoverage: Int? = null,
    val latestAiPracticeAccuracy: Int? = null,
    val aiPracticeSessions: Int = 0,
    val focusTopicId: String? = null,
    val focusTopicName: String = "",
    val focusSubjectName: String = "",
    val focusAverageScore: Int? = null,
    val focusLatestScore: Int? = null,
    val focusAttempts: Int = 0,
    val focusLevel: String = "",
    val recommendations: List<HomeRecommendationUi> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class HomePlannerTaskUi(
    val id: String = "",
    val title: String = "",
    val estimatedMinutes: Int = 0,
    val daysLeft: Int = 0
)