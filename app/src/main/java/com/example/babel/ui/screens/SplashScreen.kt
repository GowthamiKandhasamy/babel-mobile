package com.example.babel.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.babel.data.repository.AuthRepository
import com.example.babel.ui.theme.Amethyst
import com.example.babel.ui.theme.DeepPlum
import com.example.babel.ui.theme.MidnightBlue
import com.example.babel.ui.theme.SilverAccent
import com.example.babel.ui.theme.PaleWhite
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    navController: NavController,
    authRepository: AuthRepository = AuthRepository(Firebase.auth)
) {
    val currentUser = remember { authRepository.currentUser() }

    val infiniteTransition = rememberInfiniteTransition(label = "gradientShift")
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientShiftValue"
    )

    val colors = listOf(MidnightBlue, DeepPlum, Amethyst, SilverAccent)

    val animatedBrush = Brush.linearGradient(
        colors = colors,
        start = Offset(0f + gradientShift * 200f, 1200f + gradientShift * 200f),
        end = Offset(1200f - gradientShift * 200f, 0f - gradientShift * 200f)
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("magic_logo.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush),
        contentAlignment = Alignment.Center
    ) {
        composition?.let {
            LottieAnimation(it, progress, modifier = Modifier.size(200.dp))
        }

        Text(
            text = "Babel",
            fontSize = 48.sp,
            color = PaleWhite,
            modifier = Modifier
                .padding(top = 250.dp)
                .alpha(0.95f)
        )
    }

    LaunchedEffect(Unit) {
        delay(1) // let first frame render
        delay(3000) // animation duration
        if (currentUser != null) {
            navController.navigate("biometric") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}
