package com.learn2hire.app.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AiPracticeScreen(
    topicId: String,
    difficulty: String,
    onBack: () -> Unit,
    onAskAi: (String) -> Unit,
    aiPracticeViewModel: AiPracticeViewModel = viewModel()
) {
    val state by aiPracticeViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(topicId, difficulty) {
        aiPracticeViewModel.initialize(topicId, difficulty)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.onBackground
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Practice",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = state.topicName,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            DifficultyBadge(state.difficulty)
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Preparing adaptive practice…",
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            state.errorMessage != null -> {
                GlassPanel {
                    Text(
                        text = "Practice unavailable",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = state.errorMessage.orEmpty(),
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant
                    )
                    GradientButton(
                        text = "Try Again",
                        icon = Icons.Outlined.Refresh,
                        onClick = aiPracticeViewModel::retry
                    )
                }
            }

            state.isComplete -> {
                ResultContent(
                    state = state,
                    onPracticeAgain = aiPracticeViewModel::retry,
                    onAskAi = { onAskAi(state.topicId) }
                )
            }

            else -> {
                val question = state.currentQuestion
                if (question != null) {
                    QuestionProgress(
                        current = state.currentQuestionIndex + 1,
                        total = state.questions.size,
                        difficulty = state.difficulty
                    )

                    GlassPanel(accented = true) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = colors.primary
                            )
                            Text(
                                text = "Adaptive question",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }

                        Text(
                            text = question.question,
                            fontSize = 20.sp,
                            lineHeight = 27.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        question.options.forEachIndexed { index, option ->
                            val selected =
                                state.selectedAnswers[question.id] == index

                            OptionCard(
                                letter = ('A'.code + index).toChar().toString(),
                                text = option,
                                selected = selected,
                                onClick = {
                                    aiPracticeViewModel.selectAnswer(index)
                                }
                            )
                        }
                    }

                    GradientButton(
                        text = if (
                            state.currentQuestionIndex ==
                            state.questions.lastIndex
                        ) "Finish Practice" else "Next Question",
                        icon = Icons.Outlined.AutoAwesome,
                        enabled = state.selectedAnswers[question.id] != null,
                        onClick = aiPracticeViewModel::next
                    )
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ResultContent(
    state: AiPracticeUiState,
    onPracticeAgain: () -> Unit,
    onAskAi: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    GlassPanel(accented = true) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF22B85A), Color(0xFF9BE21B))
                        ),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    null,
                    Modifier.size(23.dp),
                    tint = Color(0xFF101512)
                )
            }

            Column {
                Text(
                    text = "Practice Complete",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Text(
                    text = "Adaptive training result",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "${state.percentage}%",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = "${state.score} of ${state.questions.size} correct",
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
            }

            DifficultyBadge(state.difficulty)
        }

        Text(
            text = when {
                state.percentage >= 80 -> "Strong practice round. Try another challenge or ask AI to go deeper."
                state.percentage >= 60 -> "Good progress. Another focused round can strengthen this topic."
                else -> "Review the explanations below, then practice this topic again."
            },
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = colors.onSurfaceVariant
        )
    }

    Text(
        text = "Answer Review",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = colors.onBackground
    )

    state.questions.forEachIndexed { index, question ->
        val selected = state.selectedAnswers[question.id]
        val correct = selected == question.correctAnswerIndex

        GlassPanel(accented = correct) {
            Text(
                text = "${index + 1}. ${question.question}",
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )

            Text(
                text = if (correct) {
                    "Correct"
                } else {
                    "Your answer: ${
                        selected?.let { question.options.getOrNull(it) }
                            ?: "No answer"
                    }"
                },
                fontSize = 12.sp,
                color = if (correct) colors.primary else colors.error
            )

            if (!correct) {
                Text(
                    text = "Correct answer: ${question.options[question.correctAnswerIndex]}",
                    fontSize = 12.sp,
                    color = colors.onSurface
                )
            }

            Text(
                text = question.explanation,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = colors.onSurfaceVariant
            )
        }
    }

    GradientButton(
        text = "Practice Again",
        icon = Icons.Outlined.Refresh,
        onClick = onPracticeAgain
    )

    SecondaryButton(
        text = "Ask AI About This Topic",
        onClick = onAskAi
    )
}

@Composable
private fun QuestionProgress(
    current: Int,
    total: Int,
    difficulty: String
) {
    val colors = MaterialTheme.colorScheme
    val progress = if (total <= 0) 0f else current.toFloat() / total.toFloat()

    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question $current of $total",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
            color = colors.primary,
            trackColor = colors.surfaceVariant.copy(alpha = 0.55f)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Psychology,
                null,
                Modifier.size(15.dp),
                tint = colors.onSurfaceVariant
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = "$difficulty adaptive practice",
                fontSize = 11.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val colors = MaterialTheme.colorScheme
    val accent = when (difficulty) {
        "Foundation" -> Color(0xFF22B85A)
        "Intermediate" -> Color(0xFFF59E0B)
        "Challenge" -> Color(0xFF7C3AED)
        else -> colors.primary
    }

    Text(
        text = difficulty,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = accent,
        modifier = Modifier
            .background(accent.copy(alpha = 0.11f), CircleShape)
            .border(1.dp, accent.copy(alpha = 0.18f), CircleShape)
            .padding(horizontal = 11.dp, vertical = 7.dp)
    )
}

@Composable
private fun OptionCard(
    letter: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) colors.primary.copy(alpha = 0.12f)
                else colors.surfaceVariant.copy(alpha = 0.38f),
                shape
            )
            .border(
                1.dp,
                if (selected) colors.primary.copy(alpha = 0.58f)
                else colors.outline.copy(alpha = 0.16f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    if (selected) colors.primary
                    else colors.surface.copy(alpha = 0.80f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) colors.onPrimary else colors.onSurface
            )
        }

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            lineHeight = 19.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = colors.onSurface
        )
    }
}

@Composable
private fun GlassPanel(
    accented: Boolean = false,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (accented) {
                    Brush.linearGradient(
                        listOf(
                            colors.primary.copy(alpha = 0.10f),
                            colors.surface.copy(alpha = 0.90f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            colors.surface.copy(alpha = 0.84f),
                            colors.surface.copy(alpha = 0.84f)
                        )
                    )
                },
                shape
            )
            .border(
                1.dp,
                if (accented) colors.primary.copy(alpha = 0.20f)
                else colors.outline.copy(alpha = 0.18f),
                shape
            )
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun GradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(17.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF22B85A).copy(alpha = if (enabled) 1f else 0.35f),
                        Color(0xFF9BE21B).copy(alpha = if (enabled) 1f else 0.35f)
                    )
                ),
                shape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color(0xFF101512)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF101512)
        )
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(17.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colors.surfaceVariant.copy(alpha = 0.55f), shape)
            .border(1.dp, colors.outline.copy(alpha = 0.20f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface
        )
    }
}
