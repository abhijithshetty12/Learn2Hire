package com.learn2hire.app.ui.navigation

sealed class Screen(val route: String) {

    data object Login : Screen("login")
    data object Register : Screen("register")

    data object Home : Screen("home")
    data object Study : Screen("study")
    data object Interview : Screen("interview")
    data object Profile : Screen("profile")
    data object Planner : Screen("planner")
    data object Progress : Screen("progress")

    data object InterviewPractice : Screen("interview-practice")
    data object InterviewResult : Screen("interview-result")

    data object SubjectDetail : Screen("study/{subjectId}") {
        fun createRoute(subjectId: String): String =
            "study/$subjectId"
    }

    data object TopicDetail : Screen("topic/{topicId}") {
        fun createRoute(topicId: String): String =
            "topic/$topicId"
    }

    data object Quiz : Screen("quiz/{topicId}") {
        fun createRoute(topicId: String): String =
            "quiz/$topicId"
    }
    data object Tutor : Screen("tutor")

    data object TopicTutor : Screen("tutor/{topicId}") {
        fun createRoute(topicId: String): String =
            "tutor/$topicId"
    }

    data object AiPractice : Screen("ai-practice/{topicId}/{difficulty}") {
        fun createRoute(
            topicId: String,
            difficulty: String
        ): String = "ai-practice/$topicId/$difficulty"
    }

    data object QuizResult :
        Screen("quiz-result/{topicId}/{score}/{total}") {

        fun createRoute(
            topicId: String,
            score: Int,
            total: Int
        ): String {
            return "quiz-result/$topicId/$score/$total"
        }
    }
}