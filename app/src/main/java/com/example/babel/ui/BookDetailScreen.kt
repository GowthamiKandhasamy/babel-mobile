package com.example.babel.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.babel.data.BookLoader

@Composable
fun BookDetailScreen(navController: NavController, bookId: Int) {
    val context = LocalContext.current
    val books = BookLoader.loadSampleBooks(context)
    val book = books.find { it.id == bookId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        book?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberAsyncImagePainter(it.coverImage),
                    contentDescription = it.title,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = it.title, style = MaterialTheme.typography.titleLarge)
                Text(text = it.authors.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
                it.subtitle?.let { sub ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = sub, style = MaterialTheme.typography.bodySmall)
                }
            }
        } ?: Text("Book not found.")
    }
}
