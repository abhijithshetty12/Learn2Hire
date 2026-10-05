package com.learn2hire.app.ui.progress

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.learn2hire.app.data.local.StudyContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private var studyListener: ListenerRegistration? = null
    private var quizListener: ListenerRegistration? = null
    private var plannerTaskListener: ListenerRegistration? = null
    private var plannerSessionListener: ListenerRegistration? = null
    private var interviewListener: ListenerRegistration? = null
    private var aiPracticeListener: ListenerRegistration? = null

    private var studyRows: List<StudyRow> = emptyList()
    private var quizRows: List<QuizRow> = emptyList()
    private var plannerTaskRows: List<PlannerTaskRow> = emptyList()
    private var plannerSessionRows: List<PlannerSessionRow> = emptyList()
    private var interviewRows: List<InterviewRow> = emptyList()
    private var aiPracticeRows: List<AiPracticeRow> = emptyList()

    private var studyLoaded = false
    private var quizLoaded = false
    private var plannerTasksLoaded = false
    private var plannerSessionsLoaded = false
    private var interviewsLoaded = false
    private var aiPracticeLoaded = false

    private var studyError: String? = null
    private var quizError: String? = null
    private var plannerTaskError: String? = null
    private var plannerSessionError: String? = null
    private var interviewError: String? = null
    private var aiPracticeError: String? = null

    init {
        observeProgress()
    }

    private fun observeProgress() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.value = ProgressUiState(
                isLoading = false,
                errorMessage = "Please sign in to view progress."
            )
            return
        }

        val user = firestore.collection("users").document(uid)

        studyListener = user.collection("studyProgress")
            .addSnapshotListener { snapshot, error ->
                studyLoaded = true
                studyError = error?.message
                if (error == null && snapshot != null) {
                    studyRows = snapshot.documents.map { document ->
                        StudyRow(
                            topicId = document.getString("topicId") ?: document.id,
                            subjectId = document.getString("subjectId").orEmpty(),
                            completed = document.getBoolean("completed") ?: false,
                            completedAt = document.getLong("completedAt"),
                            lastOpenedAt = document.getLong("lastOpenedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        quizListener = user.collection("quizAttempts")
            .addSnapshotListener { snapshot, error ->
                quizLoaded = true
                quizError = error?.message
                if (error == null && snapshot != null) {
                    quizRows = snapshot.documents.map { document ->
                        QuizRow(
                            id = document.id,
                            topicId = document.getString("topicId").orEmpty(),
                            score = document.getLong("score")?.toInt() ?: 0,
                            total = document.getLong("total")?.toInt() ?: 0,
                            percentage = document.getLong("percentage")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        plannerTaskListener = user.collection("plannerTasks")
            .addSnapshotListener { snapshot, error ->
                plannerTasksLoaded = true
                plannerTaskError = error?.message
                if (error == null && snapshot != null) {
                    plannerTaskRows = snapshot.documents.map { document ->
                        PlannerTaskRow(
                            id = document.id,
                            title = document.getString("title").orEmpty(),
                            completed = document.getBoolean("completed") ?: false,
                            createdAt = document.getLong("createdAt") ?: 0L,
                            completedAt = document.getLong("completedAt")
                        )
                    }
                }
                rebuildState()
            }

        plannerSessionListener = user.collection("plannerSessions")
            .addSnapshotListener { snapshot, error ->
                plannerSessionsLoaded = true
                plannerSessionError = error?.message
                if (error == null && snapshot != null) {
                    plannerSessionRows = snapshot.documents.map { document ->
                        PlannerSessionRow(
                            id = document.id,
                            title = document.getString("title").orEmpty(),
                            durationMinutes = document.getLong("durationMinutes")?.toInt() ?: 0,
                            createdAt = document.getLong("createdAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        interviewListener = user.collection("interviewSessions")
            .addSnapshotListener { snapshot, error ->
                interviewsLoaded = true
                interviewError = error?.message
                if (error == null && snapshot != null) {
                    interviewRows = snapshot.documents.map { document ->
                        InterviewRow(
                            id = document.id,
                            role = document.getString("role").orEmpty(),
                            type = document.getString("type").orEmpty(),
                            questionsCompleted = document.getLong("questionsCompleted")?.toInt() ?: 0,
                            coveragePercentage = document.getLong("coveragePercentage")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }

        aiPracticeListener = user.collection("aiPracticeSessions")
            .addSnapshotListener { snapshot, error ->
                aiPracticeLoaded = true
                aiPracticeError = error?.message
                if (error == null && snapshot != null) {
                    aiPracticeRows = snapshot.documents.map { document ->
                        AiPracticeRow(
                            id = document.id,
                            topicId = document.getString("topicId").orEmpty(),
                            difficulty = document.getString("difficulty") ?: "Standard",
                            score = document.getLong("score")?.toInt() ?: 0,
                            total = document.getLong("total")?.toInt() ?: 0,
                            percentage = document.getLong("percentage")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0L
                        )
                    }
                }
                rebuildState()
            }
    }

    private fun rebuildState() {
        val totalTopics = StudyContent.topics.size
        val completedRows = studyRows.filter { it.completed }
        val completedTopics = completedRows.map { it.topicId }.distinct().size.coerceAtMost(totalTopics)
        val studyProgress =
            if (totalTopics == 0) 0 else ((completedTopics * 100f) / totalTopics).toInt()

        val subjectProgress = StudyContent.subjects.map { subject ->
            val completed = completedRows
                .filter { it.subjectId == subject.id }
                .map { it.topicId }
                .distinct()
                .size
                .coerceAtMost(subject.totalTopics)

            SubjectProgressUi(
                subjectId = subject.id,
                subjectName = subject.name,
                completedTopics = completed,
                totalTopics = subject.totalTopics,
                progress = if (subject.totalTopics == 0) 0
                else ((completed * 100f) / subject.totalTopics).toInt()
            )
        }

        val averageQuizAccuracy =
            if (quizRows.isEmpty()) 0 else quizRows.map { it.percentage }.average().toInt()
        val bestQuizAccuracy = quizRows.maxOfOrNull { it.percentage } ?: 0

        val attemptsByTopic = quizRows.groupBy { it.topicId }
        val practiceByTopic = aiPracticeRows.groupBy { it.topicId }

        val topicPerformance = StudyContent.topics.map { topic ->
            val attempts = attemptsByTopic[topic.id]
                .orEmpty()
                .sortedBy { it.completedAt }
            val subjectName = StudyContent.getSubject(topic.subjectId)
                ?.name
                .orEmpty()
            val practice = practiceByTopic[topic.id].orEmpty().sortedBy { it.completedAt }
            val practiceAverage = practice.takeIf { it.isNotEmpty() }
                ?.map { it.percentage }?.average()?.toInt()
            val practiceLatest = practice.lastOrNull()?.percentage
            val practiceTrend = when {
                practice.size < 2 -> ""
                practice.last().percentage > practice.first().percentage -> "Improving"
                practice.last().percentage < practice.first().percentage -> "Needs consistency"
                else -> "Steady"
            }

            if (attempts.isEmpty()) {
                TopicPerformanceUi(
                    topicId = topic.id,
                    topicName = topic.name,
                    subjectName = subjectName,
                    practiceSessions = practice.size,
                    practiceAverageScore = practiceAverage,
                    practiceLatestScore = practiceLatest,
                    practiceTrend = practiceTrend
                )
            } else {
                val average = attempts
                    .map { it.percentage }
                    .average()
                    .toInt()

                TopicPerformanceUi(
                    topicId = topic.id,
                    topicName = topic.name,
                    subjectName = subjectName,
                    averageScore = average,
                    latestScore = attempts.last().percentage,
                    attempts = attempts.size,
                    level = performanceLevel(average),
                    practiceSessions = practice.size,
                    practiceAverageScore = practiceAverage,
                    practiceLatestScore = practiceLatest,
                    practiceTrend = practiceTrend
                )
            }
        }.sortedWith(
            compareBy<TopicPerformanceUi> {
                when (it.level) {
                    "Needs Practice" -> 0
                    "Developing" -> 1
                    "Strong" -> 2
                    else -> 3
                }
            }.thenBy { it.averageScore ?: Int.MAX_VALUE }
        )

        val averageAiPracticeAccuracy =
            if (aiPracticeRows.isEmpty()) 0 else aiPracticeRows.map { it.percentage }.average().toInt()
        val bestAiPracticeAccuracy = aiPracticeRows.maxOfOrNull { it.percentage } ?: 0

        val completedPlannerTasks = plannerTaskRows.count { it.completed }
        val plannerCompletion =
            if (plannerTaskRows.isEmpty()) 0
            else ((completedPlannerTasks * 100f) / plannerTaskRows.size).toInt()

        val averageInterviewCoverage =
            if (interviewRows.isEmpty()) 0
            else interviewRows.map { it.coveragePercentage }.average().toInt()

        val bestInterviewCoverage =
            interviewRows.maxOfOrNull { it.coveragePercentage } ?: 0

        val activities = buildList {
            completedRows.forEach { row ->
                add(
                    RecentActivityUi(
                        id = "study-${row.topicId}",
                        title = topicName(row.topicId),
                        subtitle = "Topic completed",
                        timestamp = row.completedAt ?: row.lastOpenedAt,
                        type = ActivityType.STUDY
                    )
                )
            }

            quizRows.forEach { row ->
                add(
                    RecentActivityUi(
                        id = "quiz-${row.id}",
                        title = topicName(row.topicId),
                        subtitle = "Quiz • ${row.percentage}% • ${row.score}/${row.total}",
                        timestamp = row.completedAt,
                        type = ActivityType.QUIZ
                    )
                )
            }

            plannerTaskRows.filter { it.completed }.forEach { row ->
                add(
                    RecentActivityUi(
                        id = "planner-task-${row.id}",
                        title = row.title.ifBlank { "Planner task" },
                        subtitle = "Planner task completed",
                        timestamp = row.completedAt ?: row.createdAt,
                        type = ActivityType.PLANNER
                    )
                )
            }

            plannerSessionRows.forEach { row ->
                add(
                    RecentActivityUi(
                        id = "planner-session-${row.id}",
                        title = row.title.ifBlank { "Study plan" },
                        subtitle = "Study plan • ${row.durationMinutes} min",
                        timestamp = row.createdAt,
                        type = ActivityType.PLANNER
                    )
                )
            }

            aiPracticeRows.forEach { row ->
                add(
                    RecentActivityUi(
                        id = "ai-practice-${row.id}",
                        title = topicName(row.topicId),
                        subtitle = "AI Practice • ${row.percentage}% • ${row.difficulty}",
                        timestamp = row.completedAt,
                        type = ActivityType.AI_PRACTICE
                    )
                )
            }

            interviewRows.forEach { row ->
                add(
                    RecentActivityUi(
                        id = "interview-${row.id}",
                        title = "${row.role} • ${row.type}",
                        subtitle = "Interview • ${row.coveragePercentage}% coverage • ${row.questionsCompleted} questions",
                        timestamp = row.completedAt,
                        type = ActivityType.INTERVIEW
                    )
                )
            }
        }.sortedByDescending { it.timestamp }.take(10)

        _uiState.value = ProgressUiState(
            overallProgress = studyProgress,
            completedTopics = completedTopics,
            totalTopics = totalTopics,
            studyProgress = studyProgress,
            averageQuizAccuracy = averageQuizAccuracy,
            bestQuizAccuracy = bestQuizAccuracy,
            quizzesCompleted = quizRows.size,
            aiPracticeSessions = aiPracticeRows.size,
            averageAiPracticeAccuracy = averageAiPracticeAccuracy,
            bestAiPracticeAccuracy = bestAiPracticeAccuracy,
            plannerTasks = plannerTaskRows.size,
            completedPlannerTasks = completedPlannerTasks,
            plannerCompletion = plannerCompletion,
            plannerSessions = plannerSessionRows.size,
            interviewSessions = interviewRows.size,
            averageInterviewCoverage = averageInterviewCoverage,
            bestInterviewCoverage = bestInterviewCoverage,
            subjectProgress = subjectProgress,
            topicPerformance = topicPerformance,
            recentActivity = activities,
            isLoading = !(studyLoaded && quizLoaded && plannerTasksLoaded && plannerSessionsLoaded && interviewsLoaded && aiPracticeLoaded),
            errorMessage = studyError ?: quizError ?: plannerTaskError ?: plannerSessionError ?: interviewError ?: aiPracticeError
        )
    }

    private fun topicName(topicId: String): String {
        return StudyContent.getTopic(topicId)?.name ?: "Learning activity"
    }

    private fun performanceLevel(score: Int): String = when {
        score < 60 -> "Needs Practice"
        score < 80 -> "Developing"
        else -> "Strong"
    }


    override fun onCleared() {
        studyListener?.remove()
        quizListener?.remove()
        plannerTaskListener?.remove()
        plannerSessionListener?.remove()
        interviewListener?.remove()
        aiPracticeListener?.remove()
        super.onCleared()
    }

    private data class StudyRow(
        val topicId: String,
        val subjectId: String,
        val completed: Boolean,
        val completedAt: Long?,
        val lastOpenedAt: Long
    )

    private data class QuizRow(
        val id: String,
        val topicId: String,
        val score: Int,
        val total: Int,
        val percentage: Int,
        val completedAt: Long
    )

    private data class PlannerTaskRow(
        val id: String,
        val title: String,
        val completed: Boolean,
        val createdAt: Long,
        val completedAt: Long?
    )

    private data class PlannerSessionRow(
        val id: String,
        val title: String,
        val durationMinutes: Int,
        val createdAt: Long
    )

    private data class AiPracticeRow(
        val id: String,
        val topicId: String,
        val difficulty: String,
        val score: Int,
        val total: Int,
        val percentage: Int,
        val completedAt: Long
    )

    private data class InterviewRow(
        val id: String,
        val role: String,
        val type: String,
        val questionsCompleted: Int,
        val coveragePercentage: Int,
        val completedAt: Long
    )
}
