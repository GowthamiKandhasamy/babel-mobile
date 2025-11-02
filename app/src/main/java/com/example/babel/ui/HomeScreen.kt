package com.example.babel.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.data.BookLoader
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BookCarousel
import com.example.babel.ui.components.BottomBar
import com.example.babel.ui.components.CategoryGrid
import com.example.babel.ui.components.TopBar
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopBar(
                navController
            )
        },
        bottomBar = {
            BottomBar(
                navController
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
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                GreetingSection()
                Spacer(modifier = Modifier.height(24.dp))

                val context = LocalContext.current
                val sampleBooks by remember { mutableStateOf(BookLoader.loadSampleBooks(context)) }

                BookCarousel(
                    title = "Featured",
                    books = sampleBooks,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(32.dp))
                CategoryGrid(navController = navController)
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
