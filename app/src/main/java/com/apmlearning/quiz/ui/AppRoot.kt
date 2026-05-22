package com.apmlearning.quiz.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.apmlearning.quiz.QuizViewModel
import com.apmlearning.quiz.Screen
import com.apmlearning.quiz.ui.screens.HomeScreen
import com.apmlearning.quiz.ui.screens.QuizScreen
import com.apmlearning.quiz.ui.screens.ResultScreen
import com.apmlearning.quiz.ui.screens.SizeScreen
import com.apmlearning.quiz.ui.screens.TopicScreen
import com.apmlearning.quiz.ui.theme.Indigo
import com.apmlearning.quiz.ui.theme.Navy
import com.apmlearning.quiz.ui.theme.NavyLight

val BrandGradient = Brush.linearGradient(listOf(NavyLight, Navy))
val BrandGradientVivid = Brush.linearGradient(listOf(Indigo, Navy))

@Composable
fun AppRoot(vm: QuizViewModel) {
    AnimatedContent(
        targetState = vm.screen,
        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
        label = "screen",
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(vm)
            Screen.TOPIC_PICK -> TopicScreen(vm)
            Screen.SIZE_PICK -> SizeScreen(vm)
            Screen.QUIZ -> QuizScreen(vm)
            Screen.RESULT -> ResultScreen(vm)
        }
    }
}

private fun tween(durationMillis: Int) =
    androidx.compose.animation.core.tween<Float>(durationMillis)
