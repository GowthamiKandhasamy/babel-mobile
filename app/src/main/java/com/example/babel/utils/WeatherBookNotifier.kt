package com.example.babel.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.babel.R
import com.example.babel.data.models.Book
import com.example.babel.repository.LocationWeatherRepository
import kotlinx.coroutines.*
import org.json.JSONArray
import kotlin.random.Random

class WeatherBookNotifier(private val context: Context) {

    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val repo = LocationWeatherRepository(context)

    // Load notification messages from assets
    private val messages: List<String> by lazy { loadMessagesFromAssets(context) }

    /**
     * Start showing book recommendations periodically
     */
    fun start(apiKey: String) {
        stop() // stop existing job to prevent duplicates
        Log.d("WeatherBookNotifier", "🔔 start() called")

        job = scope.launch {
            while (isActive) {
                try {
                    repo.getLocation { lat, lon, _ ->
                        repo.getWeather(lat, lon, apiKey) { condition ->
                            repo.fetchWeatherBooks(condition) { books ->
                                if (books.isNotEmpty()) {
                                    val randomBook = books.random()
                                    val msgTemplate = messages.randomOrNull() ?: "Check out (title)!"
                                    val message = msgTemplate.replace("(title)", randomBook.title)
                                    sendNotification(message)
                                } else {
                                    Log.d("WeatherBookNotifier", "⚠️ No books found for $condition")
                                }
                            }
                        }
                    }
                    delay(10_000) // 10 seconds (you can adjust this)
                } catch (e: Exception) {
                    Log.e("WeatherBookNotifier", "Error in notification loop: ${e.message}")
                }
            }
        }
    }

    /**
     * Sends a single notification
     */
    private fun sendNotification(message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createChannel(notificationManager)

        val builder = NotificationCompat.Builder(context, "weather_books")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Babel Suggests 📖")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(Random.nextInt(), builder.build())
    }

    /**
     * Creates the notification channel if needed (for Android O+)
     */
    private fun createChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_books",
                "Weather Book Suggestions",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Stops all running notification loops
     */
    fun stop() {
        Log.d("WeatherBookNotifier", "🛑 stop() called")
        job?.cancel()
        job = null
    }

    /**
     * Loads notification messages from assets JSON file
     */
    private fun loadMessagesFromAssets(context: Context): List<String> {
        return try {
            val inputStream = context.assets.open("notification_message_sample.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            List(jsonArray.length()) { index -> jsonArray.getString(index) }
        } catch (e: Exception) {
            Log.e("WeatherBookNotifier", "⚠️ Failed to load messages: ${e.message}")
            listOf("Hey, check out (title)!") // fallback message
        }
    }
}
