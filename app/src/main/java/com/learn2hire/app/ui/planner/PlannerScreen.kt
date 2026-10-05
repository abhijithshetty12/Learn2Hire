package com.learn2hire.app.ui.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learn2hire.app.data.model.PlannerTask

@Composable
fun PlannerScreen(
    onBack: () -> Unit,
    plannerViewModel: PlannerViewModel = viewModel()
) {
    val state by plannerViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 18.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column {
                    Text(
                        text = "Smart Planner",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Turn your available time into a focused study plan.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        state.errorMessage?.let { message ->
            item {
                GlassPanel {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            GlassPanel(accented = true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Timer,
                            null,
                            Modifier.size(21.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "Available today",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Set how much focused study time you have.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.availableMinutes,
                    onValueChange = plannerViewModel::updateAvailableMinutes,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Available minutes") },
                    suffix = { Text("min") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        item {
            GlassPanel {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.AddTask,
                        null,
                        Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Add a task",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = state.taskTitle,
                    onValueChange = plannerViewModel::updateTaskTitle,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Task name") },
                    placeholder = { Text("e.g. Revise Naïve Bayes") },
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = state.daysLeft,
                        onValueChange = plannerViewModel::updateDaysLeft,
                        modifier = Modifier.weight(1f),
                        label = { Text("Days left") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.estimatedMinutes,
                        onValueChange = plannerViewModel::updateEstimatedMinutes,
                        modifier = Modifier.weight(1f),
                        label = { Text("Est. minutes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(12.dp))

                GradientActionButton(
                    text = if (state.isSaving) "Saving..." else "Add Task",
                    enabled = !state.isSaving,
                    onClick = plannerViewModel::addTask
                )
            }
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (state.tasks.isEmpty()) {
            item {
                GlassPanel {
                    Text(
                        text = "No tasks yet",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Add your first study task to start planning.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "Tasks",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(
                items = state.tasks,
                key = { it.id }
            ) { task ->
                PlannerTaskCard(
                    task = task,
                    onToggle = { plannerViewModel.toggleTaskCompleted(task) },
                    onDelete = { plannerViewModel.removeTask(task.id) }
                )
            }

            item {
                GradientActionButton(
                    text = "Generate Plan",
                    onClick = plannerViewModel::generatePlan
                )
            }
        }

        if (state.generatedPlan.isNotEmpty()) {
            item {
                Text(
                    text = "Today's Plan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(
                items = state.generatedPlan,
                key = { "${it.title}-${it.durationMinutes}" }
            ) { session ->
                GlassPanel(accented = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
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
                                text = session.title,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Study session",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "${session.durationMinutes} min",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerTaskCard(
    task: PlannerTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val priorityAccent = when (task.priority.lowercase()) {
        "high" -> Color(0xFFFF6B5F)
        "low" -> Color(0xFF22B85A)
        else -> Color(0xFFF59E0B)
    }

    GlassPanel(accented = !task.completed) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.completed) {
                            colors.primary.copy(alpha = 0.18f)
                        } else {
                            colors.surfaceVariant.copy(alpha = 0.65f)
                        }
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Mark incomplete",
                        tint = colors.primary
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .border(2.dp, colors.onSurfaceVariant, CircleShape)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                if (task.completed) {
                    Text(
                        text = "Completed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlannerMetaChip("${task.daysLeft} days left")
                        PlannerMetaChip(
                            text = "${task.priority} priority",
                            accent = priorityAccent
                        )
                    }
                }

                Text(
                    text = "Estimated ${task.estimatedMinutes} min",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete task",
                    tint = colors.error.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
private fun PlannerMetaChip(
    text: String,
    accent: Color? = null
) {
    val colors = MaterialTheme.colorScheme
    val chipColor = accent ?: colors.onSurfaceVariant

    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = chipColor,
        modifier = Modifier
            .clip(CircleShape)
            .background(chipColor.copy(alpha = 0.09f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun GlassPanel(
    accented: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        if (accented) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.80f)
                        },
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.84f)
                    )
                )
            )
            .border(
                1.dp,
                if (accented) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                },
                shape
            )
            .padding(17.dp),
        content = content
    )
}

@Composable
private fun GradientActionButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
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
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = if (enabled) {
                Color(0xFF101512)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}