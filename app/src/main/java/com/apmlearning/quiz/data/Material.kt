package com.apmlearning.quiz.data

import android.content.Context
import org.json.JSONArray

/** One block of learning material: a heading, a paragraph, or a bullet list. */
sealed interface MaterialBlock {
    data class Heading(val text: String) : MaterialBlock
    data class Paragraph(val text: String) : MaterialBlock
    data class Bullets(val items: List<String>) : MaterialBlock
}

/** A readable section of the course learning material. */
data class MaterialSection(
    val title: String,
    val blocks: List<MaterialBlock>,
)

/** Loads the bundled learning material from assets/material.json. */
object MaterialLibrary {

    fun load(context: Context): List<MaterialSection> {
        val text = context.assets.open("material.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val array = JSONArray(text)
        val sections = ArrayList<MaterialSection>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val blocksJson = obj.getJSONArray("blocks")
            val blocks = ArrayList<MaterialBlock>(blocksJson.length())
            for (j in 0 until blocksJson.length()) {
                val block = blocksJson.getJSONObject(j)
                when {
                    block.has("h") -> blocks.add(MaterialBlock.Heading(block.getString("h")))
                    block.has("p") -> blocks.add(MaterialBlock.Paragraph(block.getString("p")))
                    block.has("b") -> {
                        val itemsJson = block.getJSONArray("b")
                        val items = ArrayList<String>(itemsJson.length())
                        for (k in 0 until itemsJson.length()) {
                            items.add(itemsJson.getString(k))
                        }
                        blocks.add(MaterialBlock.Bullets(items))
                    }
                }
            }
            sections.add(MaterialSection(obj.getString("title"), blocks))
        }
        return sections
    }
}
