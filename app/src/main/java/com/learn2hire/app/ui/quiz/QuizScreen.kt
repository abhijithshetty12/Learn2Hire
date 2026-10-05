package com.learn2hire.app.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QuizScreen(
    topicId: String,
    onBack: () -> Unit,
    onQuizComplete: (Int, Int) -> Unit,
    quizViewModel: QuizViewModel = viewModel()
) {
    val uiState by quizViewModel.uiState.collectAsState()

    BackHandler(enabled = !uiState.isSavingResult) { onBack() }

    LaunchedEffect(topicId) { quizViewModel.loadQuiz(topicId) }
    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) onQuizComplete(uiState.score, uiState.totalQuestions)
    }

    val question = uiState.currentQuestion
    val glass = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)

    if (question == null) {
        Box(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No quiz available for this topic yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(glass).border(1.dp, outline, CircleShape)
                    .clickable(enabled = !uiState.isSavingResult) { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Exit quiz", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.AssignmentTurnedIn,
                        null,
                        Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Fixed Assessment",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "Quiz",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text("${uiState.currentQuestionIndex + 1}/${uiState.totalQuestions}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(22.dp))

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(glass)
                .border(1.dp, outline, RoundedCornerShape(22.dp)).padding(18.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text("${((uiState.currentQuestionIndex + 1) * 100) / uiState.totalQuestions}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
        }

        Spacer(Modifier.height(18.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.09f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                        )
                    )
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    RoundedCornerShape(28.dp)
                )
                .padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Icon(
                    Icons.Outlined.AssignmentTurnedIn,
                    null,
                    Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "QUESTION ${uiState.currentQuestionIndex + 1}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(11.dp))
            Text(
                question.question,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f))
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.AssignmentTurnedIn,
                null,
                Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Select one answer. Quiz results count toward your learning performance.",
                modifier = Modifier.weight(1f),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(10.dp))

        question.options.forEachIndexed { index, option ->
            val selected = uiState.selectedAnswerIndex == index
            val shape = RoundedCornerShape(20.dp)
            val optionBackground = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f) else glass
            val optionBorder = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.75f) else outline

            Row(
                Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(shape).background(optionBackground)
                    .border(if (selected) 1.5.dp else 1.dp, optionBorder, shape)
                    .clickable(enabled = !uiState.isSavingResult) { quizViewModel.selectAnswer(index) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        ('A'.code + index).toChar().toString(),
                        fontWeight = FontWeight.Bold,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    option,
                    Modifier.weight(1f),
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
                )
                if (selected) {
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        Icons.Outlined.CheckCircle,
                        null,
                        Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        uiState.errorMessage?.let { message ->
            Spacer(Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(22.dp))

        val enabled = uiState.selectedAnswerIndex != null && !uiState.isSavingResult
        Box(
            Modifier.fillMaxWidth().height(58.dp).clip(RoundedCornerShape(20.dp))
                .background(
                    if (enabled) Brush.horizontalGradient(listOf(Color(0xFF22B85A), Color(0xFF9BE21B)))
                    else Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                )
                .clickable(enabled = enabled) { quizViewModel.submitAnswer() },
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isSavingResult) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(10.dp))
                    Text("Saving Result…", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val isLastQuestion =
                    uiState.currentQuestionIndex == uiState.questions.lastIndex

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (isLastQuestion) "Submit Quiz" else "Next Question",
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) Color(0xFF101512)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isLastQuestion && enabled) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Outlined.ArrowForward,
                            null,
                            Modifier.size(18.dp),
                            tint = Color(0xFF101512)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(72.dp))
    }
}
