package com.example.babel.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.window.Dialog
import com.example.babel.data.models.Book
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookDialog(
    books: List<Book>,
    onDismiss: () -> Unit,
    onSave: (bookId: String, shelf: String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val formatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var selectedShelf by remember { mutableStateOf("Currently Reading") }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var progressType by remember { mutableStateOf("Pages") }
    var progressValue by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val datePicker = { onDateSelected: (String) -> Unit ->
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(formatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            color = colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Add Book to Shelf",
                    style = typography.titleLarge,
                    color = colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // --- Search Field ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Book Title", color = colorScheme.onSurfaceVariant) },
                    placeholder = { Text("Search your collection", color = colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline,
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                val results = books.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }
                if (searchQuery.isNotBlank() && results.isNotEmpty()) {
                    Column {
                        results.take(3).forEach { result ->
                            Text(
                                text = result.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedBook = result
                                        searchQuery = result.title
                                    }
                                    .padding(6.dp),
                                style = typography.bodyMedium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                // --- Shelf Dropdown ---
                val shelves = listOf("Currently Reading", "Finished Reading", "Want to Read")
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedShelf,
                        onValueChange = {},
                        label = { Text("Shelf", color = colorScheme.onSurfaceVariant) },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline,
                            focusedTextColor = colorScheme.onPrimary,
                            unfocusedTextColor = colorScheme.onPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        shelves.forEach { shelf ->
                            DropdownMenuItem(
                                text = { Text(shelf, color = colorScheme.onSurface) },
                                onClick = {
                                    selectedShelf = shelf
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // --- Conditional Inputs ---
                when (selectedShelf) {
                    "Finished Reading" -> {
                        RatingBar(rating) { rating = it }

                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Comment (optional)", color = colorScheme.onSurfaceVariant) },
                            singleLine = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        DateField("Start Date", startDate) { datePicker { startDate = it } }
                        DateField("End Date", endDate) { datePicker { endDate = it } }

                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text("Tags (comma-separated)", color = colorScheme.onSurfaceVariant) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    "Currently Reading" -> {
                        DateField("Start Date", startDate) { datePicker { startDate = it } }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = progressType == "Pages",
                                onClick = { progressType = "Pages" },
                                label = { Text("Pages") },
                                colors = FilterChipDefaults.filterChipColors( // Corrected parameter name
                                    selectedContainerColor = colorScheme.primary.copy(alpha = 0.1f),
                                    selectedLabelColor = colorScheme.primary,
                                    labelColor = colorScheme.onPrimary // Use labelColor for unselected state
                                )
                            )
                            FilterChip(
                                selected = progressType == "Percent",
                                onClick = { progressType = "Percent" },
                                label = { Text("%") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorScheme.primary.copy(alpha = 0.1f),
                                    labelColor = colorScheme.onPrimary,
                                    selectedLabelColor = colorScheme.primary
                                )
                            )
                        }

                        OutlinedTextField(
                            value = progressValue,
                            onValueChange = { progressValue = it },
                            label = {
                                Text(
                                    if (progressType == "Pages") "Pages Completed" else "Progress (%)",
                                    color = colorScheme.onSurfaceVariant
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    "Want to Read" -> {
                        Text(
                            "This book will be saved to your wishlist.",
                            color = colorScheme.onSurfaceVariant,
                            style = typography.bodySmall,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // --- Buttons ---
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = colorScheme.secondary)
                    }
                    TextButton(onClick = {
                        selectedBook?.let { onSave(it.id, selectedShelf) }
                        onDismiss()
                    }) {
                        Text("Save", color = colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        (1..5).forEach { i ->
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) colorScheme.tertiary else colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged(i) }
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun DateField(label: String, value: String, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label, color = colorScheme.onSurfaceVariant) },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.clickable { onClick() }
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
