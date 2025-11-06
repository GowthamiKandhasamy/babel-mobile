package com.example.babel.ui.screens

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.local.BookLoader
import com.example.babel.data.local.UserReadingActivityLoader
import com.example.babel.data.models.Book
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BottomBar
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babel.ui.viewmodel.StatsViewModel


@Composable
fun StatsScreen(navController: NavController, uid: String = "demo_uid") {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    val localBooks = remember { BookLoader.loadSampleBooks(context) }
    val localUserActivity = remember { UserReadingActivityLoader.loadUserActivity(context) }

    val finishedBooks = localUserActivity.filter { it.shelf == "Finished Reading" }
    val totalPages = finishedBooks.sumOf { it.progressValue ?: 0 }
    val totalBooks = finishedBooks.size

    val viewModel: StatsViewModel = viewModel()
    val statsState by viewModel.uiState.collectAsState()

    // fallback to local data if stats not loaded
    val stats = statsState.stats

    // goal logic
    var goalType by remember { mutableStateOf("Pages") }
    var goalValue by remember { mutableStateOf(stats?.goalPages ?: 20000) }
    val currentProgress = if (goalType == "Pages")
        totalPages.toFloat() / goalValue
    else totalBooks.toFloat() / (stats?.goalBooks ?: 50)

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    // --- Monthly Data ---
    val monthlyData = finishedBooks.mapNotNull {
        try {
            it.endDate?.let { d ->
                val date = LocalDate.parse(d, formatter)
                val key = date.month.name.take(3)
                key to 1
            }
        } catch (_: DateTimeParseException) { null }
    }.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }

    val sortedMonths = listOf("JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC")
    val sortedMonthlyData = sortedMonths.associateWith { monthlyData[it] ?: 0 }

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatsHeader()
                Spacer(Modifier.height(24.dp))
                QuickStatsRow(totalPages = totalPages, finishedCount = totalBooks)
                Spacer(Modifier.height(32.dp))
                ReadingGoalsSection(
                    goalType = goalType,
                    goalValue = goalValue,
                    currentProgress = currentProgress,
                    onGoalChange = { newType, newGoal ->
                        goalType = newType
                        goalValue = newGoal
                        viewModel.updateGoal(uid,
                            if (goalType == "Pages") newGoal else null,
                            if (goalType == "Books") newGoal else null)
                    }
                )
                Spacer(Modifier.height(40.dp))
                MonthlyBarChart(sortedMonthlyData)
                Spacer(Modifier.height(40.dp))
                GenrePieChart(finishedBooks.mapNotNull { fb -> localBooks.find { it.id == fb.bookId } })
                Spacer(Modifier.height(40.dp))
                PublishedYearScatter(finishedBooks.mapNotNull { fb -> localBooks.find { it.id == fb.bookId } })
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
                style = Stroke(width = 18f)
            )
            drawArc(
                color = colorScheme.secondary,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 18f)
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
fun GenrePieChart(finishedBooks: List<Book>) {
    val colorScheme = MaterialTheme.colorScheme
    // Assuming genre_id is a list of Ints that correspond to genre names/IDs
    // We'll group by the Int and count occurrences.
    val genreCounts = finishedBooks.flatMap { it.genreId }.groupingBy { it }.eachCount()
    val total = genreCounts.values.sum().toFloat()
    val colors = listOf(colorScheme.primary, colorScheme.secondary, colorScheme.tertiary, colorScheme.primaryContainer, colorScheme.secondaryContainer)

    Text("Genre Distribution", style = MaterialTheme.typography.titleMedium, color = colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))

    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = 0f
        genreCounts.entries.forEachIndexed { index, entry ->
            val sweepAngle = (entry.value / total) * 360f
            drawArc(
                color = colors[index % colors.size], // Cycle through a list of colors
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}


@Composable
fun PublishedYearScatter(finishedBooks: List<Book>) {
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
