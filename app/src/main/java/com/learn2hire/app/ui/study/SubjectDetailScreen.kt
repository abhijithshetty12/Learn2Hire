package com.learn2hire.app.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    onBack: () -> Unit,
    onTopicClick: (String) -> Unit,
    studyViewModel: StudyViewModel
) {
    val state by studyViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme

    val subject = state.subjects.find {
        it.id == subjectId
    }

    val topics = state.topics.filter {
        it.subjectId == subjectId
    }

    if (state.isLoading && subject == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colors.primary
            )
        }

        return
    }

    if (subject == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(20.dp)
        ) {
            GlassBackButton(
                onClick = onBack
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Subject not found",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "This subject is not available in the current study catalog.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            GlassBackButton(
                onClick = onBack
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = subject.name,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subject.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(50.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    colors.primary,
                                    colors.secondary
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.24f),
                            RoundedCornerShape(17.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            SubjectProgressPanel(
                completed = subject.completedTopics,
                total = subject.totalTopics,
                progress = subject.progress
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Topics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Complete lessons to update your progress.",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )
        }

        if (topics.isEmpty()) {
            item {
                EmptyTopicsCard()
            }
        } else {
            items(
                items = topics,
                key = { it.id }
            ) { topic ->
                val completed =
                    state.isTopicCompleted(topic.id)

                TopicGlassCard(
                    name = topic.name,
                    description = topic.description,
                    estimatedMinutes = topic.estimatedMinutes,
                    completed = completed,
                    onClick = {
                        onTopicClick(topic.id)
                    }
                )
            }
        }

        if (state.errorMessage != null) {
            item {
                ErrorGlassCard(
                    message = state.errorMessage
                        ?: "Your progress could not be synchronized."
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
private fun SubjectProgressPanel(
    completed: Int,
    total: Int,
    progress: Float
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    val safeProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        colors.primary.copy(alpha = 0.19f),
                        colors.secondary.copy(alpha = 0.10f),
                        colors.surface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.38f),
                        colors.primary.copy(alpha = 0.22f),
                        colors.outline.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            )
            .padding(19.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your progress",
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$completed of $total topics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(colors.surface.copy(alpha = 0.70f))
                        .border(
                            1.dp,
                            colors.primary.copy(alpha = 0.18f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(safeProgress * 100).toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(CircleShape)
                    .background(colors.onSurface.copy(alpha = 0.07f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(safeProgress)
                        .height(9.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    colors.primary,
                                    colors.secondary
                                )
                            )
                        )
                )
            }

            if (safeProgress >= 1f && total > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.primary
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "Subject completed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicGlassCard(
    name: String,
    description: String,
    estimatedMinutes: Int,
    completed: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource =
        remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    if (completed) {
                        listOf(
                            colors.primary.copy(alpha = 0.13f),
                            colors.secondary.copy(alpha = 0.07f),
                            colors.surface.copy(alpha = 0.94f)
                        )
                    } else {
                        listOf(
                            colors.surface.copy(alpha = 0.96f),
                            colors.surface.copy(alpha = 0.90f)
                        )
                    }
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    if (completed) {
                        listOf(
                            colors.primary.copy(alpha = 0.30f),
                            Color.White.copy(alpha = 0.24f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.28f),
                            colors.outline.copy(alpha = 0.14f)
                        )
                    }
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(17.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        if (completed) {
                            Brush.linearGradient(
                                listOf(
                                    colors.primary,
                                    colors.secondary
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    colors.surfaceVariant,
                                    colors.surfaceVariant
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (completed) {
                        Icons.Rounded.CheckCircle
                    } else {
                        Icons.Rounded.School
                    },
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = if (completed) {
                        Color.White
                    } else {
                        colors.onSurfaceVariant
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )

                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = colors.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text(
                        text = "$estimatedMinutes min",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )

                    if (completed) {
                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = "Completed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassBackButton(
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource =
        remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(colors.surface.copy(alpha = 0.90f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.34f),
                        colors.outline.copy(alpha = 0.16f)
                    )
                ),
                shape = CircleShape
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

@Composable
private fun EmptyTopicsCard() {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface.copy(alpha = 0.94f))
            .border(
                1.dp,
                colors.outline.copy(alpha = 0.16f),
                shape
            )
            .padding(18.dp)
    ) {
        Text(
            text = "No topics yet",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Topics for this subject will appear here.",
            fontSize = 13.sp,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorGlassCard(
    message: String
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.errorContainer.copy(alpha = 0.72f))
            .border(
                1.dp,
                colors.error.copy(alpha = 0.18f),
                shape
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Progress sync issue",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onErrorContainer
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = colors.onErrorContainer.copy(alpha = 0.82f)
        )
    }
}
