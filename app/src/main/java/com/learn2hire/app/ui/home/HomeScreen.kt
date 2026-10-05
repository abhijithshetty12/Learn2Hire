package com.learn2hire.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

private val HomeBlue = Color(0xFF2563EB)
private val HomeIndigo = Color(0xFF4F46E5)
private val HomeViolet = Color(0xFF7C3AED)
private val HomeFuchsia = Color(0xFFC026D3)
private val HomeAmber = Color(0xFFF59E0B)
private val HomeOrange = Color(0xFFFFA52A)
private val HomeYellow = Color(0xFFFACC15)
private val HomeRose = Color(0xFFE11D48)
private val HomeRed = Color(0xFFEF4444)

@Composable
fun HomeScreen(
    onStudyClick: () -> Unit,
    onQuizClick: (String) -> Unit,
    onPlannerClick: () -> Unit,
    onInterviewClick: () -> Unit,
    onProgressClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onTutorClick: () -> Unit = {},
    onTopicTutorClick: (String) -> Unit = {},
    onAiPracticeClick: (String, String) -> Unit = { _, _ -> },
    homeViewModel: HomeViewModel = viewModel()
) {
    val state by homeViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 132.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = greeting(state.userName),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 34.sp,
                        lineHeight = 39.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.7).sp,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Ready to make progress?",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Text(
                    text = "${state.overallProgress}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.10f))
                        .clickable(onClick = onProgressClick)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }

        state.errorMessage?.let { message ->
            item {
                GlassCard {
                    Text(
                        text = "Some dashboard data could not be loaded.",
                        fontWeight = FontWeight.SemiBold,
                        color = colors.error
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        item {
            OverallLearningCard(
                state = state,
                onClick = onProgressClick
            )
        }

        if (state.focusTopicId != null) {
            item {
                SectionTitle(
                    title = "Your Study Insight",
                    subtitle = "Personalized from your quiz performance"
                )
            }

            item {
                StudyInsightCard(
                    state = state,
                    onReviewClick = {
                        state.focusTopicId?.let(onTopicClick)
                    },
                    onTutorClick = {
                        state.focusTopicId?.let(onTopicTutorClick)
                    },
                    onPracticeClick = {
                        state.focusTopicId?.let { topicId ->
                            onAiPracticeClick(
                                topicId,
                                difficultyForLevel(state.focusLevel)
                            )
                        }
                    }
                )
            }
        }

        item {
            SectionTitle(
                title = "Continue Learning",
                subtitle = "Pick up from your latest unfinished topic"
            )
        }

        item {
            ContinueLearningCard(
                state = state,
                onStudyClick = onStudyClick,
                onTopicClick = onTopicClick
            )
        }

        item {
            SectionTitle(
                title = "Today's Focus",
                subtitle = "Your highest-priority unfinished planner tasks"
            )
        }

        if (state.plannerTasks.isEmpty()) {
            item {
                EmptyFocusCard(onPlannerClick)
            }
        } else {
            items(
                items = state.plannerTasks,
                key = { it.id }
            ) { task ->
                PlannerFocusCard(
                    task = task,
                    onClick = onPlannerClick
                )
            }
        }

        item {
            SectionTitle(
                title = "Recommended for You",
                subtitle = "Prioritized from your learning activity"
            )
        }

        items(
            items = state.recommendations,
            key = { it.id }
        ) { recommendation ->
            RecommendationGlassCard(
                recommendation = recommendation,
                onClick = {
                    when (recommendation.action) {
                        HomeRecommendationAction.REVIEW_TOPIC ->
                            recommendation.topicId?.let(onTopicClick) ?: onStudyClick()

                        HomeRecommendationAction.TAKE_QUIZ ->
                            recommendation.topicId?.let(onQuizClick) ?: onStudyClick()

                        HomeRecommendationAction.ASK_AI ->
                            recommendation.topicId?.let(onTopicTutorClick) ?: onTutorClick()

                        HomeRecommendationAction.INTERVIEW ->
                            onInterviewClick()

                        HomeRecommendationAction.PLANNER ->
                            onPlannerClick()

                        HomeRecommendationAction.AI_PRACTICE ->
                            recommendation.topicId?.let { topicId ->
                                val level = if (topicId == state.focusTopicId) state.focusLevel else ""
                                onAiPracticeClick(topicId, difficultyForLevel(level))
                            } ?: onStudyClick()

                        HomeRecommendationAction.STUDY ->
                            onStudyClick()
                    }
                }
            )
        }

        item {
            SectionTitle(
                title = "Recent Performance",
                subtitle = "Your latest quiz and interview results"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PerformanceCard(
                    title = "Quiz",
                    value = state.latestQuizAccuracy?.let { "$it%" } ?: "—",
                    subtitle = if (state.latestQuizAccuracy == null) "No attempts yet" else "Latest accuracy",
                    icon = Icons.Outlined.Psychology,
                    startColor = HomeBlue,
                    endColor = HomeIndigo,
                    onClick = {
                        state.continueTopicId?.let(onQuizClick) ?: onStudyClick()
                    },
                    modifier = Modifier.weight(1f)
                )

                PerformanceCard(
                    title = "Interview",
                    value = state.latestInterviewCoverage?.let { "$it%" } ?: "—",
                    subtitle = if (state.latestInterviewCoverage == null) "No sessions yet" else "Latest coverage",
                    icon = Icons.Outlined.WorkOutline,
                    startColor = HomeRed,
                    endColor = HomeRose,
                    onClick = onInterviewClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle(
                title = "Quick Actions",
                subtitle = "Jump back into your preparation"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientQuickAction(
                        title = "AI Tutor",
                        subtitle = "Ask anything",
                        icon = Icons.Outlined.AutoAwesome,
                        startColor = HomeViolet,
                        endColor = HomeFuchsia,
                        onClick = onTutorClick,
                        modifier = Modifier.weight(1f)
                    )

                    GradientQuickAction(
                        title = "Study",
                        subtitle = "Learn a topic",
                        icon = Icons.Outlined.MenuBook,
                        startColor = colors.primary,
                        endColor = colors.secondary,
                        onClick = onStudyClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientQuickAction(
                        title = "Planner",
                        subtitle = "Plan your study",
                        icon = Icons.Outlined.CalendarMonth,
                        startColor = HomeAmber,
                        endColor = HomeYellow,
                        onClick = onPlannerClick,
                        modifier = Modifier.weight(1f)
                    )

                    GradientQuickAction(
                        title = "Interview",
                        subtitle = "Practice now",
                        icon = Icons.Outlined.WorkOutline,
                        startColor = HomeRed,
                        endColor = HomeRose,
                        onClick = onInterviewClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }
}

private fun greeting(name: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val dayPart = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Welcome back"
    }

    val firstName = name.trim().substringBefore(" ").takeIf { it.isNotBlank() }
    return if (firstName == null) "$dayPart 👋" else "$dayPart, $firstName 👋"
}

@Composable
private fun StudyInsightCard(
    state: HomeUiState,
    onReviewClick: () -> Unit,
    onTutorClick: () -> Unit,
    onPracticeClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    val isWeak = state.focusLevel == "Needs Practice"
    val isDeveloping = state.focusLevel == "Developing"
    val isStrong = state.focusLevel == "Strong"
    val accent = when {
        isWeak -> HomeOrange
        isDeveloping -> HomeYellow
        isStrong -> colors.primary
        else -> colors.primary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.16f),
                        colors.surface.copy(alpha = 0.94f),
                        colors.surface.copy(alpha = 0.82f)
                    )
                )
            )
            .border(1.dp, accent.copy(alpha = 0.30f), shape)
            .padding(19.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = state.focusSubjectName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = state.focusTopicName,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Text(
                    text = when (state.focusLevel) {
                        "Needs Practice" -> "Focus here next. Strengthen the basics with guided practice."
                        "Developing" -> "You're progressing. Another focused practice round can help."
                        "Strong" -> "You're performing strongly. Push further with a challenge."
                        else -> "Build your understanding, then assess yourself with a quiz."
                    },
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.14f))
                    .border(1.dp, accent.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = state.focusAverageScore?.let { "$it%" } ?: "—",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }
        }

        Text(
            text = "${state.focusLevel} • ${state.focusAttempts} " +
                    if (state.focusAttempts == 1) "quiz attempt" else "quiz attempts",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurfaceVariant
        )

        if (isWeak || isDeveloping) {
            InsightAction(
                text = "Practice with AI",
                onClick = onPracticeClick,
                modifier = Modifier.fillMaxWidth(),
                primary = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InsightAction("Review Topic", onReviewClick, Modifier.weight(1f))
                InsightAction("Ask AI", onTutorClick, Modifier.weight(1f))
            }
        } else if (isStrong) {
            InsightAction(
                text = "Ask AI",
                onClick = onTutorClick,
                modifier = Modifier.fillMaxWidth(),
                primary = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InsightAction("Review Topic", onReviewClick, Modifier.weight(1f))
                InsightAction("Challenge Practice", onPracticeClick, Modifier.weight(1f))
            }
        } else {
            InsightAction(
                text = "Review Topic",
                onClick = onReviewClick,
                modifier = Modifier.fillMaxWidth(),
                primary = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InsightAction("Ask AI", onTutorClick, Modifier.weight(1f))
                InsightAction("Practice with AI", onPracticeClick, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InsightAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(shape)
            .background(
                if (primary) {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF22B85A), Color(0xFF9BE21B))
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            colors.primary.copy(alpha = 0.12f),
                            colors.secondary.copy(alpha = 0.10f)
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (primary) Color.White.copy(alpha = 0.25f)
                else colors.primary.copy(alpha = 0.16f),
                shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (primary) Color(0xFF101512) else colors.onSurface
        )
    }
}

@Composable
private fun OverallLearningCard(
    state: HomeUiState,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.primary.copy(alpha = 0.22f), shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface.copy(alpha = 0.82f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Overall Learning",
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "${state.overallProgress}%",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            LinearProgressIndicator(
                progress = { state.overallProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape),
                color = colors.primary,
                trackColor = colors.surfaceVariant.copy(alpha = 0.70f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
            )

            Text(
                text = "${state.completedTopics} of ${state.totalTopics} topics completed",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContinueLearningCard(
    state: HomeUiState,
    onStudyClick: () -> Unit,
    onTopicClick: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)
    val topicId = state.continueTopicId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline.copy(alpha = 0.20f), shape)
            .clickable {
                if (topicId != null) onTopicClick(topicId) else onStudyClick()
            },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface.copy(alpha = 0.82f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (topicId == null) "All current topics completed" else state.continueSubjectName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary
            )

            Text(
                text = if (topicId == null) "Explore Study" else state.continueTopicName,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )

            if (topicId != null) {
                LinearProgressIndicator(
                    progress = { state.continueTopicProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = if (state.continueTopicProgress == 0) {
                        "Ready to start"
                    } else {
                        "${state.continueTopicProgress}% topic progress"
                    },
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PlannerFocusCard(
    task: HomePlannerTaskUi,
    onClick: () -> Unit
) {
    GlassCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${task.estimatedMinutes} min • ${deadlineText(task.daysLeft)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyFocusCard(onPlannerClick: () -> Unit) {
    GlassCard(onClick = onPlannerClick) {
        Text(
            text = "No unfinished planner tasks",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Open Planner to create your next study focus.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun deadlineText(daysLeft: Int): String = when {
    daysLeft <= 0 -> "Due today"
    daysLeft == 1 -> "1 day left"
    else -> "$daysLeft days left"
}

@Composable
private fun PerformanceCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GradientQuickAction(
        title = "$title  $value",
        subtitle = subtitle,
        icon = icon,
        startColor = startColor,
        endColor = endColor,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun GradientQuickAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .height(96.dp)
            .clip(shape)
            .background(Brush.linearGradient(listOf(startColor, endColor)))
            .border(1.dp, Color.White.copy(alpha = 0.20f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color.Black.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.80f)
                )
            }
        }
    }
}

@Composable
private fun RecommendationGlassCard(
    recommendation: HomeRecommendationUi,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline.copy(alpha = 0.20f), shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface.copy(alpha = 0.82f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(HomeAmber, HomeYellow)))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.20f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = recommendation.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Text(
                    text = recommendation.text,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = recommendation.actionLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun GlassCard(
    onClick: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)
    val clickableModifier =
        if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface.copy(alpha = 0.80f))
            .border(1.dp, colors.outline.copy(alpha = 0.18f), shape)
            .then(clickableModifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.25).sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun difficultyForLevel(level: String): String =
    when (level) {
        "Needs Practice" -> "Foundation"
        "Developing" -> "Intermediate"
        "Strong" -> "Challenge"
        else -> "Standard"
    }
