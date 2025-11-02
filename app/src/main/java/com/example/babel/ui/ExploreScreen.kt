package com.example.babel.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.BookLoader
import com.example.babel.ui.components.*

@Composable
fun ExploreScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val allBooks = remember { BookLoader.loadSampleBooks(context) }

    Scaffold(
        topBar = {
            TopBar(
                navController,
            )
        },
        bottomBar = {
            BottomBar(
                navController,
            )
        },
        containerColor = colorScheme.background,
        contentColor = colorScheme.onBackground
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
                // 1. Location-Based
                BookCarousel(
                    title = "Popular in Your Location",
                    books = allBooks.shuffled().take(6),
                    navController = navController
                )
                Spacer(modifier = Modifier.height(28.dp))

                // 2. Personalized
                BookCarousel(
                    title = "Readers Like You Loved",
                    books = allBooks.shuffled().take(6),
                    navController = navController
                )
                Spacer(modifier = Modifier.height(28.dp))

                // 3. Weather-Based
                BookCarousel(
                    title = "On a Rainy Day ☔",
                    books = allBooks.shuffled().take(6),
                    navController = navController
                )
                Spacer(modifier = Modifier.height(32.dp))

                // 4. Editors' Picks
                EditorPickCard()
                Spacer(modifier = Modifier.height(32.dp))

                // 5. Reader Musings
                JournalSection()
                Spacer(modifier = Modifier.height(32.dp))

                // 6. Genre Grid
                CategoryGrid(navController = navController)

                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}
