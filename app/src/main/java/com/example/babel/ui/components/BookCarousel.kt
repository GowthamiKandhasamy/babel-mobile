package com.example.babel.ui.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.babel.models.Book

/**
 * A generic reusable carousel for displaying a horizontal row of books.
 * Can be reused for Featured, Library shelves, etc.
 */
@Composable
fun BookCarousel(
    title: String,
    books: List<Book>,
    navController: NavController,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    cardWidth: Int = 180,
    cardHeight: Int = 260
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        if (showTitle) {
            Text(
                text = title,
                style = typography.titleLarge.copy(color = colorScheme.secondary),
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(books) { book ->
                BookCarouselCard(
                    book = book,
                    navController = navController,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight
                )
            }
        }
    }
}

/**
 * A reusable carousel card showing a single book.
 */
@Composable
fun BookCarouselCard(
    book: Book,
    navController: NavController,
    cardWidth: Int,
    cardHeight: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .scale(scale.value)
            .size(width = cardWidth.dp, height = cardHeight.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(colorScheme.surfaceVariant)
            .pointerInput(book.title) {
                detectTapGestures(
                    onTap = {
                        Log.d("BookNav", "Clicked ${book.id}")
                        navController.navigate("bookDetail/${book.id}")
                    },
                    onPress = {
                        scale.animateTo(0.95f, tween(100))
                        tryAwaitRelease()
                        scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- Book Cover ---
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(book.coverImage)
                .crossfade(true)
                .build(),
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- Gradient Overlay ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // --- Text Info ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = book.title,
                color = colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.authors.joinToString(", "),
                color = colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
