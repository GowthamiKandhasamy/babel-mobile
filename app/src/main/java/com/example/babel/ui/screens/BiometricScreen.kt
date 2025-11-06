package com.example.babel.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.babel.ui.theme.Amethyst
import com.example.babel.ui.theme.DeepPlum
import com.example.babel.ui.theme.MidnightBlue
import com.example.babel.ui.theme.SilverAccent

@Composable
fun BiometricScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    val brush = Brush.linearGradient(
        listOf(MidnightBlue, DeepPlum, Amethyst, SilverAccent)
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("book_open.json"))
    var playAnimation by remember { mutableStateOf(true) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (playAnimation) LottieConstants.IterateForever else 1
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp)
        )
    }


    LaunchedEffect(Unit) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.beginTransaction()
                .add(BiometricHelperFragment { success ->
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
                }, "biometric_fragment")
                .commit()
        } else {
            Toast.makeText(context, "Biometric requires FragmentActivity", Toast.LENGTH_SHORT).show()
            navController.navigate("auth")
        }
    }
}
