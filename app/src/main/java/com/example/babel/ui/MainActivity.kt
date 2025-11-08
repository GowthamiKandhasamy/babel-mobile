package com.example.babel.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.babel.ui.screens.AuthScreen
import com.example.babel.ui.screens.BiometricScreen
import com.example.babel.ui.screens.BookDetailScreen
import com.example.babel.ui.screens.ExploreScreen
import com.example.babel.ui.screens.HomeScreen
import com.example.babel.ui.screens.JournalScreen
import com.example.babel.ui.screens.LibraryScreen
import com.example.babel.ui.screens.ProfileScreen
import com.example.babel.ui.screens.SettingsScreen
import com.example.babel.ui.screens.SplashScreen
import com.example.babel.ui.screens.StatsScreen
import com.example.babel.ui.theme.BabelTheme
import com.google.firebase.FirebaseApp
import androidx.fragment.app.FragmentActivity
import com.example.babel.utils.EncryptedLocationManager
import com.example.babel.utils.NotificationPreferenceManager
import com.example.babel.utils.WeatherBookNotifier
import com.google.firebase.auth.FirebaseAuth


class MainActivity : FragmentActivity() {
    private var weatherNotifier: WeatherBookNotifier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        val prefManager = NotificationPreferenceManager(this)
        val apiKey = "0a69a41f0e43ceb3a89ab79ddb8cc4d5"
        prefManager.resumeIfEnabled(apiKey)
        val encryptedLocationManager = EncryptedLocationManager(this)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            encryptedLocationManager.fetchAndStoreEncryptedLocation()
        }
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                encryptedLocationManager.fetchAndStoreEncryptedLocation()
            } else {
                // Optional: handle logout
            }
        }
        setContent {
            BabelTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    composable("auth") { AuthScreen(navController) }
                    composable("home") { HomeScreen(navController) }
                    composable("library") { LibraryScreen(navController) }
                    composable("biometric") { BiometricScreen(navController) }
                    composable("explore") { ExploreScreen(navController, apiKey = "0a69a41f0e43ceb3a89ab79ddb8cc4d5") }
                    composable("journal") { JournalScreen(navController) }
                    composable("stats") { StatsScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                    composable("profile") { ProfileScreen(navController) }
                    composable(
                        "bookDetail/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0
                        BookDetailScreen(navController, bookId)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BabelTheme {
        Greeting("Android")
    }
}
