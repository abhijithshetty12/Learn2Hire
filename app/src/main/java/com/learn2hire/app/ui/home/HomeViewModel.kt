package com.learn2hire.app.ui.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.learn2hire.app.data.local.StudyContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var profileListener: ListenerRegistration? = null
    private var studyListener: ListenerRegistration? = null
    private var quizListener: ListenerRegistration? = null
    private var plannerListener: ListenerRegistration? = null
    private var interviewListener: ListenerRegistration? = null
    private var aiPracticeListener: ListenerRegistration? = null

    private var profileName = ""
    private var studyRows: List<StudyRow> = emptyList()
    private var quizRows: List<QuizRow> = emptyList()
    private var plannerRows: List<PlannerRow> = emptyList()
    private var interviewRows: List<InterviewRow> = emptyList()
    private var aiPracticeRows: List<AiPracticeRow> = emptyList()

    private var profileLoaded = false
    private var studyLoaded = false
    private var quizLoaded = false
    private var plannerLoaded = false
    private var interviewLoaded = false
    private var aiPracticeLoaded = false

    private var profileError: String? = null
    private var studyError: String? = null
    private var quizError: String? = null
    private var plannerError: String? = null
    private var interviewError: String? = null
    private var aiPracticeError: String? = null

    init {
        observeHome()
    }

    private fun observeHome() {
        val user = auth.currentUser

        if (user == null) {
            _uiState.value = HomeUiState(
                isLoading = false,
                errorMessage = "Please sign in to view your dashboard."
            )
            return
        }

        val userDocument = firestore.collection("users").document(user.uid)

        profileListener = userDocument.addSnapshotListener { snapshot, error ->
            profileLoaded = true
            profileError = error?.message
            if (error == null && snapshot != null) {
                profileName = snapshot.getString("name").orEmpty()
            }
            rebuildState()
        }

        studyListener = userDocument.collection("studyProgress")
            .addSnapshotListener { snapshot, error ->
                studyLoaded = true
                studyError = error?.message
                if (error == null && snapshot != null) {
                    studyRows = snapshot.documents.map { document ->
                        StudyRow(
                            topicId = document.getString("topicId") ?: document.id,
                            completed = document.getBoolean("completed") ?: false,
                            progress = document.getDouble("progress")?.toFloat()
                                ?: document.getLong("progress")?.toFloat()
                                ?: 0f,
                            lastOpenedAt = document.getLong("lastOpenedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        quizListener = userDocument.collection("quizAttempts")
            .addSnapshotListener { snapshot, error ->
                quizLoaded = true
                quizError = error?.message
                if (error == null && snapshot != null) {
                    quizRows = snapshot.documents.map { document ->
                        QuizRow(
                            topicId = document.getString("topicId").orEmpty(),
                            percentage = document.getLong("percentage")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        plannerListener = userDocument.collection("plannerTasks")
            .addSnapshotListener { snapshot, error ->
                plannerLoaded = true
                plannerError = error?.message
                if (error == null && snapshot != null) {
                    plannerRows = snapshot.documents.map { document ->
                        PlannerRow(
                            id = document.id,
                            title = document.getString("title").orEmpty(),
                            estimatedMinutes = document.getLong("estimatedMinutes")?.toInt() ?: 0,
                            daysLeft = document.getLong("daysLeft")?.toInt() ?: 0,
                            completed = document.getBoolean("completed") ?: false,
                            createdAt = document.getLong("createdAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        interviewListener = userDocument.collection("interviewSessions")
            .addSnapshotListener { snapshot, error ->
                interviewLoaded = true
                interviewError = error?.message
                if (error == null && snapshot != null) {
                    interviewRows = snapshot.documents.map { document ->
                        InterviewRow(
                            coveragePercentage =
                                document.getLong("coveragePercentage")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        aiPracticeListener = userDocument.collection("aiPracticeSessions")
            .addSnapshotListener { snapshot, error ->
                aiPracticeLoaded = true
                aiPracticeError = error?.message
                if (error == null && snapshot != null) {
                    aiPracticeRows = snapshot.documents.map { document ->
                        AiPracticeRow(
                            topicId = document.getString("topicId").orEmpty(),
                            percentage = document.getLong("percentage")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

    }

    private fun rebuildState() {
        val completedTopics = studyRows
            .filter { it.completed }
            .map { it.topicId }
            .distinct()
            .size
            .coerceAtMost(StudyContent.topics.size)

        val overallProgress =
            if (StudyContent.topics.isEmpty()) 0
            else ((completedTopics * 100f) / StudyContent.topics.size).toInt()

        val unfinishedRows = studyRows
            .filterNot { it.completed }
            .sortedByDescending { it.lastOpenedAt }

        val continueTopicId = when {
            unfinishedRows.isNotEmpty() -> unfinishedRows.first().topicId
            else -> StudyContent.topics.firstOrNull { topic ->
                studyRows.none { it.topicId == topic.id && it.completed }
            }?.id
        }

        val continueTopic = StudyContent.getTopic(continueTopicId ?: "")
        val continueRow = studyRows.firstOrNull { it.topicId == continueTopicId }

        val continueProgress = when {
            continueRow == null -> 0
            continueRow.completed -> 100
            continueRow.progress <= 1f -> (continueRow.progress * 100f).toInt()
            else -> continueRow.progress.toInt()
        }.coerceIn(0, 100)

        val latestQuiz = quizRows.maxByOrNull { it.completedAt }
        val latestInterview = interviewRows.maxByOrNull { it.completedAt }
        val latestAiPractice = aiPracticeRows.maxByOrNull { it.completedAt }
        val practiceByTopic = aiPracticeRows.groupBy { it.topicId }

        val assessedTopics = quizRows
            .groupBy { it.topicId }
            .mapNotNull { (topicId, attempts) ->
                val topic = StudyContent.getTopic(topicId)
                    ?: return@mapNotNull null

                val subject = StudyContent.getSubject(topic.subjectId)
                val sortedAttempts = attempts.sortedBy { it.completedAt }
                val averageScore = attempts
                    .map { it.percentage }
                    .average()
                    .toInt()

                TopicInsight(
                    topicId = topic.id,
                    topicName = topic.name,
                    subjectName = subject?.name.orEmpty(),
                    averageScore = averageScore,
                    latestScore = sortedAttempts.last().percentage,
                    attempts = attempts.size,
                    level = performanceLevel(averageScore)
                )
            }

        val focusTopic = assessedTopics.minWithOrNull(
            compareBy<TopicInsight> { it.averageScore }
                .thenByDescending { it.attempts }
        )

        val focusTasks = plannerRows
            .filterNot { it.completed }
            .sortedWith(
                compareBy<PlannerRow> { it.daysLeft }
                    .thenByDescending { it.createdAt }
            )
            .take(3)
            .map {
                HomePlannerTaskUi(
                    id = it.id,
                    title = it.title.ifBlank { "Study task" },
                    estimatedMinutes = it.estimatedMinutes,
                    daysLeft = it.daysLeft
                )
            }

        val recommendations = buildRecommendations(
            assessedTopics = assessedTopics,
            latestInterview = latestInterview,
            continueTopic = continueTopic,
            practiceByTopic = practiceByTopic
        )

        _uiState.value = HomeUiState(
            userName = profileName.ifBlank {
                auth.currentUser?.displayName.orEmpty()
            },
            completedTopics = completedTopics,
            totalTopics = StudyContent.topics.size,
            overallProgress = overallProgress,
            continueTopicId = continueTopic?.id,
            continueTopicName = continueTopic?.name.orEmpty(),
            continueSubjectName = StudyContent.getSubject(continueTopic?.subjectId.orEmpty())?.name.orEmpty(),
            continueTopicProgress = continueProgress,
            plannerTasks = focusTasks,
            latestQuizAccuracy = latestQuiz?.percentage,
            latestInterviewCoverage = latestInterview?.coveragePercentage,
            latestAiPracticeAccuracy = latestAiPractice?.percentage,
            aiPracticeSessions = aiPracticeRows.size,
            focusTopicId = focusTopic?.topicId,
            focusTopicName = focusTopic?.topicName.orEmpty(),
            focusSubjectName = focusTopic?.subjectName.orEmpty(),
            focusAverageScore = focusTopic?.averageScore,
            focusLatestScore = focusTopic?.latestScore,
            focusAttempts = focusTopic?.attempts ?: 0,
            focusLevel = focusTopic?.level.orEmpty(),
            recommendations = recommendations,
            isLoading = !(profileLoaded && studyLoaded && quizLoaded && plannerLoaded && interviewLoaded && aiPracticeLoaded),
            errorMessage = profileError ?: studyError ?: quizError ?: plannerError ?: interviewError ?: aiPracticeError
        )
    }

    private fun buildRecommendations(
        assessedTopics: List<TopicInsight>,
        latestInterview: InterviewRow?,
        continueTopic: com.learn2hire.app.data.model.Topic?,
        practiceByTopic: Map<String, List<AiPracticeRow>>
    ): List<HomeRecommendationUi> {
        val recommendations = mutableListOf<HomeRecommendationUi>()

        assessedTopics.forEach { topic ->
            val practice = practiceByTopic[topic.topicId].orEmpty()
            val latestPractice = practice.maxByOrNull { it.completedAt }

            if (topic.averageScore < 60 && latestPractice != null && latestPractice.percentage >= 70) {
                recommendations += HomeRecommendationUi(
                    id = "practice-improved-${topic.topicId}",
                    title = "Ready to reassess ${topic.topicName}",
                    text = "Your quiz average is ${topic.averageScore}%, but your latest AI practice reached ${latestPractice.percentage}%. Test your improvement with the assessment.",
                    actionLabel = "Take Quiz",
                    action = HomeRecommendationAction.TAKE_QUIZ,
                    topicId = topic.topicId,
                    priority = 95
                )
            } else if (topic.averageScore < 80 && practice.isEmpty()) {
                recommendations += HomeRecommendationUi(
                    id = "try-practice-${topic.topicId}",
                    title = "Practice ${topic.topicName} with AI",
                    text = "Your quiz performance is ${topic.averageScore}%. Try adaptive practice before your next assessment.",
                    actionLabel = "Practice with AI",
                    action = HomeRecommendationAction.AI_PRACTICE,
                    topicId = topic.topicId,
                    priority = if (topic.averageScore < 60) 90 else 58
                )
            } else if (topic.averageScore < 60 && latestPractice != null && latestPractice.percentage < 60) {
                recommendations += HomeRecommendationUi(
                    id = "practice-review-${topic.topicId}",
                    title = "Keep working on ${topic.topicName}",
                    text = "Quiz average ${topic.averageScore}% • latest AI practice ${latestPractice.percentage}%. Review the lesson or ask AI before trying again.",
                    actionLabel = "Review",
                    action = HomeRecommendationAction.REVIEW_TOPIC,
                    topicId = topic.topicId,
                    priority = 92
                )
            }
        }

        assessedTopics
            .filter { it.averageScore < 60 }
            .sortedBy { it.averageScore }
            .take(2)
            .forEach { topic ->
                recommendations += HomeRecommendationUi(
                    id = "weak-${topic.topicId}",
                    title = "Review ${topic.topicName}",
                    text = "Your average quiz score is ${topic.averageScore}%. Reinforce this topic before moving on.",
                    actionLabel = "Review",
                    action = HomeRecommendationAction.REVIEW_TOPIC,
                    topicId = topic.topicId,
                    priority = 100 - topic.averageScore
                )
            }

        assessedTopics
            .filter { it.averageScore in 60..79 }
            .sortedBy { it.averageScore }
            .take(1)
            .forEach { topic ->
                recommendations += HomeRecommendationUi(
                    id = "developing-${topic.topicId}",
                    title = "Practice ${topic.topicName}",
                    text = "You're developing at ${topic.averageScore}%. Another quiz can strengthen your understanding.",
                    actionLabel = "Take Quiz",
                    action = HomeRecommendationAction.TAKE_QUIZ,
                    topicId = topic.topicId,
                    priority = 60
                )
            }

        val assessedTopicIds = assessedTopics.map { it.topicId }.toSet()
        val completedTopicIds = studyRows
            .filter { it.completed }
            .map { it.topicId }
            .toSet()

        StudyContent.topics
            .firstOrNull { it.id in completedTopicIds && it.id !in assessedTopicIds }
            ?.let { topic ->
                recommendations += HomeRecommendationUi(
                    id = "assess-${topic.id}",
                    title = "Assess ${topic.name}",
                    text = "You've completed this topic but haven't taken its quiz yet.",
                    actionLabel = "Take Quiz",
                    action = HomeRecommendationAction.TAKE_QUIZ,
                    topicId = topic.id,
                    priority = 55
                )
            }

        if (latestInterview != null && latestInterview.coveragePercentage < 60) {
            recommendations += HomeRecommendationUi(
                id = "interview",
                title = "Practice another interview",
                text = "Your latest interview coverage is ${latestInterview.coveragePercentage}%. Practice again and cover more key points.",
                actionLabel = "Practice",
                action = HomeRecommendationAction.INTERVIEW,
                priority = 50
            )
        }

        plannerRows
            .filterNot { it.completed }
            .minWithOrNull(
                compareBy<PlannerRow> { it.daysLeft }
                    .thenByDescending { it.createdAt }
            )
            ?.let { task ->
                recommendations += HomeRecommendationUi(
                    id = "planner-${task.id}",
                    title = task.title.ifBlank { "Complete your study task" },
                    text = "${task.estimatedMinutes} min • ${
                        when {
                            task.daysLeft <= 0 -> "Due today"
                            task.daysLeft == 1 -> "1 day left"
                            else -> "${task.daysLeft} days left"
                        }
                    }",
                    actionLabel = "Open Planner",
                    action = HomeRecommendationAction.PLANNER,
                    priority = if (task.daysLeft <= 0) 65 else 35
                )
            }

        if (continueTopic != null) {
            recommendations += HomeRecommendationUi(
                id = "continue-${continueTopic.id}",
                title = "Continue ${continueTopic.name}",
                text = "Keep progressing through your next unfinished topic.",
                actionLabel = "Continue",
                action = HomeRecommendationAction.REVIEW_TOPIC,
                topicId = continueTopic.id,
                priority = 25
            )
        }

        if (recommendations.isEmpty()) {
            recommendations += HomeRecommendationUi(
                id = "explore",
                title = "Keep building your skills",
                text = "Explore a topic, take a quiz, or practice an interview to keep improving.",
                actionLabel = "Study",
                action = HomeRecommendationAction.STUDY,
                priority = 1
            )
        }

        return recommendations
            .distinctBy { it.id }
            .sortedByDescending { it.priority }
            .take(4)
    }


    override fun onCleared() {
        profileListener?.remove()
        studyListener?.remove()
        quizListener?.remove()
        plannerListener?.remove()
        interviewListener?.remove()
        aiPracticeListener?.remove()
        super.onCleared()
    }

    private data class StudyRow(
        val topicId: String,
        val completed: Boolean,
        val progress: Float,
        val lastOpenedAt: Long
    )

    private data class QuizRow(
        val topicId: String,
        val percentage: Int,
        val completedAt: Long
    )

    private data class PlannerRow(
        val id: String,
        val title: String,
        val estimatedMinutes: Int,
        val daysLeft: Int,
        val completed: Boolean,
        val createdAt: Long
    )

    private data class AiPracticeRow(
        val topicId: String,
        val percentage: Int,
        val completedAt: Long
    )

    private data class InterviewRow(
        val coveragePercentage: Int,
        val completedAt: Long
    )

    private data class TopicInsight(
        val topicId: String,
        val topicName: String,
        val subjectName: String,
        val averageScore: Int,
        val latestScore: Int,
        val attempts: Int,
        val level: String
    )

    private fun performanceLevel(score: Int): String = when {
        score < 60 -> "Needs Practice"
        score < 80 -> "Developing"
        else -> "Strong"
    }
}