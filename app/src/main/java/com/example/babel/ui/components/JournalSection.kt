package com.example.babel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

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
                JournalCard(
                    text = quote,
                    date = null,
                    onClick = null
                )
            }
        }
    }
}

@Composable
fun JournalCard(
    text: String,
    date: String?,
    onClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val gradient = Brush.verticalGradient(
        listOf(
            colorScheme.primary.copy(alpha = 0.12f),
            colorScheme.secondary.copy(alpha = 0.12f)
        )
    )

    Box(
        modifier = Modifier
            .width(250.dp)
            .heightIn(min = 120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = colorScheme.primary
                )
            )
            if (date != null) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun AddNoteCard(onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val gradient = Brush.linearGradient(
        listOf(colorScheme.primary.copy(alpha = 0.15f), colorScheme.secondary.copy(alpha = 0.15f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add note",
            tint = colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
    }
}
