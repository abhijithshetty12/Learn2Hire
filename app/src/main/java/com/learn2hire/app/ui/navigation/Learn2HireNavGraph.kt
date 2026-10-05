package com.learn2hire.app.ui.navigation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learn2hire.app.ui.interview.InterviewPracticeScreen
import com.learn2hire.app.ui.interview.InterviewResultScreen
import com.learn2hire.app.ui.interview.InterviewViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learn2hire.app.ui.home.HomeScreen
import com.learn2hire.app.ui.interview.InterviewScreen
import com.learn2hire.app.ui.planner.PlannerScreen
import com.learn2hire.app.ui.profile.ProfileScreen
import com.learn2hire.app.ui.quiz.QuizResultScreen
import com.learn2hire.app.ui.quiz.QuizScreen
import com.learn2hire.app.ui.quiz.QuizViewModel
import com.learn2hire.app.ui.study.StudyScreen
import com.learn2hire.app.ui.study.StudyViewModel
import com.learn2hire.app.ui.study.SubjectDetailScreen
import com.learn2hire.app.ui.study.TopicDetailScreen
import com.learn2hire.app.ui.progress.ProgressScreen
import com.learn2hire.app.ui.practice.AiPracticeScreen
import com.learn2hire.app.ui.practice.AiPracticeViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.learn2hire.app.ui.auth.AuthViewModel
import com.learn2hire.app.ui.auth.LoginScreen
import com.learn2hire.app.ui.auth.RegisterScreen
import com.learn2hire.app.ui.profile.ProfileViewModel
import com.learn2hire.app.ui.theme.ThemeMode
import com.learn2hire.app.ui.tutor.TutorScreen
import com.learn2hire.app.ui.tutor.TutorViewModel
@Composable
fun Learn2HireNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val tutorViewModel: TutorViewModel = viewModel()
    val interviewViewModel: InterviewViewModel = viewModel()
    val studyViewModel: StudyViewModel = viewModel()
    val quizViewModel: QuizViewModel = viewModel()
    val aiPracticeViewModel: AiPracticeViewModel = viewModel()
    val quizState by quizViewModel.uiState.collectAsState()

    val startDestination =
        if (authState.isAuthenticated) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
    LaunchedEffect(authState.isAuthenticated) {

        val currentRoute =
            navController.currentDestination?.route

        if (authState.isAuthenticated) {

            if (
                currentRoute == Screen.Login.route ||
                currentRoute == Screen.Register.route
            ) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }

                    launchSingleTop = true
                }
            }

        } else {

            if (
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Register.route
            ) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) {
                        inclusive = true
                    }

                    launchSingleTop = true
                }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination

    ) {
        composable(Screen.Login.route) {

            LoginScreen(
                state = authState,

                onEmailChange =
                    authViewModel::updateEmail,

                onPasswordChange =
                    authViewModel::updatePassword,

                onLoginClick = {
                    authViewModel.login()
                },

                onRegisterClick = {
                    authViewModel.clearForm()
                    navController.navigate(
                        Screen.Register.route
                    )
                },

                onGoogleTokenReceived = { token ->
                    authViewModel.signInWithGoogleToken(token)
                },

                onGoogleError = { message ->
                    authViewModel.setGoogleError(message)
                }
            )
        }

        composable(Screen.Register.route) {

            RegisterScreen(
                state = authState,

                onEmailChange = authViewModel::updateEmail,

                onPasswordChange = authViewModel::updatePassword,

                onConfirmPasswordChange =
                    authViewModel::updateConfirmPassword,

                onRegisterClick = {
                    authViewModel.register()
                },

                onLoginClick = {
                    authViewModel.clearForm()
                    navController.popBackStack()
                }
            )
        }

        // HOME
        composable(Screen.Home.route) {

            HomeScreen(
                onStudyClick = {
                    navController.navigate(Screen.Study.route)
                },
                onQuizClick = { topicId ->
                    navController.navigate(Screen.Quiz.createRoute(topicId))
                },
                onPlannerClick = {
                    navController.navigate(Screen.Planner.route)
                },
                onInterviewClick = {
                    navController.navigate(Screen.Interview.route)
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
                },
                onTopicClick = { topicId ->
                    navController.navigate(Screen.TopicDetail.createRoute(topicId))
                },
                onTutorClick = {
                    navController.navigate(
                        Screen.Tutor.route
                    )
                },
                onTopicTutorClick = { topicId ->
                    navController.navigate(
                        Screen.TopicTutor.createRoute(topicId)
                    )
                },
                onAiPracticeClick = { topicId, difficulty ->
                    navController.navigate(
                        Screen.AiPractice.createRoute(topicId, difficulty)
                    )
                }
            )
        }

        // PLANNER
        composable(Screen.Planner.route) {

            PlannerScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // STUDY
        composable(Screen.Study.route) {

            StudyScreen(
                studyViewModel = studyViewModel,
                onSubjectClick = { subjectId ->

                    navController.navigate(
                        Screen.SubjectDetail.createRoute(
                            subjectId
                        )
                    )
                }
            )
        }

        // SUBJECT DETAIL
        composable(Screen.SubjectDetail.route) { backStackEntry ->

            val subjectId =
                backStackEntry.arguments
                    ?.getString("subjectId")
                    ?: return@composable

            SubjectDetailScreen(
                subjectId = subjectId,
                studyViewModel = studyViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onTopicClick = { topicId ->

                    navController.navigate(
                        Screen.TopicDetail.createRoute(
                            topicId
                        )
                    )
                }
            )
        }

        // TOPIC DETAIL
        composable(Screen.TopicDetail.route) { backStackEntry ->

            val topicId =
                backStackEntry.arguments
                    ?.getString("topicId")
                    ?: return@composable

            TopicDetailScreen(
                topicId = topicId,
                studyViewModel = studyViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onTutorClick = {
                    navController.navigate(
                        Screen.TopicTutor.createRoute(topicId)
                    )
                },

                onQuizClick = {

                    navController.navigate(
                        Screen.Quiz.createRoute(
                            topicId
                        )
                    )
                },

                onAiPracticeClick = {
                    navController.navigate(
                        Screen.AiPractice.createRoute(
                            topicId,
                            "Standard"
                        )
                    )
                }
            )
        }

        composable(Screen.Tutor.route) {

            TutorScreen(
                topicId = null,
                tutorViewModel = tutorViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.TopicTutor.route) { backStackEntry ->

            val topicId =
                backStackEntry.arguments
                    ?.getString("topicId")

            TutorScreen(
                topicId = topicId,
                tutorViewModel = tutorViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // AI PRACTICE
        composable(Screen.AiPractice.route) { backStackEntry ->

            val topicId =
                backStackEntry.arguments
                    ?.getString("topicId")
                    ?: return@composable

            val difficulty =
                backStackEntry.arguments
                    ?.getString("difficulty")
                    ?: "Standard"

            AiPracticeScreen(
                topicId = topicId,
                difficulty = difficulty,
                aiPracticeViewModel = aiPracticeViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onAskAi = { practiceTopicId ->
                    navController.navigate(
                        Screen.TopicTutor.createRoute(practiceTopicId)
                    )
                }
            )
        }

        // QUIZ
        composable(Screen.Quiz.route) { backStackEntry ->

            val topicId =
                backStackEntry.arguments
                    ?.getString("topicId")
                    ?: return@composable

            QuizScreen(
                topicId = topicId,
                quizViewModel = quizViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onQuizComplete = { score, total ->

                    navController.navigate(
                        Screen.QuizResult.createRoute(
                            topicId = topicId,
                            score = score,
                            total = total
                        )
                    ) {

                        // Remove completed QuizScreen
                        // from the back stack.
                        popUpTo(Screen.Quiz.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // QUIZ RESULT
        composable(Screen.QuizResult.route) { backStackEntry ->

            val topicId =
                backStackEntry.arguments
                    ?.getString("topicId")
                    ?: return@composable

            val score =
                backStackEntry.arguments
                    ?.getString("score")
                    ?.toIntOrNull()
                    ?: 0

            val total =
                backStackEntry.arguments
                    ?.getString("total")
                    ?.toIntOrNull()
                    ?: 0

            QuizResultScreen(
                score = score,
                total = total,
                quizState = quizState,

                onTryAgain = {

                    quizViewModel.resetQuiz()

                    navController.navigate(
                        Screen.Quiz.createRoute(
                            topicId
                        )
                    ) {

                        // Remove old result screen.
                        popUpTo(Screen.QuizResult.route) {
                            inclusive = true
                        }
                    }
                },

                onBackToTopic = {

                    // The completed QuizScreen was already
                    // removed, so the previous screen is TopicDetail.
                    navController.popBackStack()
                }
            )
        }

        // INTERVIEW SETUP
        composable(Screen.Interview.route) {

            InterviewScreen(
                interviewViewModel =
                    interviewViewModel,

                onStartInterview = {

                    navController.navigate(
                        Screen.InterviewPractice.route
                    )
                }
            )
        }

        // INTERVIEW PRACTICE
        composable(
            Screen.InterviewPractice.route
        ) {

            InterviewPracticeScreen(
                interviewViewModel =
                    interviewViewModel,

                onBack = {
                    navController.popBackStack()
                },

                onFinished = {

                    navController.navigate(
                        Screen.InterviewResult.route
                    ) {

                        popUpTo(
                            Screen.InterviewPractice.route
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // INTERVIEW RESULT
        composable(
            Screen.InterviewResult.route
        ) {

            InterviewResultScreen(
                interviewViewModel =
                    interviewViewModel,

                onPracticeAgain = {

                    interviewViewModel
                        .startInterview()

                    navController.navigate(
                        Screen.InterviewPractice.route
                    ) {

                        popUpTo(
                            Screen.InterviewResult.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onDone = {

                    interviewViewModel
                        .resetInterview()

                    navController.popBackStack(
                        Screen.Interview.route,
                        inclusive = false
                    )
                }
            )
        }

        // PROFILE
        composable(Screen.Profile.route) {

            val profileViewModel: ProfileViewModel =
                viewModel()

            val profileState by
            profileViewModel.uiState.collectAsState()

            ProfileScreen(
                profileState = profileState,

                themeMode = themeMode,

                onThemeModeChange =
                    onThemeModeChange,

                onNameChange =
                    profileViewModel::updateName,

                onCourseChange =
                    profileViewModel::updateCourse,

                onSemesterChange =
                    profileViewModel::updateSemester,

                onEditClick =
                    profileViewModel::startEditing,

                onCancelEdit =
                    profileViewModel::cancelEditing,

                onSaveProfile =
                    profileViewModel::saveProfile,

                onSignOut = {
                    authViewModel.logout()
                }
            )
        }

        // PROGRESS
        composable(Screen.Progress.route) {
            ProgressScreen(
                onTopicClick = { topicId ->
                    navController.navigate(
                        Screen.TopicDetail.createRoute(topicId)
                    )
                },
                onTopicTutorClick = { topicId ->
                    navController.navigate(
                        Screen.TopicTutor.createRoute(topicId)
                    )
                },
                onAiPracticeClick = { topicId, difficulty ->
                    navController.navigate(
                        Screen.AiPractice.createRoute(topicId, difficulty)
                    )
                }
            )
        }
    }
}