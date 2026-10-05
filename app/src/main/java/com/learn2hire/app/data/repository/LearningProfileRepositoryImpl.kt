package com.learn2hire.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.learn2hire.app.data.local.StudyContent
import com.learn2hire.app.data.model.InterviewSession
import com.learn2hire.app.data.model.PerformanceLevel
import com.learn2hire.app.data.model.QuizAttempt
import com.learn2hire.app.data.model.StudentLearningProfile
import com.learn2hire.app.data.model.StudyProgress
import com.learn2hire.app.data.model.TopicPerformance
import com.learn2hire.app.data.model.AiPracticeSession
import com.learn2hire.app.data.model.TopicPracticePerformance
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LearningProfileRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : LearningProfileRepository {

    override fun observeLearningProfile(): Flow<StudentLearningProfile> =
        callbackFlow {
            val uid = auth.currentUser?.uid

            if (uid == null) {
                trySend(StudentLearningProfile())
                close()
                return@callbackFlow
            }

            var studyProgress = emptyList<StudyProgress>()
            var quizAttempts = emptyList<QuizAttempt>()
            var interviewSessions = emptyList<InterviewSession>()
            var aiPracticeSessions = emptyList<AiPracticeSession>()

            fun emitProfile() {
                trySend(
                    buildProfile(
                        studyProgress = studyProgress,
                        quizAttempts = quizAttempts,
                        interviewSessions = interviewSessions,
                        aiPracticeSessions = aiPracticeSessions
                    )
                )
            }

            val userDocument = firestore
                .collection("users")
                .document(uid)

            val studyListener = userDocument
                .collection("studyProgress")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    studyProgress = snapshot
                        ?.documents
                        ?.mapNotNull {
                            it.toObject(StudyProgress::class.java)
                        }
                        ?: emptyList()

                    emitProfile()
                }

            val quizListener = userDocument
                .collection("quizAttempts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    quizAttempts = snapshot
                        ?.documents
                        ?.mapNotNull {
                            it.toObject(QuizAttempt::class.java)
                        }
                        ?: emptyList()

                    emitProfile()
                }

            val interviewListener = userDocument
                .collection("interviewSessions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    interviewSessions = snapshot
                        ?.documents
                        ?.mapNotNull {
                            it.toObject(InterviewSession::class.java)
                        }
                        ?: emptyList()

                    emitProfile()
                }


            val aiPracticeListener = userDocument
                .collection("aiPracticeSessions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        android.util.Log.e(
                            "Learn2HireProfile",
                            "Failed to observe AI practice sessions",
                            error
                        )
                        return@addSnapshotListener
                    }

                    aiPracticeSessions = snapshot
                        ?.documents
                        ?.mapNotNull {
                            it.toObject(AiPracticeSession::class.java)
                        }
                        ?: emptyList()

                    emitProfile()
                }

            awaitClose {
                studyListener.remove()
                quizListener.remove()
                interviewListener.remove()
                aiPracticeListener.remove()
            }
        }

    private fun buildProfile(
        studyProgress: List<StudyProgress>,
        quizAttempts: List<QuizAttempt>,
        interviewSessions: List<InterviewSession>,
        aiPracticeSessions: List<AiPracticeSession>
    ): StudentLearningProfile {
        val topics = StudyContent.topics
        val subjects = StudyContent.subjects

        val completedTopicIds = studyProgress
            .filter { it.completed }
            .map { it.topicId }
            .toSet()

        val totalTopics = topics.size
        val completedTopics = topics.count {
            it.id in completedTopicIds
        }

        val overallStudyProgress =
            if (totalTopics == 0) {
                0f
            } else {
                completedTopics.toFloat() / totalTopics.toFloat()
            }

        val attemptsByTopic = quizAttempts.groupBy {
            it.topicId
        }

        val performances = topics.map { topic ->
            val attempts = attemptsByTopic[topic.id]
                .orEmpty()
                .sortedBy { it.completedAt }

            val subject = subjects.firstOrNull {
                it.id == topic.subjectId
            }

            if (attempts.isEmpty()) {
                TopicPerformance(
                    topicId = topic.id,
                    topicName = topic.name,
                    subjectId = topic.subjectId,
                    subjectName = subject?.name.orEmpty()
                )
            } else {
                val average = attempts
                    .map { it.percentage }
                    .average()
                    .toInt()

                val latest = attempts.last().percentage

                TopicPerformance(
                    topicId = topic.id,
                    topicName = topic.name,
                    subjectId = topic.subjectId,
                    subjectName = subject?.name.orEmpty(),
                    averageScore = average,
                    latestScore = latest,
                    attempts = attempts.size,
                    level = classifyPerformance(average)
                )
            }
        }

        val averageQuizScore =
            if (quizAttempts.isEmpty()) {
                0
            } else {
                quizAttempts
                    .map { it.percentage }
                    .average()
                    .toInt()
            }

        val averageInterviewCoverage =
            if (interviewSessions.isEmpty()) {
                0
            } else {
                interviewSessions
                    .map { it.coveragePercentage }
                    .average()
                    .toInt()
            }

        val practicePerformance = aiPracticeSessions
            .groupBy { it.topicId }
            .map { (topicId, sessions) ->
                val sorted = sessions.sortedBy { it.completedAt }
                TopicPracticePerformance(
                    topicId = topicId,
                    sessions = sessions.size,
                    averageScore = sessions.map { it.percentage }.average().toInt(),
                    latestScore = sorted.lastOrNull()?.percentage ?: 0
                )
            }

        val averageAiPracticeScore =
            if (aiPracticeSessions.isEmpty()) 0
            else aiPracticeSessions.map { it.percentage }.average().toInt()

        return StudentLearningProfile(
            completedTopics = completedTopics,
            totalTopics = totalTopics,
            studyProgress = overallStudyProgress,
            quizAttempts = quizAttempts.size,
            averageQuizScore = averageQuizScore,
            topicPerformance = performances,
            weakTopics = performances.filter {
                it.level == PerformanceLevel.WEAK
            },
            developingTopics = performances.filter {
                it.level == PerformanceLevel.DEVELOPING
            },
            strongTopics = performances.filter {
                it.level == PerformanceLevel.STRONG
            },
            interviewSessions = interviewSessions.size,
            averageInterviewCoverage = averageInterviewCoverage,
            aiPracticeSessions = aiPracticeSessions.size,
            averageAiPracticeScore = averageAiPracticeScore,
            topicPracticePerformance = practicePerformance
        )
    }

    private fun classifyPerformance(
        averageScore: Int
    ): PerformanceLevel {
        return when {
            averageScore < 60 -> PerformanceLevel.WEAK
            averageScore < 80 -> PerformanceLevel.DEVELOPING
            else -> PerformanceLevel.STRONG
        }
    }
}
