package com.learn2hire.app.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    onTopicClick: (String) -> Unit,
    onTopicTutorClick: (String) -> Unit,
    onAiPracticeClick: (String, String) -> Unit,
    progressViewModel: ProgressViewModel = viewModel()
) {
    val state by progressViewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 132.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "Progress",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Your learning, backed by real activity.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        state.errorMessage?.let { message ->
            item {
                GlassPanel {
                    Text(
                        text = "Some progress could not be loaded.",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            OverallProgressCard(state)
        }

        item {
            SectionTitle("Learning")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.CheckCircle,
                    title = "Completed",
                    value = "${state.completedTopics}/${state.totalTopics}",
                    subtitle = "Topics",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.Insights,
                    title = "Study",
                    value = "${state.studyProgress}%",
                    subtitle = "Progress",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle("Quiz Performance")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.Quiz,
                    title = "Average",
                    value = "${state.averageQuizAccuracy}%",
                    subtitle = "Accuracy",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "Best",
                    value = "${state.bestQuizAccuracy}%",
                    subtitle = "${state.quizzesCompleted} attempts",
                    modifier = Modifier.weight(1f)
                )
            }
        }



        item {
            SectionTitle("AI Practice")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.AutoAwesome,
                    title = "Average",
                    value = "${state.averageAiPracticeAccuracy}%",
                    subtitle = "${state.aiPracticeSessions} sessions",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "Best",
                    value = "${state.bestAiPracticeAccuracy}%",
                    subtitle = "Practice accuracy",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            SectionTitle("Study Planner")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.CheckCircle,
                    title = "Completed",
                    value = "${state.completedPlannerTasks}/${state.plannerTasks}",
                    subtitle = "Planner tasks",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.Schedule,
                    title = "Plans",
                    value = state.plannerSessions.toString(),
                    subtitle = "${state.plannerCompletion}% task completion",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle("Interview Practice")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.WorkOutline,
                    title = "Average",
                    value = "${state.averageInterviewCoverage}%",
                    subtitle = "${state.interviewSessions} sessions",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "Best",
                    value = "${state.bestInterviewCoverage}%",
                    subtitle = "Coverage",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle("Topic Performance")
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Quiz assessment with AI practice improvement signals",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(
            items = state.topicPerformance,
            key = { it.topicId }
        ) { topic ->
            TopicPerformanceCard(
                topic = topic,
                onReviewClick = { onTopicClick(topic.topicId) },
                onTutorClick = { onTopicTutorClick(topic.topicId) },
                onPracticeClick = {
                    onAiPracticeClick(
                        topic.topicId,
                        difficultyForTopicLevel(topic.level)
                    )
                }
            )
        }

        item {
            SectionTitle("Subjects")
        }

        items(
            items = state.subjectProgress,
            key = { it.subjectId }
        ) { subject ->
            SubjectProgressCard(subject)
        }

        item {
            SectionTitle("Recent Activity")
        }

        if (state.recentActivity.isEmpty()) {
            item {
                GlassPanel {
                    Text(
                        text = "No activity yet",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Complete a topic, quiz, AI practice, planner task, or interview and it will appear here.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(
                items = state.recentActivity,
                key = { it.id }
            ) { activity ->
                ActivityCard(activity)
            }
        }
    }
}

@Composable
private fun OverallProgressCard(state: ProgressUiState) {
    val shape = RoundedCornerShape(28.dp)
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        primary.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    )
                )
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.24f),
                shape
            )
            .padding(22.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Overall learning",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${state.overallProgress}%",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(primary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Insights,
                        contentDescription = null,
                        tint = primary
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = { state.overallProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "${state.completedTopics} of ${state.totalTopics} topics completed",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.82f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                shape
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = value,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TopicPerformanceCard(
    topic: TopicPerformanceUi,
    onReviewClick: () -> Unit,
    onTutorClick: () -> Unit,
    onPracticeClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)
    val isWeak = topic.level == "Needs Practice"
    val isDeveloping = topic.level == "Developing"
    val isStrong = topic.level == "Strong"

    val accent = when {
        isWeak -> Color(0xFFFFA52A)
        isDeveloping -> Color(0xFFFFD21F)
        isStrong -> colors.primary
        else -> colors.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.09f),
                        colors.surface.copy(alpha = 0.90f)
                    )
                )
            )
            .border(1.dp, accent.copy(alpha = 0.22f), shape)
            .padding(18.dp),
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
                    text = topic.subjectName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )

                Text(
                    text = topic.topicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Text(
                    text = if (topic.attempts == 0) {
                        "Not assessed by quiz yet"
                    } else {
                        "${topic.attempts} ${if (topic.attempts == 1) "quiz attempt" else "quiz attempts"} • Latest ${topic.latestScore}%"
                    },
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = topic.averageScore?.let { "$it%" } ?: "—",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.14f))
                        .border(
                            1.dp,
                            accent.copy(alpha = 0.24f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = topic.level,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "AI Practice",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )

            if (topic.practiceSessions > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    PracticeMetricChip(
                        label = "Latest",
                        value = "${topic.practiceLatestScore}%"
                    )
                    PracticeMetricChip(
                        label = "Avg",
                        value = "${topic.practiceAverageScore}%"
                    )
                    PracticeMetricChip(
                        label = "Sessions",
                        value = topic.practiceSessions.toString()
                    )
                }

                if (topic.practiceTrend.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.primary.copy(alpha = 0.10f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (topic.practiceTrend == "Improving") {
                                "↑ Improving"
                            } else {
                                topic.practiceTrend
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primary
                        )
                    }
                }
            } else {
                Text(
                    text = "No AI practice sessions yet",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        if (isWeak || isDeveloping) {
            TopicActionButton(
                text = "Practice with AI",
                icon = Icons.Outlined.AutoAwesome,
                onClick = onPracticeClick,
                modifier = Modifier.fillMaxWidth(),
                primary = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TopicActionButton(
                    text = "Review",
                    icon = Icons.Outlined.MenuBook,
                    onClick = onReviewClick,
                    modifier = Modifier.weight(1f)
                )
                TopicActionButton(
                    text = "Ask AI",
                    icon = Icons.Outlined.AutoAwesome,
                    onClick = onTutorClick,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TopicActionButton(
                    text = "Review",
                    icon = Icons.Outlined.MenuBook,
                    onClick = onReviewClick,
                    modifier = Modifier.weight(1f)
                )
                TopicActionButton(
                    text = "Ask AI",
                    icon = Icons.Outlined.AutoAwesome,
                    onClick = onTutorClick,
                    modifier = Modifier.weight(1f),
                    primary = isStrong
                )
            }

            TopicActionButton(
                text = if (isStrong) "Challenge Practice" else "Practice with AI",
                icon = Icons.Outlined.Quiz,
                onClick = onPracticeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PracticeMetricChip(
    label: String,
    value: String
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.55f))
            .border(
                1.dp,
                colors.outline.copy(alpha = 0.14f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 9.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )
    }
}

@Composable
private fun TopicActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(15.dp)

    Row(
        modifier = modifier
            .height(46.dp)
            .clip(shape)
            .background(
                if (primary) {
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF22B85A),
                            Color(0xFF9BE21B)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            colors.primary.copy(alpha = 0.13f),
                            colors.secondary.copy(alpha = 0.11f)
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (primary) Color.White.copy(alpha = 0.28f)
                else colors.primary.copy(alpha = 0.16f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = if (primary) Color(0xFF101512) else colors.primary
        )

        Spacer(Modifier.size(7.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (primary) Color(0xFF101512) else colors.onSurface
        )
    }
}

@Composable
private fun SubjectProgressCard(subject: SubjectProgressUi) {
    GlassPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.subjectName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "${subject.completedTopics}/${subject.totalTopics} topics",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${subject.progress}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { subject.progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun ActivityCard(activity: RecentActivityUi) {
    val icon = when (activity.type) {
        ActivityType.QUIZ -> Icons.Outlined.Quiz
        ActivityType.PLANNER -> Icons.Outlined.Schedule
        ActivityType.INTERVIEW -> Icons.Outlined.WorkOutline
        ActivityType.AI_PRACTICE -> Icons.Outlined.AutoAwesome
        ActivityType.STUDY -> Icons.Outlined.CheckCircle
    }

    GlassPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(21.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activity.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (activity.timestamp > 0L) {
                Text(
                    text = formatDate(activity.timestamp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GlassPanel(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.78f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                shape
            )
            .padding(17.dp),
        content = content
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM", Locale.getDefault())
        .format(Date(timestamp))
}

private fun difficultyForTopicLevel(level: String): String =
    when (level) {
        "Needs Practice" -> "Foundation"
        "Developing" -> "Intermediate"
        "Strong" -> "Challenge"
        else -> "Standard"
    }
