package com.apmlearning.quiz.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apmlearning.quiz.QuizViewModel
import com.apmlearning.quiz.data.MaterialBlock
import com.apmlearning.quiz.data.MaterialSection
import com.apmlearning.quiz.ui.ActionRowCard
import com.apmlearning.quiz.ui.Badge
import com.apmlearning.quiz.ui.GradientHeader

@Composable
fun MaterialListScreen(vm: QuizViewModel) {
    BackHandler { vm.backToHomeFromMaterial() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        GradientHeader(
            title = "Learning material",
            subtitle = "Read the course content, section by section",
            onBack = { vm.backToHomeFromMaterial() },
        )

        Column(modifier = Modifier.padding(20.dp)) {
            vm.material.forEachIndexed { index, section ->
                ActionRowCard(
                    title = section.title,
                    subtitle = sectionPreview(section),
                    onClick = { vm.openSection(section) },
                    leading = { Badge(text = (index + 1).toString()) },
                )
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun MaterialDetailScreen(vm: QuizViewModel) {
    BackHandler { vm.backToMaterialList() }

    val section = vm.selectedSection ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        GradientHeader(
            title = section.title,
            subtitle = "APM Foundation course material",
            onBack = { vm.backToMaterialList() },
        )

        Column(modifier = Modifier.padding(20.dp)) {
            section.blocks.forEachIndexed { index, block ->
                when (block) {
                    is MaterialBlock.Heading -> {
                        if (index != 0) Spacer(Modifier.height(20.dp))
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    is MaterialBlock.Paragraph -> {
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    is MaterialBlock.Bullets -> {
                        block.items.forEach { item ->
                            Row(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun sectionPreview(section: MaterialSection): String {
    val firstParagraph = section.blocks
        .firstOrNull { it is MaterialBlock.Paragraph } as? MaterialBlock.Paragraph
    return firstParagraph?.text ?: "Tap to read this section"
}
