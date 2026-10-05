package com.learn2hire.app.ui.interview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme

@Composable
fun InterviewPracticeScreen(
    interviewViewModel: InterviewViewModel,
    onBack: () -> Unit,
    onFinished: () -> Unit
) {

    val state by interviewViewModel.uiState.collectAsState()
    val aiFeedback by interviewViewModel.aiFeedback.collectAsState()
    val isAiFeedbackLoading by interviewViewModel.isAiFeedbackLoading.collectAsState()

    val question = state.currentQuestion

    BackHandler {
        onBack()
    }

    if (question == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "No interview questions available.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onBack
            ) {
                Text("Go Back")
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 96.dp
        ),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored
                                .Outlined
                                .ArrowBack,
                        contentDescription =
                            "Exit interview"
                    )
                }

                Column {

                    Text(
                        text = "Interview Practice",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text =
                            "${state.selectedRole} • ${state.selectedType}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {

            val progress =
                (state.currentQuestionIndex + 1).toFloat() / state.totalQuestions

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${state.currentQuestionIndex + 1} of ${state.totalQuestions}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            )
        }

        item {

            InterviewGlassPanel(accented = true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Interview question",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = question.question,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {

            Text(
                text = "Your answer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = state.answer,
                onValueChange =
                    interviewViewModel::updateAnswer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                placeholder = {
                    Text(
                        "Type your answer here..."
                    )
                },
                enabled =
                    !state.feedbackVisible
            )
        }

        if (!state.feedbackVisible) {

            item {
                InterviewPrimaryButton(
                    text = "Submit Answer",
                    enabled = state.answer.isNotBlank(),
                    onClick = interviewViewModel::submitAnswer
                )
            }
        }

        if (state.feedbackVisible) {

            item {
                InterviewGlassPanel(accented = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Answer Evaluation",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Key-point assessment",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        EvaluationBadge(
                            matched = state.matchedKeyPoints.size,
                            total = state.matchedKeyPoints.size + state.missedKeyPoints.size
                        )
                    }

                    if (state.matchedKeyPoints.isNotEmpty()) {
                        FeedbackSectionTitle(
                            icon = Icons.Outlined.CheckCircle,
                            text = "Covered well",
                            positive = true
                        )
                        state.matchedKeyPoints.forEach { point ->
                            FeedbackPoint(point = point, positive = true)
                        }
                    }

                    if (state.missedKeyPoints.isNotEmpty()) {
                        FeedbackSectionTitle(
                            icon = Icons.Outlined.Info,
                            text = "Could also mention",
                            positive = false
                        )
                        state.missedKeyPoints.forEach { point ->
                            FeedbackPoint(point = point, positive = false)
                        }
                    }
                }
            }

            item {
                InterviewGlassPanel(aiCoach = true) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF7C3AED), Color(0xFFC026D3))
                                    ),
                                    RoundedCornerShape(13.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.AutoAwesome,
                                null,
                                Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "AI Interview Coach",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Qualitative coaching • score unchanged",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isAiFeedbackLoading) {
                        Text(
                            text = "Preparing personalized feedback...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        aiFeedback?.let { feedback ->
                            Text(
                                text = if (feedback.usedFallback) {
                                    "Study feedback"
                                } else {
                                    "AI feedback"
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "What you did well",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = feedback.whatWentWell,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "How to improve",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = feedback.whatToImprove,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Stronger answer",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = feedback.improvedAnswer,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                val isLastQuestion =
                    state.currentQuestionIndex == state.questions.lastIndex

                InterviewPrimaryButton(
                    text = if (isLastQuestion) {
                        if (state.isSavingResult) "Saving..." else "Finish Interview"
                    } else {
                        "Next Question"
                    },
                    enabled = !state.isSavingResult,
                    onClick = {
                        if (isLastQuestion) {
                            interviewViewModel.finishInterview(onSaved = onFinished)
                        } else {
                            interviewViewModel.nextQuestion()
                        }
                    }
                )
            }
        }

        state.errorMessage?.let { message ->
            item {
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun InterviewGlassPanel(
    accented: Boolean = false,
    aiCoach: Boolean = false,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)
    val accent = if (aiCoach) Color(0xFF7C3AED) else colors.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        if (accented || aiCoach) accent.copy(alpha = 0.10f)
                        else colors.surface.copy(alpha = 0.84f),
                        colors.surface.copy(alpha = 0.90f)
                    )
                ),
                shape
            )
            .border(
                1.dp,
                if (accented || aiCoach) accent.copy(alpha = 0.20f)
                else colors.outline.copy(alpha = 0.16f),
                shape
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun InterviewPrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(17.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF22B85A).copy(alpha = if (enabled) 1f else 0.35f),
                        Color(0xFF9BE21B).copy(alpha = if (enabled) 1f else 0.35f)
                    )
                ),
                shape
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF101512)
        )
        if (enabled && text == "Next Question") {
            Spacer(Modifier.size(8.dp))
            Icon(
                Icons.Outlined.ArrowForward,
                null,
                Modifier.size(18.dp),
                tint = Color(0xFF101512)
            )
        }
    }
}

@Composable
private fun EvaluationBadge(
    matched: Int,
    total: Int
) {
    val colors = MaterialTheme.colorScheme
    val percentage = if (total == 0) 0 else (matched * 100) / total

    Text(
        text = "$percentage%",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = colors.primary,
        modifier = Modifier
            .background(colors.primary.copy(alpha = 0.11f), RoundedCornerShape(13.dp))
            .padding(horizontal = 11.dp, vertical = 7.dp)
    )
}

@Composable
private fun FeedbackSectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    positive: Boolean
) {
    val colors = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            icon,
            null,
            Modifier.size(18.dp),
            tint = if (positive) colors.primary else Color(0xFFF59E0B)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )
    }
}

@Composable
private fun FeedbackPoint(
    point: String,
    positive: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val accent = if (positive) colors.primary else Color(0xFFF59E0B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(accent.copy(alpha = 0.07f), RoundedCornerShape(14.dp))
            .padding(11.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (positive) "✓" else "○",
            fontWeight = FontWeight.Bold,
            color = accent
        )
        Text(
            text = point,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = colors.onSurfaceVariant
        )
    }
}