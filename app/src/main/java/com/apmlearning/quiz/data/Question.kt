package com.apmlearning.quiz.data

/** A question exactly as authored in the bundled question bank. */
data class RawQuestion(
    val id: String,
    val topic: String,
    val question: String,
    val correct: String,
    val distractors: List<String>,
    val hint: String,
    val explanation: String,
    val source: String,
)

/** One A–E option as presented to the user in a quiz. */
data class Option(
    val letter: Char,
    val text: String,
    val isCorrect: Boolean,
)

/** A question prepared for a quiz session, with options placed in A–E slots. */
data class QuizQuestion(
    val raw: RawQuestion,
    val options: List<Option>,
    val correctLetter: Char,
)

/** A built quiz ready to be played. [topic] is null for a whole-syllabus quiz. */
data class QuizSession(
    val questions: List<QuizQuestion>,
    val topic: String?,
)
