package com.apmlearning.quiz.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apmlearning.quiz.QuizViewModel
import com.apmlearning.quiz.data.Option
import com.apmlearning.quiz.data.QuizQuestion
import com.apmlearning.quiz.ui.theme.Amber
import com.apmlearning.quiz.ui.theme.CorrectGreen
import com.apmlearning.quiz.ui.theme.CorrectGreenBright
import com.apmlearning.quiz.ui.theme.QuestionTextStyle
import com.apmlearning.quiz.ui.theme.WrongRed
import com.apmlearning.quiz.ui.theme.WrongRedBright

@Composable
fun QuizScreen(vm: QuizViewModel) {
    val question = vm.currentQuestion ?: return
    var showQuitDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    BackHandler { showQuitDialog = true }

    // Scroll back to the top whenever the user moves to a new question.
    LaunchedEffect(vm.current) { scrollState.scrollTo(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        QuizHeader(vm) { showQuitDialog = true }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(20.dp),
        ) {
            Text(
                text = question.raw.question,
                style = QuestionTextStyle,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(20.dp))

            question.options.forEach { option ->
                OptionCard(
                    option = option,
                    answered = vm.isAnswered,
                    selectedLetter = vm.selectedLetter,
                    onClick = { vm.selectAnswer(option.letter) },
                )
                Spacer(Modifier.height(10.dp))
            }

            AnimatedVisibility(visible = vm.hintShown && !vm.isAnswered) {
                HintCard(question.raw.hint)
            }

            AnimatedVisibility(visible = vm.isAnswered) {
                FeedbackCard(vm, question)
            }

            Spacer(Modifier.height(8.dp))
        }

        QuizBottomBar(vm)
    }

    if (showQuitDialog) {
        QuitDialog(
            score = vm.score,
            answered = vm.answered,
            onConfirm = { showQuitDialog = false; vm.quit() },
            onDismiss = { showQuitDialog = false },
        )
    }
}

@Composable
private fun QuizHeader(vm: QuizViewModel, onQuit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(com.apmlearning.quiz.ui.HeaderGradient)
            .padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "QUESTION ${vm.current + 1} OF ${vm.total}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = vm.sessionTopic ?: "Whole syllabus",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.72f),
                    )
                }
                ScorePill(score = vm.score, answered = vm.answered)
                IconButton(onClick = onQuit) {
                    Icon(Icons.Filled.Close, contentDescription = "Quit quiz", tint = Color.White)
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (vm.current + 1).toFloat() / vm.total.coerceAtLeast(1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Amber,
                trackColor = Color.White.copy(alpha = 0.20f),
            )
        }
    }
}

@Composable
private fun ScorePill(score: Int, answered: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "$score / $answered",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
        )
    }
}

@Composable
private fun OptionCard(
    option: Option,
    answered: Boolean,
    selectedLetter: Char?,
    onClick: () -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val green = if (dark) CorrectGreenBright else CorrectGreen
    val red = if (dark) WrongRedBright else WrongRed

    val isChosen = option.letter == selectedLetter
    val revealCorrect = answered && option.isCorrect
    val revealWrong = answered && isChosen && !option.isCorrect
    val faded = answered && !revealCorrect && !revealWrong

    val accent = when {
        revealCorrect -> green
        revealWrong -> red
        else -> MaterialTheme.colorScheme.outline
    }
    val container = when {
        revealCorrect -> green.copy(alpha = 0.14f)
        revealWrong -> red.copy(alpha = 0.14f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        enabled = !answered,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (faded) 0.55f else 1f)
            .border(
                width = if (revealCorrect || revealWrong) 2.dp else 1.dp,
                color = accent,
                shape = RoundedCornerShape(16.dp),
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LetterCircle(
                letter = option.letter,
                revealCorrect = revealCorrect,
                revealWrong = revealWrong,
                green = green,
                red = red,
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (revealCorrect) {
                Icon(Icons.Filled.Check, contentDescription = "Correct answer", tint = green)
            } else if (revealWrong) {
                Icon(Icons.Filled.Close, contentDescription = "Your answer", tint = red)
            }
        }
    }
}

@Composable
private fun LetterCircle(
    letter: Char,
    revealCorrect: Boolean,
    revealWrong: Boolean,
    green: Color,
    red: Color,
) {
    val bg = when {
        revealCorrect -> green
        revealWrong -> red
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val fg = when {
        revealCorrect || revealWrong -> Color.White
        else -> MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(50))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
        )
    }
}

@Composable
private fun HintCard(hint: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Amber.copy(alpha = 0.16f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Icon(
                Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = Amber,
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Hint",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard(vm: QuizViewModel, question: QuizQuestion) {
    val dark = isSystemInDarkTheme()
    val green = if (dark) CorrectGreenBright else CorrectGreen
    val red = if (dark) WrongRedBright else WrongRed

    val correct = !vm.toldAnswer && vm.selectedLetter == question.correctLetter
    val accent = if (correct) green else if (vm.toldAnswer) Amber else red
    val title = when {
        correct -> "Correct!"
        vm.toldAnswer -> "Here's the answer"
        else -> "Not quite"
    }
    val correctText = question.options.first { it.isCorrect }.text

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.13f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (correct) Icons.Filled.Check else Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = null,
                    tint = accent,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            if (!correct) {
                Text(
                    text = "The correct answer is ${question.correctLetter} — $correctText",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(
                text = question.raw.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(12.dp))
            SourceQuote(question.raw.source, accent)
        }
    }
}

@Composable
private fun SourceQuote(source: String, accent: Color) {
    Column {
        Text(
            text = "FROM THE APM FOUNDATION MATERIAL",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "“$source”",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun QuizBottomBar(vm: QuizViewModel) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        ) {
            if (!vm.isAnswered) {
                OutlinedButton(
                    onClick = { vm.showHint() },
                    enabled = !vm.hintShown,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Outlined.Lightbulb, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (vm.hintShown) "Hint shown above" else "Give me a hint")
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { vm.tellMe() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Tell me, I don't know")
                }
            } else {
                Button(
                    onClick = { vm.next() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        if (vm.isLastQuestion) "See my results" else "Next question",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun QuitDialog(
    score: Int,
    answered: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("End this quiz?") },
        text = {
            Text(
                "You've answered $answered question(s) so far and scored $score. " +
                    "You can see your full results, or carry on.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("End & see results") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Keep going") }
        },
    )
}
