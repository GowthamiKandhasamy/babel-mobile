package com.example.babel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.BookLoader
import com.example.babel.data.UserDataLoader
import com.example.babel.models.Book
import com.example.babel.ui.components.AddBookDialog
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BookCarousel
import com.example.babel.ui.components.BottomBar

@Composable
fun LibraryScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val user = remember { UserDataLoader.loadSampleUser(context) }
    val books = remember { BookLoader.loadSampleBooks(context) }
    val scrollState = rememberScrollState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colorScheme.background,
        contentColor = colorScheme.onBackground,
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Book")
            }
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

                Spacer(modifier = Modifier.height(10.dp))

                LibraryShelf(
                    title = "Want to Read",
                    bookIds = user.wantToRead,
                    allBooks = books,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(10.dp))

                LibraryShelf(
                    title = "Finished Reading",
                    bookIds = user.finishedReading,
                    allBooks = books,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(80.dp))
            }

            if (showAddDialog) {
                AddBookDialog(
                    books = books,
                    onDismiss = { showAddDialog = false }
                )
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
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(colorScheme.primary, colorScheme.secondary)
    )

    Text(
        text = title,
        style = typography.titleMedium,
        color = colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 8.dp)
    )

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
        Spacer(modifier = Modifier.height(10.dp))
        BookCarousel(
            title = "",
            books = shelfBooks,
            navController = navController,
            showTitle = false,
            cardWidth = 160,
            cardHeight = 230
        )
    }
}