package com.apmlearning.quiz.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apmlearning.quiz.QuizSize
import com.apmlearning.quiz.QuizViewModel
import com.apmlearning.quiz.ui.ActionRowCard
import com.apmlearning.quiz.ui.Badge
import com.apmlearning.quiz.ui.GradientHeader
import com.apmlearning.quiz.ui.theme.Amber

@Composable
fun HomeScreen(vm: QuizViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        GradientHeader(
            title = "APM Foundation Quiz",
            subtitle = "Association for Project Management · Foundation level",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.School, contentDescription = null, tint = Amber)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Questions are drawn from the APM\nFoundation Exam pre-learning material.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            SectionLabel("Take a quiz")
            Spacer(Modifier.height(12.dp))

            QuizSize.entries.forEach { size ->
                ActionRowCard(
                    title = "${size.label} quiz",
                    subtitle = sizeSubtitle(size),
                    onClick = { vm.startFullQuiz(size) },
                    leading = { Badge(text = size.count.toString()) },
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(12.dp))
            SectionLabel("Focus your practice")
            Spacer(Modifier.height(12.dp))

            ActionRowCard(
                title = "Practice by topic",
                subtitle = "Pick one of ${vm.topics.size} syllabus topics to drill",
                onClick = { vm.openTopicPicker() },
                leading = { IconBadge(Icons.Filled.Category) },
            )
            Spacer(Modifier.height(12.dp))

            ActionRowCard(
                title = "Read the learning material",
                subtitle = "Explore the full course content, section by section",
                onClick = { vm.openMaterialList() },
                leading = { IconBadge(Icons.Filled.MenuBook) },
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun IconBadge(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

private fun sizeSubtitle(size: QuizSize): String = when (size) {
    QuizSize.SHORT -> "${size.count} questions · a quick knowledge check"
    QuizSize.MEDIUM -> "${size.count} questions · a solid practice session"
    QuizSize.LARGE -> "${size.count} questions · a full mock exam"
}
