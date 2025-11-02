package com.example.babel.ui.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JournalSection() {
    val colorScheme = MaterialTheme.colorScheme
    val musings = listOf(
        "“A night in Gondor and I still can't sleep.” – Elara",
        "“When the last page turns, a part of me stays behind.” – Rowan",
        "“Jane Austen still ruins men for me.” – Mira"
    )

    Column {
        Text(
            text = "Musings from Fellow Readers",
            style = MaterialTheme.typography.titleLarge.copy(
                color = colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(musings) { quote ->
                JournalCard(quote)
            }
        }
    }
}

@Composable
fun JournalCard(text: String) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .width(250.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = colorScheme.primary
            )
        )
    }
}