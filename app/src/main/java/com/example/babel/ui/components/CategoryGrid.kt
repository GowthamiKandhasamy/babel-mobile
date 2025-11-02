package com.example.babel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CategoryGrid(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val categories = listOf("Your Library", "Discover", "Stats", "Nearby Readers")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        categories.forEach { category ->
            CategoryCard(title = category, colorScheme = colorScheme) {
                // TODO: Navigate appropriately
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, colorScheme: ColorScheme, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(
        listOf(colorScheme.primaryContainer, colorScheme.tertiaryContainer)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = colorScheme.onPrimaryContainer,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
