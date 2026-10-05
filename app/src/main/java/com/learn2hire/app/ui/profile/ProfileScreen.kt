package com.learn2hire.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn2hire.app.ui.theme.ThemeMode

private val ActionBlue = Color(0xFF2563EB)
private val ActionIndigo = Color(0xFF4F46E5)
private val ActionAmber = Color(0xFFF59E0B)
private val ActionYellow = Color(0xFFFACC15)
private val ActionViolet = Color(0xFF7C3AED)
private val ActionPurple = Color(0xFF9333EA)
private val ActionRed = Color(0xFFEF4444)
private val ActionRose = Color(0xFFE11D48)
private val ActionGraphite = Color(0xFF3F454D)
private val ActionSlate = Color(0xFF20242A)

@Composable
fun ProfileScreen(
    profileState: ProfileUiState,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNameChange: (String) -> Unit,
    onCourseChange: (String) -> Unit,
    onSemesterChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    if (profileState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colors.primary,
                strokeWidth = 3.dp
            )
        }
        return
    }

    val profile = profileState.profile

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 38.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Profile",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 34.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp,
                    color = colors.onBackground
                )
                Text(
                    text = "Account, learning and appearance",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        profileState.errorMessage?.let { message ->
            item {
                StatusGlass(
                    text = message,
                    background = colors.errorContainer.copy(alpha = 0.72f),
                    foreground = colors.onErrorContainer
                )
            }
        }

        profileState.successMessage?.let { message ->
            item {
                StatusGlass(
                    text = message,
                    background = colors.primaryContainer.copy(alpha = 0.70f),
                    foreground = colors.primary
                )
            }
        }

        if (profile != null) {
            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    radius = 30
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(17.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GradientAvatar(
                                letter = profile.name.firstOrNull()?.uppercase()
                                    ?: profile.email.firstOrNull()?.uppercase()
                                    ?: "L"
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = profile.name.takeIf { it.isNotBlank() }
                                            ?: "Learn2Hire User",
                                        modifier = Modifier.weight(1f, fill = false),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = (-0.25).sp,
                                        color = colors.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Outlined.Verified,
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp),
                                        tint = colors.primary
                                    )
                                }

                                Text(
                                    text = profile.email,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 13.sp,
                                    color = colors.onSurfaceVariant
                                )

                                Text(
                                    text = if (profile.authProvider == "google") {
                                        "Google account"
                                    } else {
                                        "Email account"
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.primary
                                )
                            }
                        }

                        if (!profileState.isEditing) {
                            GradientActionButton(
                                text = "Edit Profile",
                                icon = Icons.Outlined.Person,
                                startColor = ActionBlue,
                                endColor = ActionIndigo,
                                onClick = onEditClick
                            )
                        }
                    }
                }
            }

            if (profileState.isEditing) {
                item {
                    SectionTitle(
                        title = "Edit Profile",
                        subtitle = "Update your personal learning details"
                    )
                }

                item {
                    GlassPanel(
                        modifier = Modifier.fillMaxWidth(),
                        radius = 28
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GlassTextField(
                                value = profileState.editedName,
                                onValueChange = onNameChange,
                                label = "Name",
                                icon = Icons.Outlined.Person
                            )
                            GlassTextField(
                                value = profile.email,
                                onValueChange = {},
                                label = "Email",
                                icon = Icons.Outlined.Email,
                                enabled = false
                            )
                            GlassTextField(
                                value = profileState.editedCourse,
                                onValueChange = onCourseChange,
                                label = "Course",
                                icon = Icons.Outlined.School
                            )
                            GlassTextField(
                                value = profileState.editedSemester,
                                onValueChange = onSemesterChange,
                                label = "Semester",
                                icon = Icons.Outlined.School
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onCancelEdit,
                                    enabled = !profileState.isSaving,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        colors.outline.copy(alpha = 0.30f)
                                    )
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                GradientActionButton(
                                    text = "Save",
                                    startColor = colors.primary,
                                    endColor = colors.secondary,
                                    enabled = !profileState.isSaving,
                                    loading = profileState.isSaving,
                                    modifier = Modifier.weight(1f),
                                    onClick = onSaveProfile
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    SectionTitle(
                        title = "Account",
                        subtitle = "Your personal information"
                    )
                }

                item {
                    GlassSettingsGroup {
                        GlassSettingRow(
                            icon = Icons.Outlined.Person,
                            title = "Name",
                            value = profile.name.takeIf { it.isNotBlank() } ?: "Not added",
                            accentStart = ActionBlue,
                            accentEnd = ActionIndigo
                        )
                        GlassDivider()
                        GlassSettingRow(
                            icon = Icons.Outlined.Email,
                            title = "Email",
                            value = profile.email,
                            accentStart = ActionViolet,
                            accentEnd = ActionPurple
                        )
                    }
                }

                item {
                    SectionTitle(
                        title = "Learning",
                        subtitle = "Academic details used by Learn2Hire"
                    )
                }

                item {
                    GlassSettingsGroup {
                        GlassSettingRow(
                            icon = Icons.Outlined.School,
                            title = "Course",
                            value = profile.course.takeIf { it.isNotBlank() } ?: "Not added",
                            accentStart = colors.primary,
                            accentEnd = colors.secondary
                        )
                        GlassDivider()
                        GlassSettingRow(
                            icon = Icons.Outlined.School,
                            title = "Semester",
                            value = profile.semester.takeIf { it.isNotBlank() } ?: "Not added",
                            accentStart = ActionAmber,
                            accentEnd = ActionYellow
                        )
                    }
                }
            }
        }

        item {
            SectionTitle(
                title = "Appearance",
                subtitle = "Choose the interface style"
            )
        }

        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                radius = 28
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Theme",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )

                    ThemeLiquidToggle(
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange
                    )

                    Text(
                        text = when (themeMode) {
                            ThemeMode.SYSTEM -> "Follows your device appearance automatically."
                            ThemeMode.LIGHT -> "Bright glass surfaces with warm highlights."
                            ThemeMode.DARK -> "Deep glass surfaces with vivid accents."
                        },
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        item {
            GradientActionButton(
                text = "Sign Out",
                icon = Icons.Outlined.ExitToApp,
                startColor = ActionRed,
                endColor = ActionRose,
                enabled = !profileState.isSaving,
                onClick = onSignOut
            )
        }
    }
}

@Composable
private fun GradientAvatar(letter: String) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.30f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.08f)
                        )
                    )
                )
        )
        Text(
            text = letter,
            fontFamily = FontFamily.SansSerif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.25).sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GlassPanel(
    modifier: Modifier = Modifier,
    radius: Int = 24,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(radius.dp)
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier.border(
            width = 1.dp,
            color = colors.outline.copy(alpha = 0.22f),
            shape = shape
        ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface.copy(alpha = 0.82f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.24f),
                                Color.Transparent
                            )
                        )
                    )
            )
            content()
        }
    }
}

@Composable
private fun GlassSettingsGroup(
    content: @Composable () -> Unit
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        radius = 25
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun GlassSettingRow(
    icon: ImageVector,
    title: String,
    value: String,
    accentStart: Color,
    accentEnd: Color
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(accentStart, accentEnd)
                    )
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.20f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                maxLines = 1,
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = colors.onSurfaceVariant
            )
            Text(
                text = value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.SansSerif,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurface
            )
        }
    }
}

@Composable
private fun GlassDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 67.dp)
            .height(1.dp)
            .background(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )
    )
}

@Composable
private fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        label = {
            Text(
                text = label,
                fontFamily = FontFamily.SansSerif
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) colors.primary else colors.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(17.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant.copy(alpha = 0.36f),
            unfocusedContainerColor = colors.surfaceVariant.copy(alpha = 0.24f),
            disabledContainerColor = colors.surfaceVariant.copy(alpha = 0.17f),
            focusedBorderColor = colors.primary.copy(alpha = 0.78f),
            unfocusedBorderColor = colors.outline.copy(alpha = 0.28f),
            disabledBorderColor = colors.outline.copy(alpha = 0.15f)
        )
    )
}

@Composable
private fun GradientActionButton(
    text: String,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val alpha = if (enabled) 1f else 0.52f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(startColor, endColor)
                ),
                alpha = alpha
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f * alpha),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = enabled && !loading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.17f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f)
                        )
                    )
                )
        )

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(19.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = Color.White
                    )
                }
                Text(
                    text = text,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun StatusGlass(
    text: String,
    background: Color,
    foreground: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .border(
                1.dp,
                foreground.copy(alpha = 0.16f),
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = foreground
        )
    }
}

@Composable
private fun ThemeLiquidToggle(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceVariant.copy(alpha = 0.56f))
            .border(
                width = 1.dp,
                color = colors.outline.copy(alpha = 0.20f),
                shape = shape
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeSegment(
            text = "System",
            icon = Icons.Outlined.AutoMode,
            selected = themeMode == ThemeMode.SYSTEM,
            startColor = ActionGraphite,
            endColor = ActionSlate,
            modifier = Modifier.weight(1f),
            onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
        )
        ThemeSegment(
            text = "Light",
            icon = Icons.Outlined.LightMode,
            selected = themeMode == ThemeMode.LIGHT,
            startColor = ActionAmber,
            endColor = ActionYellow,
            modifier = Modifier.weight(1f),
            onClick = { onThemeModeChange(ThemeMode.LIGHT) }
        )
        ThemeSegment(
            text = "Dark",
            icon = Icons.Outlined.DarkMode,
            selected = themeMode == ThemeMode.DARK,
            startColor = ActionIndigo,
            endColor = ActionViolet,
            modifier = Modifier.weight(1f),
            onClick = { onThemeModeChange(ThemeMode.DARK) }
        )
    }
}

@Composable
private fun ThemeSegment(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(76.dp)
            .clip(shape)
            .then(
                if (selected) {
                    Modifier
                        .background(Brush.horizontalGradient(listOf(startColor, endColor)))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), shape)
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.07f)
                            )
                        )
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(21.dp),
                tint = if (selected) Color.White else colors.onSurfaceVariant
            )
            Text(
                text = text,
                maxLines = 1,
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else colors.onSurfaceVariant
            )
        }
    }
}
