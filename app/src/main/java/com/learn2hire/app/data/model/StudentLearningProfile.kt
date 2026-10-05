package com.learn2hire.app.data.model

enum class PerformanceLevel {
    WEAK,
    DEVELOPING,
    STRONG,
    NOT_ASSESSED
}

data class TopicPerformance(
    val topicId: String = "",
    val topicName: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val averageScore: Int = 0,
    val latestScore: Int = 0,
    val attempts: Int = 0,
    val level: PerformanceLevel = PerformanceLevel.NOT_ASSESSED
)

data class StudentLearningProfile(
    val completedTopics: Int = 0,
    val totalTopics: Int = 0,
    val studyProgress: Float = 0f,
    val quizAttempts: Int = 0,
    val averageQuizScore: Int = 0,
    val topicPerformance: List<TopicPerformance> = emptyList(),
    val weakTopics: List<TopicPerformance> = emptyList(),
    val developingTopics: List<TopicPerformance> = emptyList(),
    val strongTopics: List<TopicPerformance> = emptyList(),
    val interviewSessions: Int = 0,
    val averageInterviewCoverage: Int = 0,
    val aiPracticeSessions: Int = 0,
    val averageAiPracticeScore: Int = 0,
    val topicPracticePerformance: List<TopicPracticePerformance> = emptyList()
)

data class TopicPracticePerformance(
    val topicId: String = "",
    val sessions: Int = 0,
    val averageScore: Int = 0,
    val latestScore: Int = 0
)
