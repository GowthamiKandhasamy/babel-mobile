package com.example.babel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.babel.data.local.BookLoader
import com.example.babel.ui.components.AddBookDialog
import com.example.babel.ui.components.BookCarousel
import com.example.babel.ui.components.TopBar
import com.example.babel.ui.components.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(navController: NavController, bookId: Int) {
    val context = LocalContext.current
    val books = remember { BookLoader.loadSampleBooks(context) }
    val book = books.find { it.id == bookId.toLong() }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedShelf by remember { mutableStateOf("Finished Reading?") }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = colorScheme . background
    ) { paddingValues ->
        book?.let {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorScheme.background)
            ) {
                item {
                    // --- Hero Image Section with Overlay Title ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(book.coverImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = book.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient starts lower and is lighter
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            colorScheme.background.copy(alpha = 0.95f)
                                        ),
                                        startY = 250f
                                    )
                                )
                        )

                        // Title and author on gradient
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = book.title,
                                style = typography.headlineSmall.copy(
                                    color = colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = book.authors.joinToString(", "),
                                style = typography.bodyMedium.copy(
                                    color = colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // --- Rating Summary ---
                        RatingSummaryRow(average = 4.2f, totalRatings = 2350, totalReviews = 420)
                        Spacer(Modifier.height(20.dp))

                        // --- Add to Shelf (Dropdown Style) ---
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = showAddDialog,
                                onExpandedChange = { showAddDialog = !showAddDialog }
                            ) {
                                OutlinedTextField(
                                    value = selectedShelf,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Add to Shelf") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAddDialog)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .width(220.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = colorScheme.onSurface,
                                        unfocusedTextColor = colorScheme.onSurface,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedBorderColor = colorScheme.primary,
                                        unfocusedBorderColor = colorScheme.secondary
                                    )
                                )
                            }
                        }

                        if (showAddDialog) {
                            AddBookDialog(
                                books = books,
                                onDismiss = {
                                    showAddDialog = false
                                    selectedShelf = "Currently Reading"
                                },
                                onSave = { bookId, shelf ->
                                    // TODO: integrate with LibraryViewModel
                                    // For now, you can just log or show a toast until backend connected
                                    showAddDialog = false
                                }
                            )
                        }


                        Spacer(Modifier.height(16.dp))

                        // --- Rate This Book ---
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorScheme.tertiaryContainer)
                                    .clickable { /* TODO: Rating Dialog */ }
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "Rate this Book",
                                    style = typography.bodyMedium.copy(color = colorScheme.primary)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // --- Preview ---
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorScheme.tertiaryContainer)
                                    .clickable { /* TODO: Rating Dialog */ }
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "Preview",
                                    style = typography.bodyMedium.copy(color = colorScheme.onTertiaryContainer)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // --- Description ---
                        Text(
                            text = "Description",
                            style = typography.titleMedium.copy(
                                color = colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = book.subtitle ?: "No description available.",
                            style = typography.bodyMedium.copy(color = colorScheme.onBackground),
                            maxLines = 8,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "${book.pageCount ?: 0} pages • First Published on ${book.publishedDate ?: "Unknown"} • ISBN13 ${book.isbn13 ?: "N/A"}",
                            style = typography.labelSmall.copy(color = colorScheme.onSurfaceVariant)
                        )

                        Spacer(Modifier.height(24.dp))

                        // --- Rating Details (Container) ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(12.dp)
                        ) {
                            RatingDetailsSection()
                        }

                        Spacer(Modifier.height(28.dp))

                        // --- Community Reviews ---
                        CommunityReviewsSection()

                        Spacer(Modifier.height(28.dp))

                        // --- Carousels ---
                        BookCarousel(
                            title = "Other Books by this Author",
                            books = books.shuffled().take(6),
                            navController = navController
                        )
                        Spacer(Modifier.height(24.dp))
                        BookCarousel(
                            title = "Readers Also Enjoyed",
                            books = books.shuffled().take(6),
                            navController = navController
                        )
                        Spacer(Modifier.height(60.dp))
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Book not found.", color = colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun RatingSummaryRow(average: Float, totalRatings: Int, totalReviews: Int) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val filledStars = average.toInt()
    val emptyStars = 5 - filledStars

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        repeat(filledStars) {
            Icon(Icons.Filled.Star, null, tint = colorScheme.tertiary)
        }
        repeat(emptyStars) {
            Icon(Icons.Outlined.Star, null, tint = colorScheme.onSurfaceVariant)
        }
        Text(
            text = String.format("%.1f", average),
            style = typography.bodyMedium.copy(color = colorScheme.onSurface),
            modifier = Modifier.padding(start = 4.dp)
        )
        Text(
            text = " • $totalRatings ratings • $totalReviews reviews",
            color = colorScheme.onSurfaceVariant,
            style = typography.labelSmall,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
fun RatingDetailsSection() {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val ratings = listOf(5, 4, 3, 2, 1)
    val percentages = listOf(60, 20, 10, 7, 3)
    val gradient = Brush.horizontalGradient(
        listOf(colorScheme.primaryContainer, colorScheme.tertiaryContainer)
    )

    Column {
        Text(
            text = "Rating Details",
            style = typography.titleMedium.copy(color = colorScheme.secondary)
        )
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ratings.forEachIndexed { index, rating ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$rating★", color = colorScheme.onSurface, modifier = Modifier.width(40.dp))
                    Box(
                        modifier = Modifier
                            .height(10.dp)
                            .width((percentages[index] * 2).dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(gradient)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("${percentages[index]}%", color = colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun CommunityReviewsSection() {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val allReviews = remember {
        listOf(
            Triple("Elara", 5, "Absolutely mesmerizing — couldn’t put it down."),
            Triple("Rowan", 4, "Great read, though pacing dipped in the middle."),
            Triple("Mira", 3, "Enjoyable but overhyped for me."),
            Triple("Jon", 4, "Strong character work and themes."),
            Triple("Kael", 5, "Would recommend to anyone!")
        )
    }

    val maxVisible = 3
    var showAll by remember { mutableStateOf(false) }
    val visibleReviews = if (showAll) allReviews else allReviews.take(maxVisible)

    Text(
        text = "Community Reviews",
        style = typography.titleMedium.copy(color = colorScheme.secondary)
    )
    Spacer(Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        visibleReviews.forEach { (user, rating, text) ->
            ReviewCard(user = user, rating = rating, text = text)
        }
        if (!showAll && allReviews.size > maxVisible) {
            Text(
                text = "Show more...",
                color = colorScheme.primary,
                style = typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { showAll = true }
            )
        }
    }
}

@Composable
fun ReviewCard(user: String, rating: Int, text: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val stars = (1..5).map { it <= rating }
    val gradient = Brush.horizontalGradient(
        listOf(colorScheme.primaryContainer, colorScheme.tertiaryContainer)
    )

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(user.first().toString(), color = colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(user, style = typography.bodyMedium.copy(color = colorScheme.onSurface))
                Row {
                    stars.forEach { filled ->
                        Icon(
                            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (filled) colorScheme.tertiary else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            style = typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.ThumbUp, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
            Icon(Icons.Outlined.Create, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
        }
    }
}
