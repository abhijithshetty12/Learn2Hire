package com.learn2hire.app.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn2hire.app.data.local.StudyContent

@Composable
fun TopicDetailScreen(
    topicId: String,
    onBack: () -> Unit,
    onTutorClick: () -> Unit,
    onQuizClick: () -> Unit,
    onAiPracticeClick: () -> Unit,
    studyViewModel: StudyViewModel
) {
    val state by studyViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme

    val topic = StudyContent.getTopic(topicId)

    if (topic == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Topic not found",
                color = colors.onBackground
            )
        }

        return
    }

    val completed =
        state.isTopicCompleted(topic.id)

    LaunchedEffect(topic.id) {
        studyViewModel.markTopicOpened(
            topicId = topic.id,
            subjectId = topic.subjectId
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            TopicBackButton(
                onClick = onBack
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = topic.name,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = topic.description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopicMetaChip(
                    text = "${topic.estimatedMinutes} min lesson",
                    icon = Icons.Rounded.Schedule
                )

                TopicMetaChip(
                    text = if (completed) "Completed" else "In progress",
                    icon = Icons.Rounded.Check,
                    highlighted = completed
                )
            }
        }

        item {
            TopicGlassPanel(accented = true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.MenuBook,
                            null,
                            Modifier.size(19.dp),
                            tint = colors.primary
                        )
                    }

                    Column {
                        Text(
                            text = "Lesson",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Text(
                            text = "Core study material",
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = topic.content,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        item {
            TopicGlassPanel {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.Lightbulb,
                        null,
                        Modifier.size(20.dp),
                        tint = Color(0xFFF59E0B)
                    )
                    Text(
                        text = "Key concepts",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                topic.keyConcepts.forEach { concept ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    colors.primary.copy(alpha = 0.14f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = colors.primary
                            )
                        }

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = concept,
                            fontSize = 14.sp,
                            color = colors.onSurface
                        )
                    }
                }
            }
        }

        item {
            GradientTopicAction(
                title = if (completed) {
                    "Completed"
                } else {
                    "Mark as Complete"
                },
                startColor = if (completed) colors.surfaceVariant else Color(0xFF22B85A),
                endColor = if (completed) colors.surfaceVariant else Color(0xFF9BE21B),
                icon = Icons.Rounded.Check,
                onClick = {
                    if (completed) {
                        studyViewModel.markTopicIncomplete(
                            topicId = topic.id,
                            subjectId = topic.subjectId
                        )
                    } else {
                        studyViewModel.markTopicCompleted(
                            topicId = topic.id,
                            subjectId = topic.subjectId
                        )
                    }
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                SecondaryTopicAction(
                    modifier = Modifier.weight(1f),
                    title = "Ask AI",
                    icon = Icons.Rounded.AutoAwesome,
                    onClick = onTutorClick
                )

                SecondaryTopicAction(
                    modifier = Modifier.weight(1f),
                    title = "Take Quiz",
                    icon = Icons.Rounded.Quiz,
                    onClick = onQuizClick
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryTopicAction(
                modifier = Modifier.fillMaxWidth(),
                title = "Practice with AI",
                icon = Icons.Rounded.AutoAwesome,
                onClick = onAiPracticeClick
            )

            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
private fun TopicMetaChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlighted: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val accent = if (highlighted) colors.primary else colors.onSurfaceVariant

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.09f))
            .border(1.dp, accent.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = accent
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = accent
        )
    }
}

@Composable
private fun TopicGlassPanel(
    accented: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        if (accented) colors.primary.copy(alpha = 0.09f)
                        else colors.surface.copy(alpha = 0.92f),
                        colors.surface.copy(alpha = 0.92f)
                    )
                )
            )
            .border(
                1.dp,
                if (accented) colors.primary.copy(alpha = 0.18f)
                else colors.outline.copy(alpha = 0.18f),
                shape
            )
            .padding(18.dp),
        content = content
    )
}

@Composable
private fun GradientTopicAction(
    title: String,
    startColor: Color,
    endColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val interactionSource =
        remember { MutableInteractionSource() }

    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(startColor, endColor)
                )
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.22f),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (title == "Completed") {
                MaterialTheme.colorScheme.onSurface
            } else {
                Color(0xFF101512)
            }
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (title == "Completed") {
                MaterialTheme.colorScheme.onSurface
            } else {
                Color(0xFF101512)
            }
        )
    }
}

@Composable
private fun SecondaryTopicAction(
    modifier: Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource =
        remember { MutableInteractionSource() }

    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(
                colors.surface.copy(alpha = 0.92f)
            )
            .border(
                1.dp,
                colors.outline.copy(alpha = 0.18f),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colors.primary
        )

        Spacer(modifier = Modifier.size(7.dp))

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface
        )
    }
}

@Composable
private fun TopicBackButton(
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource =
        remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                colors.surface.copy(alpha = 0.92f)
            )
            .border(
                1.dp,
                colors.outline.copy(alpha = 0.18f),
                CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(20.dp),
            tint = colors.onSurface
        )
    }
}