package com.example.babel.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.BookLoader
import com.example.babel.data.UserReadingActivityLoader
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BottomBar
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.math.roundToInt

@Composable
fun StatsScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    val allBooks = remember { BookLoader.loadSampleBooks(context) }
    val userActivity = remember { UserReadingActivityLoader.loadUserActivity(context) }
    val finishedBooks = userActivity.filter { it.shelf == "Finished Reading" }

    val totalPages = finishedBooks.sumOf { it.progressValue ?: 0 }
    val totalBooks = finishedBooks.size

    // --- Dynamic Reading Goal ---
    var goalType by remember { mutableStateOf("Pages") } // "Pages" or "Books"
    var goalValue by remember { mutableStateOf(20000) }
    val currentProgress = if (goalType == "Pages") totalPages.toFloat() / goalValue else totalBooks.toFloat() / goalValue

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    // --- Monthly Completion Data ---
    val monthlyData = finishedBooks.mapNotNull { activity ->
        try {
            activity.endDate?.let {
                val date = LocalDate.parse(it, formatter)
                val monthKey = date.month.name.take(3)
                monthKey to 1
            }
        } catch (_: DateTimeParseException) {
            null
        }
    }.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }

    val sortedMonths = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
    val sortedMonthlyData = sortedMonths.associateWith { monthlyData[it] ?: 0 }

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            AnimatedBackground()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatsHeader()
                Spacer(Modifier.height(24.dp))

                QuickStatsRow(totalPages = totalPages, finishedCount = totalBooks)
                Spacer(Modifier.height(32.dp))

                // --- Reading Goals Section ---
                ReadingGoalsSection(
                    goalType = goalType,
                    goalValue = goalValue,
                    currentProgress = currentProgress,
                    onGoalChange = { newType, newGoal ->
                        goalType = newType
                        goalValue = newGoal
                    }
                )

                Spacer(Modifier.height(40.dp))

                MonthlyBarChart(sortedMonthlyData)
                Spacer(Modifier.height(40.dp))

                GenrePieChart(finishedBooks.mapNotNull { fb -> allBooks.find { it.id == fb.bookId } })
                Spacer(Modifier.height(40.dp))

                PublishedYearScatter(finishedBooks.mapNotNull { fb -> allBooks.find { it.id == fb.bookId } })
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun StatsHeader() {
    val colorScheme = MaterialTheme.colorScheme
    val gradient = Brush.horizontalGradient(listOf(colorScheme.primary, colorScheme.secondary, colorScheme.tertiary))
    var quote by remember { mutableStateOf("Every page turned is a world discovered.") }

    LaunchedEffect(Unit) {
        val quotes = listOf(
            "Every page turned is a world discovered.",
            "You’ve written your own story in the stars.",
            "Keep reading — your next adventure awaits.",
            "Books are the constellations of the soul."
        )
        while (true) {
            delay(4000)
            quote = quotes.random()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(24.dp)).background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Your Reading Journey", color = colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = quote, color = colorScheme.onPrimary.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
fun QuickStatsRow(totalPages: Int, finishedCount: Int) {
    val colorScheme = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        QuickStatCard("Books Finished", finishedCount.toString(), colorScheme.primaryContainer)
        QuickStatCard("Pages Read", totalPages.toString(), colorScheme.secondaryContainer)
    }
}

@Composable
fun QuickStatCard(title: String, value: String, background: Color) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Box(
        modifier = Modifier.width(150.dp).height(90.dp).clip(RoundedCornerShape(16.dp))
            .background(background.copy(alpha = 0.85f)).padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = typography.titleLarge, color = colorScheme.onPrimaryContainer)
            Text(title, style = typography.labelSmall, color = colorScheme.onPrimaryContainer)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingGoalsSection(
    goalType: String,
    goalValue: Int,
    currentProgress: Float,
    onGoalChange: (String, Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var expanded by remember { mutableStateOf(false) }
    var newGoalValue by remember { mutableStateOf(goalValue.toString()) }

    Text(
        "Reading Goals",
        style = typography.titleMedium,
        color = colorScheme.onBackground
    )
    Spacer(Modifier.height(12.dp))

    // Type Toggle
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = goalType == "Pages",
            onClick = { onGoalChange("Pages", goalValue) },
            label = {
                Text(
                    "Pages",
                    style = typography.labelSmall,
                    color = colorScheme.onPrimaryContainer
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = colorScheme.primaryContainer.copy(alpha = 0.4f),
                containerColor = Color.Transparent,
                selectedLabelColor = colorScheme.onPrimaryContainer,
                labelColor = colorScheme.onPrimaryContainer
            )
        )
        FilterChip(
            selected = goalType == "Books",
            onClick = { onGoalChange("Books", goalValue) },
            label = {
                Text(
                    "Books",
                    style = typography.labelSmall,
                    color = colorScheme.onPrimaryContainer
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = colorScheme.primaryContainer.copy(alpha = 0.4f),
                containerColor = Color.Transparent,
                selectedLabelColor = colorScheme.onPrimaryContainer,
                labelColor = colorScheme.onPrimaryContainer
            )
        )
    }

    Spacer(Modifier.height(12.dp))

    // Update Goal Field
    OutlinedTextField(
        value = newGoalValue,
        onValueChange = { newGoalValue = it.filter { ch -> ch.isDigit() } },
        label = { Text("Set Goal (${goalType.lowercase()})") },
        trailingIcon = {
            TextButton(onClick = { onGoalChange(goalType, newGoalValue.toIntOrNull() ?: goalValue) }) {
                Text("Update", color = colorScheme.primary)
            }
        },
        modifier = Modifier.fillMaxWidth(0.7f)
    )

    Spacer(Modifier.height(20.dp))

    GoalProgressChart(currentProgress, goalValue, goalType)
}

@Composable
fun GoalProgressChart(progress: Float, goal: Int, goalType: String) {
    val colorScheme = MaterialTheme.colorScheme
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val sweepAngle = progress.coerceIn(0f, 1f) * 360f
            drawArc(
                color = colorScheme.primary.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
            )
            drawArc(
                color = colorScheme.secondary,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(progress * 100).roundToInt()}%", color = colorScheme.onBackground)
            Text("of $goal $goalType goal", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun MonthlyBarChart(data: Map<String, Int>) {
    val colorScheme = MaterialTheme.colorScheme
    val months = data.keys.toList()
    val values = months.map { data[it] ?: 0 }
    val maxValue = (values.maxOrNull() ?: 1).toFloat()

    Text("Books Finished Per Month", style = MaterialTheme.typography.titleMedium, color = colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))

    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val barWidth = size.width / (months.size * 2)
        values.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * size.height * 0.8f
            drawRoundRect(
                color = colorScheme.primary,
                topLeft = Offset((index * barWidth * 2) + barWidth / 2, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }
    }
}

@Composable
fun GenrePieChart(finishedBooks: List<com.example.babel.models.Book>) {
    val colorScheme = MaterialTheme.colorScheme
    val genreCounts = finishedBooks.flatMap { it.genre_id }.groupingBy { it }.eachCount()
    val total = genreCounts.values.sum().toFloat()

    Text("Genre Distribution", style = MaterialTheme.typography.titleMedium, color = colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))

    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = -90f
        genreCounts.entries.forEachIndexed { index, entry ->
            val sweep = (entry.value / total) * 360f
            drawArc(
                color = listOf(
                    colorScheme.primary,
                    colorScheme.secondary,
                    colorScheme.tertiary,
                    colorScheme.primaryContainer,
                    colorScheme.secondaryContainer
                )[index % 5],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )
            startAngle += sweep
        }
    }
}

@Composable
fun PublishedYearScatter(finishedBooks: List<com.example.babel.models.Book>) {
    val colorScheme = MaterialTheme.colorScheme
    val years = finishedBooks.mapNotNull { it.publishedDate?.takeLast(4)?.toIntOrNull() }.sorted()
    if (years.isEmpty()) return

    val minYear = years.minOrNull() ?: 1900
    val maxYear = years.maxOrNull() ?: 2025

    Text("Literary Timeline", style = MaterialTheme.typography.titleMedium, color = colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))

    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        years.forEach { year ->
            val x = ((year - minYear).toFloat() / (maxYear - minYear)) * size.width
            val y = size.height - (Random().nextFloat() * size.height * 0.6f)
            drawCircle(color = colorScheme.secondary, radius = 6f, center = Offset(x, y))
        }
    }
}
