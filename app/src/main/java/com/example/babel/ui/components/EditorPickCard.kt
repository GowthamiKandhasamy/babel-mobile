package com.example.babel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EditorPickCard() {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        colorScheme.primaryContainer,
                        colorScheme.secondaryContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Books Our Editors Loved",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Curated tales handpicked by our in-house bibliophiles.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            )
        }
    }
}
