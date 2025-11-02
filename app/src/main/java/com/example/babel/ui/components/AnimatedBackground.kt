package com.example.babel.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

@Composable
fun AnimatedBackground() {
    val colorScheme = MaterialTheme.colorScheme
    val particles = remember {
        List(25) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6 + 2,
                speed = Random.nextFloat() * 0.002f + 0.0005f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        particles.forEach { p ->
            val newY = (p.y + p.speed) % 1f
            drawCircle(
                color = colorScheme.secondary.copy(alpha = 0.08f),
                radius = p.size,
                center = Offset(p.x * width, newY * height)
            )
            p.y = newY
        }
    }
}

data class Particle(
    var x: Float,
    var y: Float,
    var size: Float,
    var speed: Float
)
