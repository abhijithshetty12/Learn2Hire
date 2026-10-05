package com.learn2hire.app.ui.tutor

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TutorMarkdownText(
    text: String,
    color: Color,
    isUser: Boolean = false
) {
    val annotatedText = if (isUser) {
        AnnotatedString(text)
    } else {
        parseTutorMarkdown(text)
    }

    Text(
        text = annotatedText,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = color
    )
}

private fun parseTutorMarkdown(
    text: String
): AnnotatedString {

    return buildAnnotatedString {

        val lines = text.lines()

        lines.forEachIndexed { index, rawLine ->

            var line = rawLine

            val heading = when {
                line.startsWith("### ") -> {
                    line = line.removePrefix("### ")
                    true
                }

                line.startsWith("## ") -> {
                    line = line.removePrefix("## ")
                    true
                }

                line.startsWith("# ") -> {
                    line = line.removePrefix("# ")
                    true
                }

                else -> false
            }

            if (heading) {
                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            when {
                line.startsWith("* ") -> {
                    append("• ")
                    line = line.removePrefix("* ")
                }

                line.startsWith("- ") -> {
                    append("• ")
                    line = line.removePrefix("- ")
                }
            }

            appendInlineMarkdown(line)

            if (heading) {
                pop()
            }

            if (index != lines.lastIndex) {
                append("\n")
            }
        }
    }
}

private fun AnnotatedString.Builder.appendInlineMarkdown(
    text: String
) {
    var index = 0

    while (index < text.length) {

        if (
            index + 1 < text.length &&
            text[index] == '*' &&
            text[index + 1] == '*'
        ) {
            val end = text.indexOf(
                "**",
                startIndex = index + 2
            )

            if (end != -1) {
                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                )

                append(
                    text.substring(
                        index + 2,
                        end
                    )
                )

                pop()

                index = end + 2
                continue
            }
        }

        if (text[index] == '`') {
            val end = text.indexOf(
                '`',
                startIndex = index + 1
            )

            if (end != -1) {
                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Medium,
                        background = Color.Black.copy(
                            alpha = 0.08f
                        )
                    )
                )

                append(
                    text.substring(
                        index + 1,
                        end
                    )
                )

                pop()

                index = end + 1
                continue
            }
        }

        append(text[index])

        index++
    }
}