package com.apmlearning.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.apmlearning.quiz.ui.AppRoot
import com.apmlearning.quiz.ui.theme.ApmQuizTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApmQuizTheme {
                AppRoot(viewModel)
            }
        }
    }
}
