package com.learn2hire.app.ui.tutor

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn2hire.app.data.model.TutorChat
import com.learn2hire.app.data.model.TutorMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api

private val TutorViolet = Color(0xFF7C3AED)
private val TutorFuchsia = Color(0xFFC026D3)

@Composable
fun TutorScreen(
    topicId: String?,
    onBack: () -> Unit,
    tutorViewModel: TutorViewModel
) {
    val state by tutorViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    LaunchedEffect(topicId) {
        tutorViewModel.initialize(topicId)
    }

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                state.messages.lastIndex
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .imePadding()
            .navigationBarsPadding()
    ) {
        TutorHeader(
            topicName = state.topicName,
            subjectName = state.subjectName,
            onBack = onBack,
            onNewChat = tutorViewModel::startNewChat,
            onHistory = tutorViewModel::openHistory,
            enabled = !state.isLoading
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 16.dp
            )
        ) {
            if (state.topicId != null) {
                item {
                    QuickPromptSection(
                        onExplain = tutorViewModel::explainSimply,
                        onExample = tutorViewModel::giveExample,
                        onExam = tutorViewModel::examAnswer,
                        onQuiz = tutorViewModel::quizMe,
                        enabled = !state.isLoading
                    )
                }
            }

            items(
                items = state.messages,
                key = { it.id }
            ) { message ->
                TutorMessageBubble(message)
            }

            if (state.isLoading) {
                item {
                    TutorTypingBubble()
                }
            }

            state.errorMessage?.let { error ->
                item {
                    TutorErrorCard(
                        message = error,
                        canRetry = state.lastFailedMessage != null,
                        onRetry = tutorViewModel::retryLastMessage,
                        onDismiss = tutorViewModel::dismissError
                    )
                }
            }
        }

        TutorInput(
            value = state.input,
            enabled = !state.isLoading,
            onValueChange = tutorViewModel::updateInput,
            onSend = { tutorViewModel.sendMessage() }
        )
    }

    if (state.isHistoryOpen) {
        TutorHistorySheet(
            chats = state.chats,
            currentChatId = state.chatId,
            onDismiss = tutorViewModel::closeHistory,
            onNewChat = tutorViewModel::startNewChat,
            onOpenChat = tutorViewModel::openChat,
            onDeleteChat = tutorViewModel::deleteChat
        )
    }
}

@Composable
private fun TutorHeader(
    topicName: String,
    subjectName: String,
    onBack: () -> Unit,
    onNewChat: () -> Unit,
    onHistory: () -> Unit,
    enabled: Boolean
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        TutorViolet.copy(alpha = 0.10f),
                        colors.surface.copy(alpha = 0.94f),
                        colors.surface.copy(alpha = 0.90f)
                    )
                )
            )
            .border(1.dp, TutorViolet.copy(alpha = 0.14f))
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconButton(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBack, "Back", Modifier.size(20.dp), tint = colors.onSurface)
        }

        Spacer(Modifier.size(12.dp))

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(TutorViolet, TutorFuchsia))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AutoAwesome, null, Modifier.size(22.dp), tint = Color.White)
        }

        Spacer(Modifier.size(11.dp))

        Column(Modifier.weight(1f)) {
            Text("AI Tutor", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(
                text = when {
                    topicName.isNotBlank() && subjectName.isNotBlank() -> "$subjectName • $topicName"
                    topicName.isNotBlank() -> topicName
                    else -> "Learn2Hire"
                },
                fontSize = 11.sp,
                color = colors.onSurfaceVariant
            )
        }

        GlassIconButton(onClick = { if (enabled) onNewChat() }) {
            Icon(
                Icons.Rounded.Add,
                "New chat",
                Modifier.size(20.dp),
                tint = if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.45f)
            )
        }

        Spacer(Modifier.size(8.dp))

        GlassIconButton(onClick = { if (enabled) onHistory() }) {
            Icon(
                Icons.Rounded.History,
                "Chat history",
                Modifier.size(20.dp),
                tint = if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.45f)
            )
        }
    }
}

@Composable
private fun QuickPromptSection(
    onExplain: () -> Unit,
    onExample: () -> Unit,
    onExam: () -> Unit,
    onQuiz: () -> Unit,
    enabled: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        TutorViolet.copy(alpha = 0.10f),
                        colors.surface.copy(alpha = 0.88f)
                    )
                )
            )
            .border(1.dp, TutorViolet.copy(alpha = 0.16f), shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Rounded.AutoAwesome,
                null,
                Modifier.size(17.dp),
                tint = TutorViolet
            )
            Text(
                text = "Study with AI",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }

        Text(
            text = "Use a quick prompt or ask your own question below.",
            fontSize = 12.sp,
            color = colors.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromptChip("Explain simply", onExplain, enabled, Modifier.weight(1f))
            PromptChip("Give example", onExample, enabled, Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromptChip("Exam answer", onExam, enabled, Modifier.weight(1f))
            PromptChip("Quiz me", onQuiz, enabled, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PromptChip(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(shape)
            .background(colors.surface.copy(alpha = 0.76f))
            .border(
                1.dp,
                TutorViolet.copy(alpha = if (enabled) 0.18f else 0.08f),
                shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) colors.onSurface
            else colors.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun TutorMessageBubble(
    message: TutorMessage
) {
    val colors = MaterialTheme.colorScheme
    val isUser = message.role == "user"
    val shape = RoundedCornerShape(
        topStart = 22.dp,
        topEnd = 22.dp,
        bottomStart = if (isUser) 22.dp else 7.dp,
        bottomEnd = if (isUser) 7.dp else 22.dp
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(TutorViolet, TutorFuchsia))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    null,
                    Modifier.size(16.dp),
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.84f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = if (isUser) "You" else "AI Tutor",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)
            )

            Box(
                modifier = Modifier
                    .clip(shape)
                    .then(
                        if (isUser) {
                            Modifier.background(
                                Brush.linearGradient(
                                    listOf(colors.primary, colors.secondary)
                                )
                            )
                        } else {
                            Modifier.background(
                                Brush.linearGradient(
                                    listOf(
                                        TutorViolet.copy(alpha = 0.08f),
                                        colors.surface.copy(alpha = 0.94f)
                                    )
                                )
                            )
                        }
                    )
                    .border(
                        1.dp,
                        if (isUser) Color.White.copy(alpha = 0.20f)
                        else TutorViolet.copy(alpha = 0.14f),
                        shape
                    )
                    .padding(horizontal = 15.dp, vertical = 12.dp)
            ) {
                TutorMarkdownText(
                    text = message.text,
                    isUser = isUser,
                    color = if (isUser) Color.White else colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun TutorTypingBubble() {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    colors.surface.copy(alpha = 0.90f)
                )
                .border(
                    1.dp,
                    colors.outline.copy(alpha = 0.16f),
                    RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 15.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = colors.primary
            )

            Spacer(Modifier.size(9.dp))

            Text(
                text = "AI Tutor is thinking...",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TutorErrorCard(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                colors.errorContainer.copy(alpha = 0.65f)
            )
            .border(
                width = 1.dp,
                color = colors.error.copy(alpha = 0.22f),
                shape = shape
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = message,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = colors.onErrorContainer
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (canRetry) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.error)
                        .clickable(onClick = onRetry)
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                ) {
                    Text(
                        text = "Try Again",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onError
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onDismiss)
                    .padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "Dismiss",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun TutorInput(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colors.surface.copy(alpha = 0.94f)
            )
            .border(
                width = 1.dp,
                color = colors.outline.copy(alpha = 0.14f)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        val inputShape = RoundedCornerShape(22.dp)

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .clip(inputShape)
                .background(
                    colors.surfaceVariant.copy(alpha = 0.45f)
                )
                .border(
                    1.dp,
                    colors.outline.copy(alpha = 0.16f),
                    inputShape
                )
                .padding(
                    horizontal = 15.dp,
                    vertical = 13.dp
                ),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = colors.onSurface,
                fontSize = 14.sp
            ),
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (value.isNotBlank() && enabled) onSend()
                }
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isBlank()) {
                        Text(
                            text = "Ask about this topic...",
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    innerTextField()
                }
            }
        )

        val canSend =
            value.isNotBlank() && enabled

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (canSend) {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF22B85A),
                                Color(0xFF9BE21B)
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
                )
                .clickable(
                    enabled = canSend,
                    onClick = onSend
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Send,
                contentDescription = "Send",
                modifier = Modifier.size(20.dp),
                tint =
                    if (canSend) {
                        Color(0xFF101512)
                    } else {
                        colors.onSurfaceVariant
                    }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TutorHistorySheet(
    chats: List<TutorChat>,
    currentChatId: String?,
    onDismiss: () -> Unit,
    onNewChat: () -> Unit,
    onOpenChat: (TutorChat) -> Unit,
    onDeleteChat: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var chatToDelete by remember { mutableStateOf<TutorChat?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        contentColor = colors.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Chat history",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = if (chats.isEmpty()) {
                            "Your saved Tutor conversations will appear here."
                        } else {
                            "${chats.size} saved conversation${if (chats.size == 1) "" else "s"}"
                        },
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                GlassIconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Rounded.Close,
                        "Close history",
                        Modifier.size(20.dp),
                        tint = colors.onSurface
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)))
                    .clickable(onClick = onNewChat)
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Add, null, Modifier.size(20.dp), tint = Color.White)
                Spacer(Modifier.size(9.dp))
                Text("New chat", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(14.dp))

            if (chats.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.35f))
                        .border(1.dp, colors.outline.copy(alpha = 0.14f), RoundedCornerShape(22.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.History, null, Modifier.size(28.dp), tint = colors.onSurfaceVariant)
                    Text("No conversations yet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    Text(
                        "Your AI Tutor chats will be saved here automatically.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chats, key = { it.id }) { chat ->
                        TutorHistoryCard(
                            chat = chat,
                            selected = chat.id == currentChatId,
                            onClick = { onOpenChat(chat) },
                            onDelete = { chatToDelete = chat }
                        )
                    }
                }
            }
        }
    }

    chatToDelete?.let { chat ->
        AlertDialog(
            onDismissRequest = { chatToDelete = null },
            title = { Text("Delete conversation?") },
            text = { Text("This will permanently delete \"${chat.title}\" from your Tutor history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteChat(chat.id)
                        chatToDelete = null
                    }
                ) {
                    Text("Delete", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { chatToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TutorHistoryCard(
    chat: TutorChat,
    selected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    val preview = chat.messages.lastOrNull()?.text?.replace("\n", " ")?.take(92).orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) colors.primaryContainer.copy(alpha = 0.55f)
                else colors.surfaceVariant.copy(alpha = 0.30f)
            )
            .border(
                1.dp,
                if (selected) colors.primary.copy(alpha = 0.28f)
                else colors.outline.copy(alpha = 0.14f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(TutorViolet, TutorFuchsia))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AutoAwesome, null, Modifier.size(20.dp), tint = Color.White)
        }

        Spacer(Modifier.size(11.dp))

        Column(Modifier.weight(1f)) {
            Text(
                chat.title.ifBlank { "AI Tutor" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
                maxLines = 1
            )

            val context = when {
                chat.subjectName.isNotBlank() && chat.topicName.isNotBlank() ->
                    "${chat.subjectName} • ${chat.topicName}"
                chat.topicName.isNotBlank() -> chat.topicName
                else -> "General Tutor"
            }

            Text(context, fontSize = 11.sp, color = colors.onSurfaceVariant, maxLines = 1)

            if (preview.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    preview,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = colors.onSurfaceVariant.copy(alpha = 0.82f),
                    maxLines = 2
                )
            }

            if (chat.updatedAt > 0L) {
                Spacer(Modifier.height(5.dp))
                Text(
                    formatTutorChatTime(chat.updatedAt),
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant.copy(alpha = 0.72f)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.DeleteOutline,
                "Delete conversation",
                Modifier.size(19.dp),
                tint = colors.error
            )
        }
    }
}

private fun formatTutorChatTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    return SimpleDateFormat("dd MMM • h:mm a", Locale.getDefault()).format(Date(timestamp))
}

@Composable
private fun GlassIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource =
        remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                colors.surfaceVariant.copy(alpha = 0.55f)
            )
            .border(
                1.dp,
                colors.outline.copy(alpha = 0.16f),
                CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}