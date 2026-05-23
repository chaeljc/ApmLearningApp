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

    var materialOpenedFromQuiz by mutableStateOf(false)
        private set

    /** Index of the block to scroll to and highlight when opening a section from a question. */
    var highlightBlockIndex by mutableStateOf<Int?>(null)
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

    fun openMaterialList() {
        materialOpenedFromQuiz = false
        highlightBlockIndex = null
        screen = Screen.MATERIAL_LIST
    }

    fun openSection(section: MaterialSection) {
        selectedSection = section
        materialOpenedFromQuiz = false
        highlightBlockIndex = null
        screen = Screen.MATERIAL_DETAIL
    }

    /**
     * Jump from a quiz question straight into the matching section, scrolled to the
     * quoted block. Prefers the topic's own section, but if the source quote can't
     * be located there it searches the other sections so the user always lands on
     * the right passage.
     */
    fun openSectionFromQuiz(topicTitle: String, sourceQuote: String?) {
        val primary = material.firstOrNull { it.title == topicTitle } ?: return

        var section = primary
        var highlight: Int? = null

        if (sourceQuote != null) {
            highlight = findBlockBySubstring(primary, sourceQuote)
            if (highlight == null) {
                for (other in material) {
                    if (other === primary) continue
                    val idx = findBlockBySubstring(other, sourceQuote)
                    if (idx != null) {
                        section = other
                        highlight = idx
                        break
                    }
                }
            }
            if (highlight == null) {
                highlight = findBlockByOverlap(primary, sourceQuote)
            }
        }

        selectedSection = section
        highlightBlockIndex = highlight
        materialOpenedFromQuiz = true
        screen = Screen.MATERIAL_DETAIL
    }

    fun backToHomeFromMaterial() {
        highlightBlockIndex = null
        screen = Screen.HOME
    }

    /** Close the detail screen: back to the quiz if that's where we came from, otherwise to the list. */
    fun closeMaterialDetail() {
        if (materialOpenedFromQuiz) {
            materialOpenedFromQuiz = false
            highlightBlockIndex = null
            screen = Screen.QUIZ
        } else {
            highlightBlockIndex = null
            screen = Screen.MATERIAL_LIST
        }
    }

    /**
     * High-precision substring match in [section]. Tries the whole source plus
     * each individual line of it, with progressively shorter probes, in both
     * directions (source-contains-block-start and block-contains-source-probe).
     * Returns null if no substring match is found.
     */
    private fun findBlockBySubstring(section: MaterialSection, sourceQuote: String): Int? {
        val candidates = section.blocks.mapIndexedNotNull { index, block ->
            val text = blockText(block)
            if (text.length >= 8) index to text else null
        }
        if (candidates.isEmpty()) return null

        val probes = mutableListOf<String>()
        val whole = normalise(sourceQuote)
        if (whole.length >= 8) probes.add(whole)
        sourceQuote.split('\n').forEach { line ->
            val normLine = normalise(line)
            if (normLine.length >= 8 && normLine != whole) probes.add(normLine)
        }
        if (probes.isEmpty()) return null

        for (probeLen in listOf(80, 50, 25)) {
            for (probe in probes) {
                val sourceProbe = probe.take(probeLen).trim()
                if (sourceProbe.length < 8) continue
                for ((index, text) in candidates) {
                    if (text.contains(sourceProbe)) return index
                    val blockProbe = text.take(probeLen).trim()
                    if (blockProbe.length >= 8 && sourceProbe.contains(blockProbe)) return index
                }
            }
        }
        return null
    }

    /** Word-overlap fallback used only when no substring match exists anywhere. */
    private fun findBlockByOverlap(section: MaterialSection, sourceQuote: String): Int? {
        val sourceWords = normalise(sourceQuote)
            .split(' ').filter { it.length > 3 }.toSet()
        if (sourceWords.size < 3) return null

        var bestIndex = -1
        var bestOverlap = 2
        section.blocks.forEachIndexed { index, block ->
            val text = blockText(block)
            if (text.length >= 8) {
                val overlap = text.split(' ').filter { it.length > 3 }.toSet()
                    .intersect(sourceWords).size
                if (overlap > bestOverlap) {
                    bestOverlap = overlap
                    bestIndex = index
                }
            }
        }
        return if (bestIndex >= 0) bestIndex else null
    }

    private fun blockText(block: com.apmlearning.quiz.data.MaterialBlock): String = when (block) {
        is com.apmlearning.quiz.data.MaterialBlock.Heading -> ""
        is com.apmlearning.quiz.data.MaterialBlock.Paragraph -> normalise(block.text)
        is com.apmlearning.quiz.data.MaterialBlock.Bullets -> block.items.joinToString(" ") { normalise(it) }
    }

    private fun normalise(s: String): String = s
        .replace('’', '\'')
        .replace('‘', '\'')
        .replace('“', '"')
        .replace('”', '"')
        .replace('–', '-')
        .replace('—', '-')
        .replace("•", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
        .lowercase()

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
