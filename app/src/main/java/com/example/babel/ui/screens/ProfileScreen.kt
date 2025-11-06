package com.example.babel.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babel.ui.components.AnimatedBackground
import com.example.babel.ui.components.BottomBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("Wanderer") }
    var email by remember { mutableStateOf("wanderer@babelverse.com") }

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
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // --- Profile Picture ---
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    colorScheme.primary.copy(alpha = 0.3f),
                                    colorScheme.secondary.copy(alpha = 0.6f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Avatar",
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // --- Username ---
                Text(
                    text = username,
                    style = typography.headlineSmall.copy(
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = email,
                    style = typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant)
                )

                Spacer(Modifier.height(28.dp))

                // --- Edit Profile ---
                ProfileOptionCard(
                    title = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = { /* TODO: Add edit functionality */ }
                )

                Spacer(Modifier.height(12.dp))

                // --- Logout ---
                ProfileOptionCard(
                    title = "Logout",
                    icon = Icons.Default.ExitToApp,
                    color = colorScheme.error,
                    onClick = {
                        scope.launch {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("auth") {
                                popUpTo("profile") { inclusive = true }
                            }
                        }
                    }
                )

                Spacer(Modifier.height(36.dp))

                Divider(
                    color = colorScheme.outline.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // --- App Info ---
                Text(
                    text = "Babel v1.0.0",
                    style = typography.labelSmall.copy(color = colorScheme.onSurfaceVariant)
                )
                Text(
                    text = "© 2025 Babel Labs",
                    style = typography.labelSmall.copy(color = colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

@Composable
fun ProfileOptionCard(
    title: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface)
            )
        }
    }
}
