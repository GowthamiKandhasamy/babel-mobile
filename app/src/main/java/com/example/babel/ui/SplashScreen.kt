package com.example.babel.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition()
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Use palette from theme (ensures consistency)
    val c1 = MaterialTheme.colorScheme.background // MidnightBlue
    val c2 = MaterialTheme.colorScheme.secondary  // DeepPlum
    val c3 = MaterialTheme.colorScheme.primary    // Amethyst
    val c4 = MaterialTheme.colorScheme.tertiary   // SilverAccent

    val animatedBrush = Brush.linearGradient(
        colors = listOf(c1, c2, c3, c4),
        start = Offset(0f, gradientShift * 1200f),
        end = Offset(1200f, 1200f - gradientShift * 1200f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush),
        contentAlignment = Alignment.Center
    ) {
        // Lottie animation (if you have it). If not, remove this block and just use the Text below.
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("magic_logo.json"))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

        if (composition != null) {
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.size(200.dp)
            )
        }

        // App title (placed under the logo)
        Text(
            text = "Babel",
            fontSize = 48.sp,
            color = MaterialTheme.colorScheme.onBackground, // will be SilverAccent on dark theme
            modifier = Modifier
                .padding(top = 250.dp)
                .alpha(0.95f)
        )
    }

    // Navigate to Home after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }
}
