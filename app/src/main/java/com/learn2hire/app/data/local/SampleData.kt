package com.learn2hire.app.data.local

import com.learn2hire.app.data.model.Subject
import com.learn2hire.app.data.model.Topic

object SampleData {

    val subjects = listOf(
        Subject(
            id = "ml",
            name = "Machine Learning",
            description = "Learn core ML concepts and algorithms",
            progress = 0.64f,
            completedTopics = 4,
            totalTopics = 7
        ),
        Subject(
            id = "wad",
            name = "Web Application Development",
            description = "Build modern web applications",
            progress = 0.42f,
            completedTopics = 3,
            totalTopics = 8
        ),
        Subject(
            id = "atcd",
            name = "Automata Theory",
            description = "Understand automata and formal languages",
            progress = 0.28f,
            completedTopics = 2,
            totalTopics = 7
        )
    )

    val topics = listOf(

        Topic(
            id = "ml1",
            subjectId = "ml",
            name = "Introduction to Machine Learning",
            description = "Understand the fundamentals of machine learning.",
            estimatedMinutes = 6,
            content = """
            Machine Learning is a branch of artificial intelligence that enables
            computers to learn patterns from data and make predictions or
            decisions without being explicitly programmed for every task.

            Machine learning is commonly divided into supervised learning,
            unsupervised learning and reinforcement learning.
        """.trimIndent(),
            keyConcepts = listOf(
                "Training data",
                "Features and labels",
                "Supervised learning",
                "Unsupervised learning"
            ),
            completed = true
        ),

        Topic(
            id = "ml4",
            subjectId = "ml",
            name = "Naïve Bayes",
            description = "Learn probabilistic classification using Bayes' theorem.",
            estimatedMinutes = 8,
            content = """
            Naïve Bayes is a probabilistic machine learning algorithm commonly
            used for classification.

            It is based on Bayes' theorem and assumes that the features used
            for prediction are conditionally independent given the class.

            Despite this simplifying assumption, Naïve Bayes performs well in
            many applications such as spam detection, document classification
            and sentiment analysis.
        """.trimIndent(),
            keyConcepts = listOf(
                "Bayes' theorem",
                "Conditional probability",
                "Prior probability",
                "Likelihood",
                "Feature independence"
            ),
            completed = true
        ),

        Topic(
            id = "ml5",
            subjectId = "ml",
            name = "Decision Trees",
            description = "Learn how decision trees make predictions.",
            estimatedMinutes = 8,
            content = """
            Decision Trees are supervised machine learning models that make
            predictions by repeatedly splitting data according to feature values.

            The resulting structure resembles a tree containing decision nodes,
            branches and leaf nodes.
        """.trimIndent(),
            keyConcepts = listOf(
                "Root node",
                "Decision node",
                "Leaf node",
                "Information gain"
            )
        ),

        Topic(
            id = "ml6",
            subjectId = "ml",
            name = "K-Means Clustering",
            description = "Understand unsupervised clustering with K-Means.",
            estimatedMinutes = 10,
            content = """
            K-Means is an unsupervised learning algorithm used to divide data
            into K clusters.

            Data points are assigned to the nearest centroid and the centroids
            are repeatedly updated until the clusters stabilize.
        """.trimIndent(),
            keyConcepts = listOf(
                "Clusters",
                "Centroids",
                "Distance",
                "Iterations"
            )
        )
    )
}