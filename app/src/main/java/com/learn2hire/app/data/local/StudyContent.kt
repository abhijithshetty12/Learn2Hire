package com.learn2hire.app.data.local

import com.learn2hire.app.data.model.Subject
import com.learn2hire.app.data.model.Topic

object StudyContent {

    val subjects = listOf(
        Subject(
            id = "ml",
            name = "Machine Learning",
            description = "Learn core machine learning concepts and algorithms.",
            totalTopics = 4
        ),
        Subject(
            id = "wad",
            name = "Web Application Development",
            description = "Build modern web applications and understand web technologies.",
            totalTopics = 3
        ),
        Subject(
            id = "atcd",
            name = "Automata Theory & Compiler Design",
            description = "Study automata, formal languages and compiler fundamentals.",
            totalTopics = 3
        )
    )

    val topics = listOf(
        Topic(
            id = "ml1",
            subjectId = "ml",
            name = "Introduction to Machine Learning",
            description = "Understand the fundamentals and types of machine learning.",
            estimatedMinutes = 20,
            content = """
                Machine Learning is a branch of artificial intelligence that enables systems to learn patterns from data and make predictions or decisions without being explicitly programmed for every situation.

                The major types of machine learning are supervised learning, unsupervised learning and reinforcement learning.
            """.trimIndent(),
            keyConcepts = listOf(
                "Artificial Intelligence",
                "Training Data",
                "Supervised Learning",
                "Unsupervised Learning",
                "Reinforcement Learning"
            )
        ),
        Topic(
            id = "ml2",
            subjectId = "ml",
            name = "Classification",
            description = "Learn how classification algorithms predict categories.",
            estimatedMinutes = 25,
            content = """
                Classification is a supervised machine learning task in which a model predicts a discrete class label.

                Examples include spam detection, pass or fail prediction, disease classification and sentiment analysis.
            """.trimIndent(),
            keyConcepts = listOf(
                "Classification",
                "Class Labels",
                "Training",
                "Prediction"
            )
        ),
        Topic(
            id = "ml3",
            subjectId = "ml",
            name = "Regression",
            description = "Understand prediction of continuous numerical values.",
            estimatedMinutes = 25,
            content = """
                Regression is a supervised learning technique used to predict continuous numerical values.

                Examples include house price prediction, temperature forecasting and sales prediction.
            """.trimIndent(),
            keyConcepts = listOf(
                "Regression",
                "Continuous Values",
                "Features",
                "Prediction"
            )
        ),
        Topic(
            id = "ml4",
            subjectId = "ml",
            name = "Naïve Bayes",
            description = "Learn probabilistic classification using Bayes' theorem.",
            estimatedMinutes = 30,
            content = """
                Naïve Bayes is a probabilistic classification algorithm based on Bayes' theorem.

                It assumes that input features are conditionally independent given the class.

                Bayes' theorem is:

                P(C|X) = P(X|C) × P(C) / P(X)

                Naïve Bayes is commonly used for text classification, spam filtering and sentiment analysis.
            """.trimIndent(),
            keyConcepts = listOf(
                "Bayes' Theorem",
                "Prior Probability",
                "Likelihood",
                "Posterior Probability",
                "Conditional Independence"
            )
        ),

        Topic(
            id = "wad1",
            subjectId = "wad",
            name = "Client and Server",
            description = "Understand client-server web architecture.",
            estimatedMinutes = 20,
            content = """
                Web applications commonly use a client-server architecture.

                The client provides the user interface and sends requests. The server processes requests, performs business logic, communicates with databases and returns responses.
            """.trimIndent(),
            keyConcepts = listOf(
                "Client",
                "Server",
                "Request",
                "Response",
                "HTTP"
            )
        ),
        Topic(
            id = "wad2",
            subjectId = "wad",
            name = "Node.js and Express",
            description = "Learn server-side JavaScript and Express fundamentals.",
            estimatedMinutes = 30,
            content = """
                Node.js allows JavaScript to execute outside the browser.

                Express is a web framework for Node.js that simplifies routing, middleware and HTTP request handling.
            """.trimIndent(),
            keyConcepts = listOf(
                "Node.js",
                "Express",
                "Routes",
                "Middleware",
                "REST API"
            )
        ),
        Topic(
            id = "wad3",
            subjectId = "wad",
            name = "SQL and NoSQL",
            description = "Compare relational and non-relational databases.",
            estimatedMinutes = 25,
            content = """
                SQL databases commonly organize structured data into tables with defined schemas and relationships.

                NoSQL databases can use documents, key-value pairs and other flexible data models.
            """.trimIndent(),
            keyConcepts = listOf(
                "SQL",
                "NoSQL",
                "Tables",
                "Documents",
                "Database Schema"
            )
        ),

        Topic(
            id = "atcd1",
            subjectId = "atcd",
            name = "Finite Automata",
            description = "Learn the fundamentals of finite-state machines.",
            estimatedMinutes = 25,
            content = """
                Finite Automata are mathematical models of computation consisting of states and transitions.

                They are commonly used to recognize regular languages.
            """.trimIndent(),
            keyConcepts = listOf(
                "States",
                "Transitions",
                "Alphabet",
                "Initial State",
                "Final State"
            )
        ),
        Topic(
            id = "atcd2",
            subjectId = "atcd",
            name = "DFA and NFA",
            description = "Understand deterministic and nondeterministic finite automata.",
            estimatedMinutes = 30,
            content = """
                A DFA has exactly one transition for each state and input symbol.

                An NFA may have multiple possible transitions for an input symbol and can also support epsilon transitions in an epsilon-NFA.
            """.trimIndent(),
            keyConcepts = listOf(
                "DFA",
                "NFA",
                "Transition Function",
                "Epsilon Transition",
                "Acceptance"
            )
        ),
        Topic(
            id = "atcd3",
            subjectId = "atcd",
            name = "Regular Expressions",
            description = "Learn how regular expressions represent regular languages.",
            estimatedMinutes = 25,
            content = """
                Regular expressions provide an algebraic notation for describing regular languages.

                Regular expressions and finite automata have equivalent expressive power for regular languages.
            """.trimIndent(),
            keyConcepts = listOf(
                "Regular Expression",
                "Regular Language",
                "Union",
                "Concatenation",
                "Kleene Star"
            )
        )
    )

    fun getSubject(subjectId: String): Subject? {
        return subjects.find {
            it.id == subjectId
        }
    }

    fun getTopicsForSubject(subjectId: String): List<Topic> {
        return topics.filter {
            it.subjectId == subjectId
        }
    }

    fun getTopic(topicId: String): Topic? {
        return topics.find {
            it.id == topicId
        }
    }
}