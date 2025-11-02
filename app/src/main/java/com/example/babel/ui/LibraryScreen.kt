package com.example.babel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.BookLoader
import com.example.babel.data.UserDataLoader
import com.example.babel.models.Book
import com.example.babel.ui.components.*

@Composable
fun LibraryScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val user = remember { UserDataLoader.loadSampleUser(context) }
    val books = remember { BookLoader.loadSampleBooks(context) }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = colorScheme.background,
        contentColor = colorScheme.onBackground,
        bottomBar = {
            BottomBar(
                navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Screen title
                Text(
                    text = "Your Library",
                    style = typography.headlineSmall,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LibraryShelf(
                    title = "Currently Reading",
                    bookIds = user.currentlyReading,
                    allBooks = books,
                    navController = navController
                )

                LibraryShelf(
                    title = "Want to Read",
                    bookIds = user.wantToRead,
                    allBooks = books,
                    navController = navController
                )

                LibraryShelf(
                    title = "Finished Reading",
                    bookIds = user.finishedReading,
                    allBooks = books,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun LibraryShelf(
    title: String,
    bookIds: List<Int>,
    allBooks: List<Book>,
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Gradient uses accent tones, not base background
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(colorScheme.primary, colorScheme.secondary)
    )

    // Shelf title
    Text(
        text = title,
        style = typography.titleMedium,
        color = colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    // Decorative divider
    Box(
        modifier = Modifier
            .height(2.dp)
            .fillMaxWidth(0.4f)
            .clip(RoundedCornerShape(4.dp))
            .background(gradientBrush)
            .padding(bottom = 8.dp)
    )

    val shelfBooks = allBooks.filter { it.id in bookIds }

    if (shelfBooks.isEmpty()) {
        Text(
            text = "No books yet...",
            color = colorScheme.onSurface.copy(alpha = 0.6f),
            style = typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        BookCarousel(
            title = "", // Title already shown above
            books = shelfBooks,
            navController = navController,
            showTitle = false,
            cardWidth = 160,
            cardHeight = 230
        )
    }
}
