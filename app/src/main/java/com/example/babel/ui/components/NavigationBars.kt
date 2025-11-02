package com.example.babel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            titleContentColor = colorScheme.onPrimary,
            actionIconContentColor = colorScheme.onPrimary
        ),
        title = {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search books...",
                        color = colorScheme.onPrimary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colorScheme.onPrimary
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp) // slightly taller for better visibility
                    .clip(RoundedCornerShape(8.dp)), // slightly less rounded
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    unfocusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    cursorColor = colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = colorScheme.onPrimary,
                    unfocusedTextColor = colorScheme.onPrimary
                ),
                textStyle = MaterialTheme.typography.labelSmall
            )
        },
        actions = {
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
fun BottomBar(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val items = listOf(
        NavItem("Home", Icons.Default.Home, "home"),
        NavItem("Library", Icons.Default.Menu, "library"),
        NavItem("Explore", Icons.Default.Search, "explore"),
        NavItem("Stats", Icons.Default.Info, "stats"),
        NavItem("Journal", Icons.Default.Create, "journal"),
        NavItem("Settings", Icons.Default.Settings, "settings")
    )

    NavigationBar(
        containerColor = colorScheme.primary, // same as top bar
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // taller to avoid cut-off icons/text
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = false, // you can link to navController destination later
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.onPrimary,
                    selectedTextColor = colorScheme.onPrimary,
                    unselectedIconColor = colorScheme.onPrimary.copy(alpha = 0.9f),
                    unselectedTextColor = colorScheme.onPrimary.copy(alpha = 0.9f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
