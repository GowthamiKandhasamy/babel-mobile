package com.example.babel.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.babel.data.viewmodel.BookViewModel
import com.example.babel.ui.components.*
import com.example.babel.ui.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel(),
    exploreViewModel: ExploreViewModel = viewModel()
) {
    val uiState by exploreViewModel.uiState.collectAsState()
    val searchResults by bookViewModel.bookList.collectAsState()
    val isBookLoading by bookViewModel.isLoading.collectAsState()

    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        exploreViewModel.loadExplore(city = "Chennai", weather = "Rainy")
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedBackground()

            AnimatedContent(
                targetState = isSearching,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "explore_search_transition"
            ) { searching ->
                if (searching) {
                    when {
                        isBookLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        searchResults.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "No results found.",
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
                    if (uiState.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            BookCarousel(
                                title = "Popular in Your City",
                                books = uiState.cityBooks,
                                navController = navController
                            )
                            BookCarousel(
                                title = "On a Rainy Day",
                                books = uiState.weatherBooks,
                                navController = navController
                            )
                            BookCarousel(
                                title = "Books Our Editors Loved",
                                books = uiState.editorPicks,
                                navController = navController
                            )
                            JournalSection(
                                journals = uiState.publicJournals,
                                context = LocalContext.current
                            )
                            CategoryGrid(
                                navController = navController,
                                context = LocalContext.current
                            )
                        }
                    }
                }
            }
        }
    }
}
