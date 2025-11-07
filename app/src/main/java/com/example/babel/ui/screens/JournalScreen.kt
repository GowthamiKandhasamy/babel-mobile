package com.example.babel.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BottomBar
import com.example.babel.ui.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Local JournalEntry model (used only for display/offline)
 */
data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val date: String = "",
    val visibility: String = "private"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JournalScreen(
    navController: NavController,
    uid: String = "demo_uid"
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val context = LocalContext.current

    val viewModel: JournalViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.initLocal(context)
        viewModel.loadUserJournals(uid)
    }

    val state by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<JournalEntry?>(null) }

    val remoteJournals = state.journals.map {
        JournalEntry(
            id = it.id,
            content = it.content,
            date = formatter.format(Date(it.createdAt)),
            visibility = it.visibility
        )
    }

    val localJournals = state.localJournals

    val combinedJournals = (localJournals + remoteJournals).sortedByDescending { it.date }

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
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground()

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
                    items(combinedJournals, key = { it.id }) { entry ->
                        JournalCard(
                            entry = entry,
                            onEdit = { editingEntry = entry },
                            onDelete = {
                                if (state.localJournals.any { it.id == entry.id })
                                    viewModel.deleteLocalJournal(entry.id)
                                else
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
                        // save locally
                        viewModel.addLocalJournal(newEntry)
                        showAddDialog = false
                    }
                )
            }

            editingEntry?.let { entry ->
                AddNoteDialog(
                    onDismiss = { editingEntry = null },
                    onSave = { updatedText ->
                        val updated = entry.copy(content = updatedText)
                        if (state.localJournals.any { it.id == entry.id })
                            viewModel.editLocalJournal(updated)
                        else
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
            .pointerInput(Unit) { detectTapGestures(onLongPress = { showMenu = true }) }
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
