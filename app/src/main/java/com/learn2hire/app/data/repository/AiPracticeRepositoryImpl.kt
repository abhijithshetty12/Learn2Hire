package com.learn2hire.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.learn2hire.app.data.model.AiPracticeQuestion
import com.learn2hire.app.data.model.AiPracticeSession
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

class AiPracticeRepositoryImpl : AiPracticeRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val model = Firebase
        .ai(backend = GenerativeBackend.googleAI())
        .generativeModel(modelName = "gemini-3.8-flash")

    override suspend fun generateQuestions(
        topicName: String,
        subjectName: String,
        lessonContent: String,
        keyConcepts: List<String>,
        difficulty: String,
        count: Int
    ): Result<AiPracticeGenerationResult> {
        return try {
            Result.success(
                AiPracticeGenerationResult(
                    questions = generateWithGemini(
                        topicName,
                        subjectName,
                        lessonContent,
                        keyConcepts,
                        difficulty,
                        count
                    ),
                    usedFallback = false
                )
            )
        } catch (e: Exception) {
            android.util.Log.e(
                "Learn2HireAI",
                "Gemini practice generation failed. Using local fallback.",
                e
            )

            val fallback = buildLocalFallbackQuestions(
                topicName = topicName,
                subjectName = subjectName,
                lessonContent = lessonContent,
                keyConcepts = keyConcepts,
                difficulty = difficulty,
                count = count
            )

            if (fallback.size == count) {
                Result.success(
                    AiPracticeGenerationResult(
                        questions = fallback,
                        usedFallback = true
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }

    private suspend fun generateWithGemini(
        topicName: String,
        subjectName: String,
        lessonContent: String,
        keyConcepts: List<String>,
        difficulty: String,
        count: Int
    ): List<AiPracticeQuestion> {
        val prompt = """
You are generating practice MCQs for Learn2Hire.

Subject: $subjectName
Topic: $topicName
Difficulty: $difficulty

Lesson material:
$lessonContent

Key concepts:
${keyConcepts.joinToString(", ")}

Generate exactly $count different multiple-choice questions.

Rules:
- Use only facts supported by the supplied lesson material and key concepts.
- Every question must test a different idea or use a different question style.
- Do not repeat the same question stem.
- Foundation: recognition, definitions and direct understanding.
- Intermediate: interpretation, comparison and simple application.
- Challenge: reasoning, scenarios and concept relationships.
- Each question must have exactly 4 options.
- Exactly one option must be correct.
- correctAnswerIndex must be 0, 1, 2, or 3.
- Give a concise explanation grounded in the supplied lesson.
- Do not use markdown or code fences.
- Return ONLY a valid JSON array.

Required format:
[
  {
    "question": "Question text",
    "options": ["Option A", "Option B", "Option C", "Option D"],
    "correctAnswerIndex": 0,
    "explanation": "Explanation"
  }
]
""".trimIndent()

        val raw = model.generateContent(prompt).text?.trim()
            ?: throw IllegalStateException("AI returned an empty response.")

        val array = JSONArray(extractJsonArray(raw))

        val questions = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val optionArray = item.getJSONArray("options")
                if (optionArray.length() != 4) continue

                val options = List(4) { optionArray.getString(it) }
                val correctIndex = item.getInt("correctAnswerIndex")
                if (correctIndex !in 0..3) continue

                add(
                    AiPracticeQuestion(
                        id = "ai-${System.currentTimeMillis()}-$index",
                        question = item.getString("question").trim(),
                        options = options,
                        correctAnswerIndex = correctIndex,
                        explanation = item.optString(
                            "explanation",
                            "Review the lesson material for this concept."
                        ).trim()
                    )
                )
            }
        }

        require(questions.size == count) {
            "AI returned an invalid question set."
        }

        require(
            questions.map { normalize(it.question) }.distinct().size == questions.size
        ) {
            "AI returned duplicate questions."
        }

        return questions
    }

    private fun buildLocalFallbackQuestions(
        topicName: String,
        subjectName: String,
        lessonContent: String,
        keyConcepts: List<String>,
        difficulty: String,
        count: Int
    ): List<AiPracticeQuestion> {
        val bank = when (topicName) {
            "Introduction to Machine Learning" -> introductionMlBank()
            "Classification" -> classificationBank()
            "Regression" -> regressionBank()
            "Naïve Bayes" -> naiveBayesBank()
            "Client and Server" -> clientServerBank()
            "Node.js and Express" -> nodeExpressBank()
            "SQL and NoSQL" -> sqlNoSqlBank()
            "Finite Automata" -> finiteAutomataBank()
            "DFA and NFA" -> dfaNfaBank()
            "Regular Expressions" -> regularExpressionsBank()
            else -> genericBank(topicName, subjectName, lessonContent, keyConcepts)
        }

        val difficultyOrder = when (difficulty) {
            "Foundation" -> listOf(0, 1, 2, 3, 4)
            "Intermediate" -> listOf(1, 2, 3, 4, 0)
            "Challenge" -> listOf(4, 3, 2, 1, 0)
            else -> listOf(0, 2, 4, 1, 3)
        }

        val selected = difficultyOrder
            .mapNotNull { bank.getOrNull(it) }
            .distinctBy { normalize(it.question) }
            .take(count)

        if (selected.size >= count) return selected

        val extras = bank
            .filterNot { candidate ->
                selected.any {
                    normalize(it.question) == normalize(candidate.question)
                }
            }

        return (selected + extras)
            .distinctBy { normalize(it.question) }
            .take(count)
    }

    private fun introductionMlBank() = listOf(
        q("What best describes machine learning?",
            "Systems learn patterns from data to make predictions or decisions",
            "Every decision is manually programmed",
            "It only stores data",
            "It only creates web pages",
            "The lesson defines machine learning as systems learning patterns from data without explicit programming for every situation."),
        q("Which is listed as a major type of machine learning?",
            "Supervised learning", "Database learning", "Browser learning", "Compiler learning",
            "The lesson lists supervised, unsupervised and reinforcement learning as the major types."),
        q("Which type of learning is explicitly listed alongside supervised and reinforcement learning?",
            "Unsupervised learning", "Regression only", "HTTP learning", "Finite learning",
            "Unsupervised learning is one of the three major types stated in the lesson."),
        q("A system learns patterns from examples instead of receiving a rule for every situation. Which idea does this illustrate?",
            "Machine Learning", "Static programming only", "Client-server routing", "Regular expressions",
            "Learning patterns from data is the central idea of machine learning in the lesson."),
        q("Which statement is most consistent with the lesson?",
            "Machine learning can make predictions or decisions from learned patterns",
            "Machine learning requires every situation to be explicitly programmed",
            "Machine learning is only a database technique",
            "Machine learning cannot use data",
            "The lesson says machine learning enables predictions or decisions using patterns learned from data.")
    )

    private fun classificationBank() = listOf(
        q("What kind of output does classification predict?",
            "A discrete class label", "A continuous numerical value", "A database table", "An HTTP response",
            "The lesson defines classification as predicting a discrete class label."),
        q("Classification belongs to which learning category in the lesson?",
            "Supervised learning", "Unsupervised learning", "Reinforcement learning only", "No learning category",
            "Classification is explicitly described as a supervised machine learning task."),
        q("Which is an example of classification from the lesson?",
            "Spam detection", "House price prediction", "Temperature forecasting", "Sales amount prediction",
            "Spam detection is listed as a classification example."),
        q("A model predicts whether a student will pass or fail. What task is this?",
            "Classification", "Regression", "Clustering", "Database normalization",
            "Pass or fail prediction uses discrete labels and is listed as a classification example."),
        q("Why is sentiment analysis suitable for classification?",
            "It can predict a discrete category or label",
            "It must always predict a continuous number",
            "It only processes database schemas",
            "It requires epsilon transitions",
            "The lesson lists sentiment analysis as an example of classification, which predicts discrete class labels.")
    )

    private fun regressionBank() = listOf(
        q("What does regression predict?",
            "Continuous numerical values", "Only discrete class labels", "HTTP requests", "Regular languages",
            "The lesson defines regression as predicting continuous numerical values."),
        q("Regression is described as what type of learning technique?",
            "Supervised learning", "Unsupervised learning only", "Reinforcement learning only", "Non-learning",
            "Regression is explicitly described as a supervised learning technique."),
        q("Which example belongs to regression according to the lesson?",
            "House price prediction", "Spam or not spam", "Pass or fail", "Sentiment category",
            "House price prediction is listed as a regression example."),
        q("A model forecasts tomorrow's temperature as 31.4°C. Which task best matches this?",
            "Regression", "Classification", "Finite automata", "Routing",
            "Temperature forecasting predicts a continuous numerical value and is listed as a regression example."),
        q("Which output would most strongly indicate a regression problem?",
            "A predicted sales value of 125430.50", "A label of spam", "A label of pass", "A positive sentiment class",
            "Sales prediction is listed as regression because the target is a continuous numerical value.")
    )

    private fun naiveBayesBank() = listOf(
        q("Which theorem is the foundation of Naïve Bayes?",
            "Bayes' Theorem", "Kleene's Theorem", "Pythagorean Theorem", "CAP Theorem",
            "The lesson states that Naïve Bayes is based on Bayes' theorem."),
        q("What assumption does Naïve Bayes make about input features given the class?",
            "They are conditionally independent", "They must be identical", "They are always continuous", "They cannot affect probability",
            "The lesson explicitly states that input features are conditionally independent given the class."),
        q("In P(C|X) = P(X|C) × P(C) / P(X), which term represents the probability of the class after observing X?",
            "P(C|X)", "P(C)", "P(X)", "P(X|C)",
            "P(C|X) is the posterior probability: the probability of class C after observing X."),
        q("Which application is explicitly associated with Naïve Bayes in the lesson?",
            "Spam filtering", "House price regression", "HTTP routing", "DFA minimization",
            "The lesson lists text classification, spam filtering and sentiment analysis as common uses."),
        q("An email classifier evaluates a message and updates the probability that it is spam using Bayes' theorem. Which algorithm best matches this process?",
            "Naïve Bayes", "Linear regression", "K-Means", "Finite automaton only",
            "Naïve Bayes is a probabilistic classification algorithm based on Bayes' theorem and is commonly used for spam filtering.")
    )

    private fun clientServerBank() = listOf(
        q("What is a main responsibility of the client?",
            "Provide the user interface and send requests", "Perform all database storage only", "Define regular languages", "Train ML models only",
            "The lesson says the client provides the user interface and sends requests."),
        q("What does the server do with incoming requests?",
            "Processes them and performs business logic", "Only displays the user interface", "Only sends keyboard input", "Only defines class labels",
            "The server processes requests and performs business logic."),
        q("Which component communicates with databases according to the lesson?",
            "Server", "Client only", "Browser icon", "User interface only",
            "The server communicates with databases as part of request processing."),
        q("A browser sends a request and receives processed data. Which architecture does this describe?",
            "Client-server architecture", "DFA architecture", "Regression architecture", "K-Means architecture",
            "The lesson describes web applications as commonly using client-server architecture."),
        q("Which sequence best reflects the lesson?",
            "Client sends request → server processes → server returns response",
            "Server sends request → client stores database → no response",
            "Database sends UI → client compiles language",
            "Client performs every server responsibility",
            "The client sends requests and the server processes them and returns responses.")
    )

    private fun nodeExpressBank() = listOf(
        q("What does Node.js allow JavaScript to do?",
            "Execute outside the browser", "Only execute inside HTML", "Replace every database", "Define DFA transitions",
            "The lesson states that Node.js allows JavaScript to execute outside the browser."),
        q("What is Express?",
            "A web framework for Node.js", "A relational database", "A machine learning algorithm", "A regular expression engine only",
            "Express is described as a web framework for Node.js."),
        q("Which task does Express simplify?",
            "Routing", "Regression prediction", "DFA minimization", "Image clustering",
            "The lesson says Express simplifies routing, middleware and HTTP request handling."),
        q("An application needs middleware and HTTP request handling in Node.js. Which tool from the lesson fits best?",
            "Express", "SQL", "Naïve Bayes", "NFA",
            "Express specifically simplifies middleware and HTTP request handling for Node.js."),
        q("Which pairing is correct?",
            "Node.js runs JavaScript outside the browser; Express provides web framework features",
            "Express runs SQL tables; Node.js creates DFA states",
            "Node.js is a database; Express is a classifier",
            "Express replaces JavaScript",
            "This pairing directly matches the two definitions in the lesson.")
    )

    private fun sqlNoSqlBank() = listOf(
        q("How do SQL databases commonly organize structured data?",
            "Into tables", "Only into video files", "Only into state diagrams", "Only into neural networks",
            "The lesson says SQL databases commonly organize structured data into tables."),
        q("What is commonly associated with SQL databases in the lesson?",
            "Defined schemas and relationships", "Only key-value pairs", "No structure at all", "Epsilon transitions",
            "Defined schemas and relationships are explicitly associated with SQL databases."),
        q("Which data model can a NoSQL database use?",
            "Documents", "Only relational tables", "Only finite states", "Only class labels",
            "The lesson states that NoSQL databases can use documents and key-value pairs."),
        q("A project needs a flexible document-based data model. Which category best matches the lesson?",
            "NoSQL", "SQL only", "Regression", "DFA",
            "NoSQL databases can use documents and other flexible data models."),
        q("Which comparison is supported by the lesson?",
            "SQL commonly uses tables with defined schemas, while NoSQL can use flexible models",
            "SQL and NoSQL are identical in data organization",
            "NoSQL can only use tables",
            "SQL has no schemas",
            "This is the central SQL-versus-NoSQL distinction stated in the lesson.")
    )

    private fun finiteAutomataBank() = listOf(
        q("What are finite automata?",
            "Mathematical models of computation", "Database schemas", "Regression equations", "HTTP frameworks",
            "The lesson defines finite automata as mathematical models of computation."),
        q("What two structural elements are explicitly mentioned for finite automata?",
            "States and transitions", "Tables and documents", "Clients and servers", "Features and labels",
            "Finite automata are described as consisting of states and transitions."),
        q("What kind of languages are finite automata commonly used to recognize?",
            "Regular languages", "Only programming languages", "Only natural languages", "Only SQL queries",
            "The lesson says finite automata are commonly used to recognize regular languages."),
        q("A computational model changes between a finite set of states using transitions. What does this describe?",
            "Finite Automata", "Regression", "NoSQL", "Express",
            "States and transitions are the defining elements given for finite automata."),
        q("Which relationship is supported by the lesson?",
            "Finite automata can recognize regular languages",
            "Finite automata predict continuous values",
            "Finite automata are web frameworks",
            "Finite automata store documents",
            "Recognition of regular languages is the stated use of finite automata.")
    )

    private fun dfaNfaBank() = listOf(
        q("How many transitions does a DFA have for each state and input symbol?",
            "Exactly one", "Always zero", "At least five", "An unlimited number is required",
            "The lesson states that a DFA has exactly one transition for each state and input symbol."),
        q("What can an NFA have for an input symbol?",
            "Multiple possible transitions", "Exactly one transition only", "No states", "Only database relations",
            "The lesson says an NFA may have multiple possible transitions for an input symbol."),
        q("Which automaton can support epsilon transitions in its epsilon form?",
            "NFA", "DFA as defined here", "Regression model", "SQL database",
            "The lesson states that an epsilon-NFA can support epsilon transitions."),
        q("A state has two possible next states for the same input symbol. Which model best fits?",
            "NFA", "DFA", "Regression", "Client-server",
            "Multiple possible transitions for one input symbol are allowed in an NFA."),
        q("Which statement correctly distinguishes DFA and NFA?",
            "A DFA has exactly one transition per state/input pair, while an NFA may have multiple",
            "A DFA always has multiple transitions while an NFA has exactly one",
            "Neither uses transitions",
            "They are database models",
            "This distinction is stated directly in the lesson.")
    )

    private fun regularExpressionsBank() = listOf(
        q("What do regular expressions provide?",
            "An algebraic notation for describing regular languages", "A database schema", "A regression target", "A web server",
            "The lesson defines regular expressions as an algebraic notation for regular languages."),
        q("Regular expressions describe which type of language?",
            "Regular languages", "Only context-sensitive languages", "Only SQL", "Only JavaScript",
            "Regular expressions are used to describe regular languages."),
        q("What expressive relationship is stated between regular expressions and finite automata?",
            "They have equivalent expressive power for regular languages",
            "Regular expressions are always more powerful",
            "Finite automata cannot represent regular languages",
            "They are unrelated",
            "The lesson states that regular expressions and finite automata have equivalent expressive power for regular languages."),
        q("If a regular language can be represented by a finite automaton, what does the lesson imply?",
            "A regular expression has equivalent expressive power for it",
            "It cannot be represented algebraically",
            "It must be a regression problem",
            "It must use NoSQL",
            "The lesson explicitly states equivalence in expressive power for regular languages."),
        q("Which pair is most directly connected by the lesson?",
            "Regular expressions and finite automata", "Regression and HTTP", "NoSQL and DFA", "Express and Bayes' theorem",
            "The lesson directly relates regular expressions and finite automata through equivalent expressive power.")
    )

    private fun genericBank(
        topicName: String,
        subjectName: String,
        lessonContent: String,
        keyConcepts: List<String>
    ): List<AiPracticeQuestion> {
        val concepts = keyConcepts.filter { it.isNotBlank() }.distinct()
        if (concepts.isEmpty()) return emptyList()

        val distractorPool = (
                concepts + listOf(subjectName, "Unrelated concept", "Different topic", "None of these")
                ).distinct()

        return concepts.take(5).mapIndexed { index, concept ->
            val distractors = distractorPool
                .filterNot { it.equals(concept, true) }
                .take(3)
                .toMutableList()

            while (distractors.size < 3) {
                distractors.add("Alternative ${distractors.size + 1}")
            }

            val correctIndex = index % 4
            val options = distractors.toMutableList()
            options.add(correctIndex, concept)

            AiPracticeQuestion(
                id = "local-${System.currentTimeMillis()}-$index",
                question = when (index % 5) {
                    0 -> "Which concept is directly included in the $topicName lesson?"
                    1 -> "Which term is a key concept for understanding $topicName?"
                    2 -> "Which concept should you review when studying $topicName?"
                    3 -> "Which of these belongs to the lesson on $topicName?"
                    else -> "Which concept is most directly associated with $topicName in the study material?"
                },
                options = options,
                correctAnswerIndex = correctIndex,
                explanation = lessonContent
                    .replace("\n", " ")
                    .take(220)
                    .ifBlank { "$concept is listed as a key concept for $topicName." }
            )
        }
    }

    private fun q(
        question: String,
        correct: String,
        wrong1: String,
        wrong2: String,
        wrong3: String,
        explanation: String
    ): AiPracticeQuestion {
        val seed = question.hashCode()
        val correctIndex = (seed and Int.MAX_VALUE) % 4
        val wrong = mutableListOf(wrong1, wrong2, wrong3)
        val options = wrong.toMutableList()
        options.add(correctIndex, correct)

        return AiPracticeQuestion(
            id = "local-${seed.toUInt()}",
            question = question,
            options = options,
            correctAnswerIndex = correctIndex,
            explanation = explanation
        )
    }

    private fun normalize(value: String): String =
        value.lowercase()
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()

    override suspend fun saveSession(
        session: AiPracticeSession
    ): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw IllegalStateException("User must be signed in.")

            val document = firestore
                .collection("users")
                .document(uid)
                .collection("aiPracticeSessions")
                .document()

            document
                .set(session.copy(id = document.id))
                .await()

            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractJsonArray(text: String): String {
        val cleaned = text
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val start = cleaned.indexOf('[')
        val end = cleaned.lastIndexOf(']')

        require(start >= 0 && end > start) {
            "AI response did not contain a JSON array."
        }

        return cleaned.substring(start, end + 1)
    }
}
