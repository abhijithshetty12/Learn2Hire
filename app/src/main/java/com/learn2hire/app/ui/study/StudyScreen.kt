package com.learn2hire.app.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learn2hire.app.ui.components.SubjectCard

@Composable
fun StudyScreen(
    onSubjectClick: (String) -> Unit,
    studyViewModel: StudyViewModel = viewModel()
) {
    val uiState by studyViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme

    val totalTopics = uiState.subjects.sumOf { it.totalTopics }
    val completedTopics = uiState.subjects.sumOf { it.completedTopics }
    val overallProgress =
        if (totalTopics > 0) {
            completedTopics.toFloat() / totalTopics.toFloat()
        } else {
            0f
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Study",
                        fontSize = 32.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Learn, practice and keep your momentum.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
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
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            StudyOverviewCard(
                completedTopics = completedTopics,
                totalTopics = totalTopics,
                progress = overallProgress
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your subjects",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Pick up where you left off.",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colors.primary
                    )
                }
            }
        } else if (uiState.errorMessage != null && uiState.subjects.isEmpty()) {
            item {
                StudyMessageCard(
                    title = "Couldn't load your study space",
                    message = uiState.errorMessage ?: "Please try again."
                )
            }
        } else if (uiState.subjects.isEmpty()) {
            item {
                StudyMessageCard(
                    title = "No subjects yet",
                    message = "Your learning subjects will appear here."
                )
            }
        } else {
            items(
                items = uiState.subjects,
                key = { it.id }
            ) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = {
                        onSubjectClick(subject.id)
                    }
                )
            }
        }

        if (uiState.errorMessage != null && uiState.subjects.isNotEmpty()) {
            item {
                StudyMessageCard(
                    title = "Sync issue",
                    message = uiState.errorMessage ?: "Progress may be temporarily out of date."
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun StudyOverviewCard(
    completedTopics: Int,
    totalTopics: Int,
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
                        colors.primary.copy(alpha = 0.18f),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.primary.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(21.dp),
                            tint = colors.primary
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 11.dp)
                    ) {
                        Text(
                            text = "Overall progress",
                            fontSize = 13.sp,
                            color = colors.onSurfaceVariant
                        )

                        Text(
                            text = "$completedTopics of $totalTopics topics",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                    }
                }

                Text(
                    text = "${(safeProgress * 100).toInt()}%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
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

            if (safeProgress >= 1f && totalTopics > 0) {
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
                        text = "All current topics completed",
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
private fun StudyMessageCard(
    title: String,
    message: String
) {
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
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = message,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = colors.onSurfaceVariant
        )
    }
}
