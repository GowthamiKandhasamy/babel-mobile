package com.example.babel.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.babel.data.repository.ExploreRepository
import com.example.babel.repository.LocationWeatherRepository
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BookCarousel
import com.example.babel.ui.components.BottomBar
import com.example.babel.ui.components.CategoryGrid
import com.example.babel.ui.components.JournalSection
import com.example.babel.ui.components.TopBar
import com.example.babel.ui.viewmodel.ExploreViewModel
import com.example.babel.data.viewmodel.BookViewModel
import com.example.babel.ui.viewmodel.ExploreViewModelFactory
import com.example.babel.ui.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    libraryViewModel: LibraryViewModel = viewModel(),
    bookViewModel: BookViewModel = viewModel(),
    apiKey: String
) {
    val context = LocalContext.current
    val locationRepo = LocationWeatherRepository(context)
    val exploreRepo = ExploreRepository()

    // ✅ Initialize ExploreViewModel properly using factory
    val exploreViewModel: ExploreViewModel = viewModel(
        factory = ExploreViewModelFactory(locationRepo, exploreRepo, apiKey)
    )

    val uiState by exploreViewModel.uiState.collectAsState()
    val searchResults by bookViewModel.bookList.collectAsState()
    val isBookLoading by bookViewModel.isLoading.collectAsState()
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("ExploreScreen", "📱 ExploreScreen launched")
        exploreViewModel.loadExplore(apiKey)
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                onSearchChange = { query ->
                    isSearching = query.isNotBlank()
                    bookViewModel.searchBooks(query)
                    Log.d("ExploreScreen", "🔍 Search query: '$query'")
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
                            Log.d("ExploreScreen", "✅ Rendering Explore content")

                            BookCarousel(
                                title = "Popular in Your City",
                                books = uiState.cityBooks,
                                navController = navController
                            )
                            BookCarousel(
                                title = "Books for Today's Weather",
                                books = uiState.weatherBooks,
                                navController = navController
                            )
                            BookCarousel(
                                title = "Editor's Picks",
                                books = uiState.editorPicks,
                                navController = navController
                            )
                            JournalSection(
                                journals = uiState.publicJournals,
                                context = context
                            )
                            CategoryGrid(
                                navController = navController,
                                context = context
                            )
                        }
                    }
                }
            }
        }
    }
}
