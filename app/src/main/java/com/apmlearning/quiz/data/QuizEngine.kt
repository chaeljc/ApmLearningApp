package com.apmlearning.quiz.data

import kotlin.random.Random

/**
 * Builds quiz sessions: selects questions that span the syllabus, shuffles them,
 * and places the correct answer into an A–E slot using the mandatory
 * randomisation-and-balancing rule from the source material.
 */
object QuizEngine {

    private val LETTERS = listOf('A', 'B', 'C', 'D', 'E')

    fun build(bank: List<RawQuestion>, requestedSize: Int, topic: String?): QuizSession {
        val pool = if (topic == null) bank else bank.filter { it.topic == topic }
        val selected = selectQuestions(pool, requestedSize)
        return QuizSession(assignLetters(selected), topic)
    }

    /**
     * Picks up to [size] questions. Draws round-robin across topics so the test
     * spans the whole document, then shuffles the final order so the starting
     * question is random.
     */
    private fun selectQuestions(pool: List<RawQuestion>, size: Int): List<RawQuestion> {
        if (pool.isEmpty()) return emptyList()
        val target = minOf(size, pool.size)

        val buckets = pool.groupBy { it.topic }
            .map { (_, qs) -> qs.shuffled().toMutableList() }
            .toMutableList()
        buckets.shuffle()

        val result = ArrayList<RawQuestion>(target)
        var index = 0
        while (result.size < target && buckets.any { it.isNotEmpty() }) {
            val bucket = buckets[index % buckets.size]
            if (bucket.isNotEmpty()) result.add(bucket.removeAt(bucket.size - 1))
            index++
        }
        return result.shuffled()
    }

    /**
     * Mandatory rule: keep a per-letter counter, place each correct answer in a
     * lowest-count letter (random tie-break), never use B for the first question,
     * and never let the gap between most- and least-used letters exceed 1.
     */
    private fun assignLetters(questions: List<RawQuestion>): List<QuizQuestion> {
        val counts = LETTERS.associateWith { 0 }.toMutableMap()
        val out = ArrayList<QuizQuestion>(questions.size)

        questions.forEachIndexed { index, question ->
            val lowest = counts.values.min()
            var candidates = LETTERS.filter { counts.getValue(it) == lowest }
            if (index == 0) {
                candidates = candidates.filter { it != 'B' }
                if (candidates.isEmpty()) candidates = LETTERS.filter { it != 'B' }
            }
            val chosen = candidates[Random.nextInt(candidates.size)]

            val distractors = question.distractors.shuffled()
            var distractorIndex = 0
            val options = LETTERS.map { letter ->
                if (letter == chosen) {
                    Option(letter, question.correct, isCorrect = true)
                } else {
                    Option(letter, distractors[distractorIndex++], isCorrect = false)
                }
            }

            counts[chosen] = counts.getValue(chosen) + 1
            out.add(QuizQuestion(question, options, chosen))
        }
        return out
    }
}
