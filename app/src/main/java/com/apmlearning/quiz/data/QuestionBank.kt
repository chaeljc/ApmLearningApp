package com.apmlearning.quiz.data

import android.content.Context
import org.json.JSONArray

/** Loads the bundled question bank from assets/questions.json. */
object QuestionBank {

    fun load(context: Context): List<RawQuestion> {
        val text = context.assets.open("questions.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val array = JSONArray(text)
        val questions = ArrayList<RawQuestion>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val distractorsJson = obj.getJSONArray("distractors")
            val distractors = ArrayList<String>(distractorsJson.length())
            for (j in 0 until distractorsJson.length()) {
                distractors.add(distractorsJson.getString(j))
            }
            questions.add(
                RawQuestion(
                    id = obj.getString("id"),
                    topic = obj.getString("topic"),
                    question = obj.getString("question"),
                    correct = obj.getString("correct"),
                    distractors = distractors,
                    hint = obj.getString("hint"),
                    explanation = obj.getString("explanation"),
                    source = obj.getString("source"),
                )
            )
        }
        return questions
    }
}
