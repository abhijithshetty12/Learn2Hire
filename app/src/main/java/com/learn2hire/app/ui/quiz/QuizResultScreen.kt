package com.learn2hire.app.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    quizState: QuizUiState,
    onTryAgain: () -> Unit,
    onBackToTopic: () -> Unit
) {
    BackHandler { onBackToTopic() }

    val percentage = if (total > 0) (score * 100) / total else 0
    val message = when {
        percentage >= 80 -> "Excellent work!"
        percentage >= 60 -> "Good job! Keep practicing."
        else -> "Keep learning and try again."
    }
    val glass = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.size(74.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF22B85A), Color(0xFF9BE21B)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.EmojiEvents, null, tint = Color(0xFF101512), modifier = Modifier.size(38.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Quiz Completed", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(message, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(20.dp))

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(glass)
                .border(1.dp, outline, RoundedCornerShape(28.dp)).padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$percentage%", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("$score out of $total correct", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text("Attempt saved", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(28.dp))
        Text("Review Answers", Modifier.fillMaxWidth(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))

        quizState.questions.forEachIndexed { index, question ->
            val selectedIndex = quizState.answers[question.id]
            val correct = selectedIndex == question.correctAnswerIndex
            val selectedText = selectedIndex?.let { question.options.getOrNull(it) } ?: "No answer"
            val correctText = question.options.getOrNull(question.correctAnswerIndex).orEmpty()
            val stateColor = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            val shape = RoundedCornerShape(22.dp)

            Column(
                Modifier.fillMaxWidth().padding(vertical = 7.dp).clip(shape).background(glass)
                    .border(1.dp, stateColor.copy(alpha = 0.45f), shape).padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (correct) Icons.Outlined.CheckCircle else Icons.Outlined.HighlightOff,
                        null,
                        tint = stateColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(9.dp))
                    Text("Question ${index + 1}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = stateColor)
                }
                Spacer(Modifier.height(10.dp))
                Text(question.question, fontSize = 16.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(14.dp))
                Text("Your answer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(selectedText, fontSize = 15.sp, color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

                if (!correct) {
                    Spacer(Modifier.height(12.dp))
                    Text("Correct answer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(correctText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }

                if (question.explanation.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = outline)
                    Spacer(Modifier.height(12.dp))
                    Text("Explanation", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(question.explanation, fontSize = 14.sp, lineHeight = 21.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        Box(
            Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF22B85A), Color(0xFF9BE21B))))
                .then(Modifier),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onTryAgain,
                modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF101512)),
                shape = RoundedCornerShape(20.dp)
            ) { Text("Try Again", fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onBackToTopic,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, outline)
        ) { Text("Back to Topic", fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(24.dp))
    }
}
