package com.example.babel.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BottomBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babel.ui.viewmodel.JournalViewModel
import java.util.*

data class JournalEntry(
    val id: String,
    val content: String,
    val date: String,
    val visibility: String = "private"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JournalScreen(navController: NavController, uid: String = "demo_uid") {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    val viewModel: JournalViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<JournalEntry?>(null) }

    val localFallback = remember {
        listOf(
            JournalEntry("1", "“A night in Gondor and I still can't sleep.” – Elara", "10 Oct 2025, 09:23 PM"),
            JournalEntry("2", "“When the last page turns, a part of me stays behind.” – Rowan", "02 Oct 2025, 07:42 PM"),
            JournalEntry("3", "“Jane Austen still ruins men for me.” – Mira", "30 Sep 2025, 11:01 AM")
        )
    }

    val journals = if (state.journals.isNotEmpty()) state.journals else localFallback

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add note")
            }
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            AnimatedBackground()
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "Your Journal",
                    style = typography.headlineSmall,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(journals) { _, entry ->
                        JournalCard(
                            entry = entry,
                            onEdit = { editingEntry = entry },
                            onDelete = {
                                viewModel.deleteJournal(entry.id, uid)
                            }
                        )
                    }
                }
            }

            if (showAddDialog) {
                AddNoteDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { newText ->
                        val newEntry = JournalEntry(
                            id = UUID.randomUUID().toString(),
                            content = newText,
                            date = formatter.format(Date())
                        )
                        viewModel.addJournal(uid, newEntry.content, "private")
                        showAddDialog = false
                    }
                )
            }

            editingEntry?.let { entry ->
                AddNoteDialog(
                    onDismiss = { editingEntry = null },
                    onSave = { updatedText ->
                        viewModel.editJournal(entry.id, updatedText, entry.visibility, uid)
                        editingEntry = null
                    },
                    initialText = entry.content
                )
            }
        }
    }
}

@Composable
fun JournalCard(entry: JournalEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        colorScheme.primary.copy(alpha = 0.12f),
                        colorScheme.secondary.copy(alpha = 0.12f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { showMenu = true })
            }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = entry.content,
                style = typography.bodyMedium.copy(color = colorScheme.onSurface),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = entry.date,
                style = typography.labelSmall.copy(color = colorScheme.onSurfaceVariant),
                modifier = Modifier.align(Alignment.End)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("Edit", color = colorScheme.onSurface) },
                onClick = {
                    showMenu = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete", color = colorScheme.error) },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    initialText: String = ""
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var text by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onSave(text) }) {
                Text("Save", color = colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colorScheme.secondary)
            }
        },
        title = {
            Text(
                if (initialText.isBlank()) "New Journal Entry" else "Edit Journal Entry",
                style = typography.titleMedium
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Write your thoughts...", color = colorScheme.onSurfaceVariant) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
