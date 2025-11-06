package com.example.babel.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.babel.data.viewmodel.BookViewModel
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BookCarousel
import com.example.babel.ui.components.BottomBar
import com.example.babel.ui.components.TopBar
import com.example.babel.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val searchResults by bookViewModel.bookList.collectAsState()
    val isBookLoading by bookViewModel.isLoading.collectAsState()

    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeViewModel.loadHomeData(userGenre = 10)
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                onSearchChange = { query ->
                    isSearching = query.isNotBlank()
                    bookViewModel.searchBooks(query)
                }
            )
        },
        bottomBar = { BottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground()

            // ✨ Smooth transition between search view and normal home view
            AnimatedContent(
                targetState = isSearching,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "search_to_home_transition"
            ) { searching ->
                if (searching) {
                    // =======================
                    // 🔍 SEARCH VIEW
                    // =======================
                    when {
                        isBookLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        searchResults.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "No books found for your search.",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                BookCarousel(
                                    title = "Search Results",
                                    books = searchResults,
                                    navController = navController
                                )
                            }
                        }
                    }
                } else {
                    // =======================
                    // 🏠 HOME VIEW
                    // =======================
                    when {
                        uiState.isLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        uiState.error != null -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Error: ${uiState.error}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        uiState.featured.isEmpty() && uiState.newReleases.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "No books found. Check Firestore fields.",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                GreetingSection()

                                if (uiState.featured.isNotEmpty()) {
                                    BookCarousel(
                                        title = "Featured Books",
                                        books = uiState.featured,
                                        navController = navController
                                    )
                                }

                                if (uiState.newReleases.isNotEmpty()) {
                                    BookCarousel(
                                        title = "New Releases",
                                        books = uiState.newReleases,
                                        navController = navController
                                    )
                                }

                                if (uiState.recommended.isNotEmpty()) {
                                    BookCarousel(
                                        title = "Since You Liked...",
                                        books = uiState.recommended,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingSection() {
    val colorScheme = MaterialTheme.colorScheme
    val messages = listOf(
        "Welcome back, Wanderer",
        "The library awaits your return",
        "Another story calls your name...",
        "The pages whisper secrets tonight..."
    )
    var currentMessage by remember { mutableStateOf(messages.random()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentMessage = messages.random()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = currentMessage,
            transitionSpec = {
                fadeIn(animationSpec = tween(2000)) togetherWith
                        fadeOut(animationSpec = tween(2000, delayMillis = 50))
            },
            label = ""
        ) { message ->
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "What shall we discover today?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}
