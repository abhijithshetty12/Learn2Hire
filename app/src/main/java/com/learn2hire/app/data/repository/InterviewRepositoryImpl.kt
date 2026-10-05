package com.learn2hire.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.learn2hire.app.data.local.SampleInterviewData
import com.learn2hire.app.data.model.InterviewAiFeedback
import com.learn2hire.app.data.model.InterviewQuestion
import com.learn2hire.app.data.model.InterviewSession
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class InterviewRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : InterviewRepository {

    private val model = Firebase
        .ai(backend = GenerativeBackend.googleAI())
        .generativeModel(modelName = "gemini-3.8-flash")

    override fun getQuestions(
        role: String,
        type: String
    ): List<InterviewQuestion> {
        return SampleInterviewData.getQuestions(
            role = role,
            type = type
        )
    }

    override suspend fun generateFeedback(
        role: String,
        type: String,
        question: InterviewQuestion,
        answer: String,
        matchedKeyPoints: List<String>,
        missedKeyPoints: List<String>
    ): InterviewAiFeedback {
        return try {
            val prompt = """
You are an interview coach inside Learn2Hire.

Role: $role
Interview type: $type
Question: ${question.question}
Candidate answer: $answer
Expected key points: ${question.keyPoints.joinToString(", ")}
Matched key points: ${matchedKeyPoints.joinToString(", ")}
Missed key points: ${missedKeyPoints.joinToString(", ")}

Give concise, constructive feedback. Do not change or calculate the score.
Return ONLY valid JSON:
{
  "whatWentWell": "1-2 concise sentences",
  "whatToImprove": "1-2 concise sentences",
  "improvedAnswer": "A stronger interview-ready answer in 2-4 sentences"
}
""".trimIndent()

            val raw = model.generateContent(prompt).text?.trim()
                ?: throw IllegalStateException("AI returned an empty response.")
            val json = extractJsonObject(raw)
            val obj = JSONObject(json)

            InterviewAiFeedback(
                whatWentWell = obj.optString("whatWentWell").trim(),
                whatToImprove = obj.optString("whatToImprove").trim(),
                improvedAnswer = obj.optString("improvedAnswer").trim(),
                usedFallback = false
            )
        } catch (e: Exception) {
            android.util.Log.e(
                "Learn2HireAI",
                "Interview AI feedback failed. Using key-point fallback.",
                e
            )

            InterviewAiFeedback(
                whatWentWell = if (matchedKeyPoints.isNotEmpty()) {
                    "You covered ${matchedKeyPoints.joinToString(", ")}."
                } else {
                    "You attempted the question and provided an answer to review."
                },
                whatToImprove = if (missedKeyPoints.isNotEmpty()) {
                    "Strengthen your answer by mentioning ${missedKeyPoints.joinToString(", ")}."
                } else {
                    "Your answer covered all expected key points. Focus on keeping it clear and concise."
                },
                improvedAnswer = buildFallbackAnswer(
                    answer = answer,
                    matchedKeyPoints = matchedKeyPoints,
                    missedKeyPoints = missedKeyPoints
                ),
                usedFallback = true
            )
        }
    }

    private fun buildFallbackAnswer(
        answer: String,
        matchedKeyPoints: List<String>,
        missedKeyPoints: List<String>
    ): String {
        val points = (matchedKeyPoints + missedKeyPoints).distinct()
        return if (points.isEmpty()) {
            answer
        } else {
            "A stronger answer should clearly explain ${points.joinToString(", ")} and connect these points directly to the question."
        }
    }

    private fun extractJsonObject(text: String): String {
        val cleaned = text
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val start = cleaned.indexOf('{')
        val end = cleaned.lastIndexOf('}')
        require(start >= 0 && end > start) {
            "AI response did not contain a JSON object."
        }
        return cleaned.substring(start, end + 1)
    }

    override suspend fun saveSession(session: InterviewSession) {
        val uid = requireNotNull(auth.currentUser?.uid) {
            "User is not signed in"
        }

        val document = firestore
            .collection("users")
            .document(uid)
            .collection("interviewSessions")
            .document()

        document.set(
            session.copy(id = document.id)
        ).await()
    }
}
