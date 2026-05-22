package com.apmlearning.quiz

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.apmlearning.quiz.data.MaterialLibrary
import com.apmlearning.quiz.data.MaterialSection
import com.apmlearning.quiz.data.QuestionBank
import com.apmlearning.quiz.data.QuizEngine
import com.apmlearning.quiz.data.QuizQuestion
import com.apmlearning.quiz.data.QuizSession
import com.apmlearning.quiz.data.RawQuestion

enum class Screen { HOME, TOPIC_PICK, SIZE_PICK, QUIZ, RESULT, MATERIAL_LIST, MATERIAL_DETAIL }

/** Sizes offered for a quiz. */
enum class QuizSize(val label: String, val count: Int) {
    SHORT("Short", 10),
    MEDIUM("Medium", 30),
    LARGE("Large", 100),
}

class QuizViewModel(app: Application) : AndroidViewModel(app) {

    val bank: List<RawQuestion> = QuestionBank.load(app)
    val topics: List<String> = bank.map { it.topic }.distinct().sorted()
    val material: List<MaterialSection> = MaterialLibrary.load(app)

    fun questionsInTopic(topic: String): Int = bank.count { it.topic == topic }

    var selectedSection by mutableStateOf<MaterialSection?>(null)
        private set

    var screen by mutableStateOf(Screen.HOME)
        private set

    private var session: QuizSession? = null

    var current by mutableStateOf(0)
        private set
    var score by mutableStateOf(0)
        private set
    var answered by mutableStateOf(0)
        private set

    // Per-question UI state.
    var selectedLetter by mutableStateOf<Char?>(null)
        private set
    var hintShown by mutableStateOf(false)
        private set
    var isAnswered by mutableStateOf(false)
        private set
    var toldAnswer by mutableStateOf(false)
        private set

    var pendingTopic by mutableStateOf<String?>(null)
        private set

    val quizQuestions: List<QuizQuestion> get() = session?.questions ?: emptyList()
    val total: Int get() = quizQuestions.size
    val currentQuestion: QuizQuestion? get() = quizQuestions.getOrNull(current)
    val sessionTopic: String? get() = session?.topic
    val isLastQuestion: Boolean get() = current >= total - 1

    // --- Navigation ---

    fun goHome() { screen = Screen.HOME }

    fun openTopicPicker() { screen = Screen.TOPIC_PICK }

    fun chooseTopic(topic: String) {
        pendingTopic = topic
        screen = Screen.SIZE_PICK
    }

    fun backToHomeFromTopics() { screen = Screen.HOME }

    fun backToTopicsFromSize() { screen = Screen.TOPIC_PICK }

    fun openMaterialList() { screen = Screen.MATERIAL_LIST }

    fun openSection(section: MaterialSection) {
        selectedSection = section
        screen = Screen.MATERIAL_DETAIL
    }

    fun backToHomeFromMaterial() { screen = Screen.HOME }

    fun backToMaterialList() { screen = Screen.MATERIAL_LIST }

    // --- Starting a quiz ---

    fun startFullQuiz(size: QuizSize) = startQuiz(size.count, null)

    fun startTopicQuiz(size: QuizSize) = startQuiz(size.count, pendingTopic)

    private fun startQuiz(size: Int, topic: String?) {
        session = QuizEngine.build(bank, size, topic)
        current = 0
        score = 0
        answered = 0
        resetQuestionState()
        screen = Screen.QUIZ
    }

    private fun resetQuestionState() {
        selectedLetter = null
        hintShown = false
        isAnswered = false
        toldAnswer = false
    }

    // --- Playing ---

    fun showHint() {
        if (!isAnswered) hintShown = true
    }

    fun selectAnswer(letter: Char) {
        if (isAnswered) return
        selectedLetter = letter
        isAnswered = true
        answered++
        if (currentQuestion?.correctLetter == letter) score++
    }

    fun tellMe() {
        if (isAnswered) return
        isAnswered = true
        toldAnswer = true
        answered++
    }

    fun next() {
        if (isLastQuestion) {
            screen = Screen.RESULT
        } else {
            current++
            resetQuestionState()
        }
    }

    fun quit() { screen = Screen.RESULT }

    // --- Results ---

    /** Percentage of answered questions that were correct. */
    fun percentage(): Int = if (answered == 0) 0 else (score * 100) / answered

    val answeredAllQuestions: Boolean get() = answered >= total && total > 0
}
