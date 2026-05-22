package com.apmlearning.quiz.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apmlearning.quiz.QuizSize
import com.apmlearning.quiz.QuizViewModel
import com.apmlearning.quiz.ui.ActionRowCard
import com.apmlearning.quiz.ui.Badge
import com.apmlearning.quiz.ui.GradientHeader

@Composable
fun TopicScreen(vm: QuizViewModel) {
    BackHandler { vm.backToHomeFromTopics() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        GradientHeader(
            title = "Practice by topic",
            subtitle = "Choose a syllabus area to focus on",
            onBack = { vm.backToHomeFromTopics() },
        )

        Column(modifier = Modifier.padding(20.dp)) {
            vm.topics.forEach { topic ->
                val count = vm.questionsInTopic(topic)
                ActionRowCard(
                    title = topic,
                    subtitle = "$count questions available",
                    onClick = { vm.chooseTopic(topic) },
                    leading = { Badge(text = count.toString()) },
                )
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun SizeScreen(vm: QuizViewModel) {
    BackHandler { vm.backToTopicsFromSize() }

    val topic = vm.pendingTopic ?: ""
    val available = if (topic.isEmpty()) vm.bank.size else vm.questionsInTopic(topic)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        GradientHeader(
            title = topic,
            subtitle = "How long would you like this session to be?",
            onBack = { vm.backToTopicsFromSize() },
        )

        Column(modifier = Modifier.padding(20.dp)) {
            QuizSize.entries.forEach { size ->
                val actual = minOf(size.count, available)
                ActionRowCard(
                    title = "${size.label} quiz",
                    subtitle = if (actual < size.count) {
                        "$actual questions · all available in this topic"
                    } else {
                        "${size.count} questions"
                    },
                    onClick = { vm.startTopicQuiz(size) },
                    leading = { Badge(text = actual.toString()) },
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
