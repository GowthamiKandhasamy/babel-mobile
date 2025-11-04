package com.example.babel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BottomBar

@Composable
fun SettingsScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var darkMode by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("English") }

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Settings",
                    style = typography.headlineSmall.copy(color = colorScheme.onBackground)
                )

                // --- Preferences Section ---
                SettingsCategoryTitle("Preferences")

                SettingToggleItem(
                    title = "Dark Mode",
                    icon = Icons.Default.KeyboardArrowUp,
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )

                SettingToggleItem(
                    title = "Enable Notifications",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )

                SettingDropdownItem(
                    title = "Language",
                    icon = Icons.Default.Edit,
                    options = listOf("English", "French", "Spanish", "German"),
                    selectedOption = language,
                    onOptionSelected = { language = it }
                )

                // --- Security Section ---
                SettingsCategoryTitle("Security")

                SettingClickableItem(
                    title = "Privacy Policy",
                    icon = Icons.Default.Face,
                    onClick = { /* TODO: open privacy */ }
                )

                SettingClickableItem(
                    title = "Change Password",
                    icon = Icons.Default.Lock,
                    onClick = { /* TODO: navigate to password change */ }
                )

                Spacer(Modifier.height(20.dp))

                // --- About Section ---
                SettingsCategoryTitle("About")

                SettingClickableItem(
                    title = "App Version: 1.0.0",
                    icon = Icons.Default.Settings,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SettingsCategoryTitle(title: String) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(color = colorScheme.secondary),
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingToggleItem(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = typography.bodyLarge.copy(color = colorScheme.onSurface)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.primary,
                checkedTrackColor = colorScheme.primary.copy(alpha = 0.4f),
                uncheckedThumbColor = colorScheme.onSurfaceVariant,
                uncheckedTrackColor = colorScheme.surfaceVariant
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDropdownItem(
    title: String,
    icon: ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        Box(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = typography.bodyLarge.copy(color = colorScheme.onSurface)
                    )
                    Text(
                        text = selectedOption,
                        style = typography.labelSmall.copy(color = colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = colorScheme.onSurface) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SettingClickableItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = typography.bodyLarge.copy(color = colorScheme.onSurface)
            )
        }
    }
}
