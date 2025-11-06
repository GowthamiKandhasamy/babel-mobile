package com.example.babel.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.babel.ui.theme.*
import com.example.babel.utils.BiometricHelperFragment
import kotlinx.coroutines.delay

@Composable
fun BiometricScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Add animated gradient background like SplashScreen
    val infiniteTransition = rememberInfiniteTransition(label = "biometricGradient")
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "biometricGradientShift"
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(MidnightBlue, DeepPlum, Amethyst, SilverAccent),
        start = Offset(0f + gradientShift * 200f, 1200f + gradientShift * 200f),
        end = Offset(1200f - gradientShift * 200f, 0f - gradientShift * 200f)
    )

    // Lottie animation setup
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("book_open.json"))
    var playAnimation by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (playAnimation) LottieConstants.IterateForever else 1
    )

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(220.dp)
        )
    }

    // Launch biometric AFTER Compose draws (slight delay)
    LaunchedEffect(Unit) {
        delay(400) // Let Compose render first
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.beginTransaction()
                .add(
                    BiometricHelperFragment { success ->
                        playAnimation = false
                        if (success) {
                            Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("biometric") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                            navController.navigate("auth") {
                                popUpTo("biometric") { inclusive = true }
                            }
                        }
                    },
                    "biometric_fragment"
                )
                .commitAllowingStateLoss() // prevents fragment state blocking
        } else {
            Toast.makeText(context, "Biometric requires FragmentActivity", Toast.LENGTH_SHORT).show()
            navController.navigate("auth")
        }
    }
}
