package com.learn2hire.app.ui.interview

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
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
import androidx.compose.material3.MaterialTheme

@Composable
fun InterviewScreen(
    onStartInterview: () -> Unit,
    interviewViewModel: InterviewViewModel = viewModel()
) {

    val state by interviewViewModel.uiState.collectAsState()

    val roles = listOf(
        "Software Developer",
        "Frontend Developer",
        "AI/ML Engineer",
        "Data Analyst"
    )

    val interviewTypes = listOf(
        "Technical",
        "HR",
        "Behavioral"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 132.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {

            Text(
                text = "Interview Prep",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Practice with focused questions and improve every answer.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {

            Text(
                text = "Choose your role",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        roles.forEach { role ->

            item {

                SelectionCard(
                    title = role,
                    selected =
                        state.selectedRole == role,
                    onClick = {
                        interviewViewModel
                            .selectRole(role)
                    }
                )
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Interview type",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {

            InterviewTypeCard(
                title = "Technical",
                description = "Role-specific technical questions",
                icon = Icons.Outlined.Code,
                selected =
                    state.selectedType == "Technical",
                onClick = {
                    interviewViewModel
                        .selectType("Technical")
                }
            )
        }

        item {

            InterviewTypeCard(
                title = "HR",
                description = "General HR and career questions",
                icon = Icons.Outlined.Groups,
                selected =
                    state.selectedType == "HR",
                onClick = {
                    interviewViewModel
                        .selectType("HR")
                }
            )
        }

        item {

            InterviewTypeCard(
                title = "Behavioral",
                description = "Situation and experience questions",
                icon = Icons.Outlined.Psychology,
                selected =
                    state.selectedType == "Behavioral",
                onClick = {
                    interviewViewModel
                        .selectType("Behavioral")
                }
            )
        }

        item {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            val enabled =
                state.selectedRole.isNotBlank() &&
                        state.selectedType.isNotBlank()

            val shape = RoundedCornerShape(20.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(shape)
                    .background(
                        if (enabled) {
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF22B85A),
                                    Color(0xFF9BE21B)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    )
                    .border(
                        1.dp,
                        if (enabled) Color.White.copy(alpha = 0.30f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        shape
                    )
                    .clickable(enabled = enabled) {
                        interviewViewModel.startInterview()
                        onStartInterview()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (enabled) "Start Interview"
                    else "Select role and interview type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color(0xFF101512)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) {
                    Brush.linearGradient(
                        listOf(
                            primary.copy(alpha = 0.22f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (selected) primary.copy(alpha = 0.60f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun InterviewTypeCard(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) {
                    Brush.linearGradient(
                        listOf(
                            primary.copy(alpha = 0.20f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.66f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (selected) primary.copy(alpha = 0.60f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (selected) primary.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(25.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
