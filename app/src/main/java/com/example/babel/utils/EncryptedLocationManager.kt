package com.example.babel.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.babel.data.models.EncryptedLocation
import com.example.babel.repository.LocationWeatherRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class EncryptedLocationManager(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val repo = LocationWeatherRepository(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Fetches user location using LocationWeatherRepository,
     * encrypts it with HMAC, and uploads to Firestore.
     */
    fun fetchAndStoreEncryptedLocation() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w("EncryptedLocationMgr", "⚠️ User not logged in — skipping location upload.")
            return
        }

        repo.getLocation { lat, lon, subLocality ->
            scope.launch {
                try {
                    val secretKey = "CRYPTOKEY" // ⚠️ Replace or move to secure storage
                    val message = "$lat,$lon,$subLocality"
                    val hashedLocation = generateHmacSHA256(secretKey, message)

                    val encryptedLocation = EncryptedLocation(
                        userId = uid,
                        hashedLocation = hashedLocation,
                        latitude = lat,
                        longitude = lon,
                        locality = subLocality,
                        timestamp = System.currentTimeMillis()
                    )

                    firestore.collection("userEncryptedLocations")
                        .document(uid) // overwrite per user (latest location)
                        .set(encryptedLocation)
                        .addOnSuccessListener {
                            Log.d("EncryptedLocationMgr", "✅ Encrypted location updated for $uid")
                        }
                        .addOnFailureListener { e ->
                            Log.e("EncryptedLocationMgr", "❌ Failed to store encrypted location", e)
                        }

                } catch (e: Exception) {
                    Log.e("EncryptedLocationMgr", "Error encrypting location: ${e.message}")
                }
            }
        }
    }

    /**
     * HMAC-SHA256 encryption helper
     */
    private fun generateHmacSHA256(secretKey: String, message: String): String {
        val algorithm = "HmacSHA256"
        val mac = Mac.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), algorithm)
        mac.init(keySpec)
        val hmacBytes = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
    }
}
