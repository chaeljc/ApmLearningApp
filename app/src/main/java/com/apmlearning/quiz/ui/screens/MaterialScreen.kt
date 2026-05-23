package com.apmlearning.quiz.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    BackHandler { vm.closeMaterialDetail() }

    val section = vm.selectedSection ?: return
    val fromQuiz = vm.materialOpenedFromQuiz
    val highlight = vm.highlightBlockIndex

    val listState = rememberLazyListState()
    LaunchedEffect(section, highlight) {
        if (highlight != null && highlight in section.blocks.indices) {
            listState.animateScrollToItem(highlight)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        GradientHeader(
            title = section.title,
            subtitle = if (fromQuiz && highlight != null) {
                "Scrolled to the passage your question came from"
            } else if (fromQuiz) {
                "Course material · linked from your quiz"
            } else {
                "APM Foundation course material"
            },
            onBack = { vm.closeMaterialDetail() },
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(section.blocks) { index, block ->
                BlockItem(
                    block = block,
                    isFirst = index == 0,
                    highlighted = index == highlight,
                )
            }

            if (fromQuiz) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { vm.closeMaterialDetail() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Text(
                            "Back to your quiz",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockItem(block: MaterialBlock, isFirst: Boolean, highlighted: Boolean) {
    if (highlighted) {
        HighlightWrapper { BlockContent(block, isFirst = true) }
    } else {
        BlockContent(block, isFirst = isFirst)
    }
}

@Composable
private fun HighlightWrapper(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { content() }
    }
}

@Composable
private fun BlockContent(block: MaterialBlock, isFirst: Boolean) {
    when (block) {
        is MaterialBlock.Heading -> {
            Column {
                if (!isFirst) Spacer(Modifier.height(12.dp))
                Text(
                    text = block.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        is MaterialBlock.Paragraph -> {
            Text(
                text = block.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        is MaterialBlock.Bullets -> {
            Column {
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
            }
        }
    }
}

private fun sectionPreview(section: MaterialSection): String {
    val firstParagraph = section.blocks
        .firstOrNull { it is MaterialBlock.Paragraph } as? MaterialBlock.Paragraph
    return firstParagraph?.text ?: "Tap to read this section"
}
