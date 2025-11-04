package com.example.babel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.local.BookLoader
import com.example.babel.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    val viewModel: ExploreViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExplore(city = "Chennai", weather = "Rainy")
    }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedBackground()

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    BookCarousel(
                        title = "Popular in Your City",
                        books = state.cityBooks,
                        navController = navController
                    )

                    BookCarousel(
                        title = "On a Rainy Day",
                        books = state.weatherBooks,
                        navController = navController
                    )

                    BookCarousel(
                        title = "Books Our Editors Loved",
                        books = state.editorPicks,
                        navController = navController
                    )

                    JournalSection(journals = state.publicJournals)

                    CategoryGrid(navController = navController)
                }
            }
        }
    }
}
