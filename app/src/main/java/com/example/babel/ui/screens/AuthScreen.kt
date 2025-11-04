package com.example.babel.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.babel.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isLogin) "Sign In to Babel" else "Create Account",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            val result = if (uiState.isLogin)
                                viewModel.signIn(email, password)
                            else viewModel.signUp(email, password)

                            result.onSuccess {
                                Toast.makeText(context, "Welcome ${it.email}", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }.onFailure {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(if (uiState.isLogin) "Sign In" else "Sign Up")
                }

                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { viewModel.toggleMode() }) {
                    Text(if (uiState.isLogin)
                        "Don't have an account? Sign Up"
                    else
                        "Already have an account? Sign In")
                }
            }
        }
    }
}
