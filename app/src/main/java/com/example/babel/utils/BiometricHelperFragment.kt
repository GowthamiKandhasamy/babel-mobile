package com.example.babel.utils

import android.content.Context
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.Executor

class BiometricHelperFragment(
    private val onAuthResult: (Boolean) -> Unit
) : Fragment() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onAttach(context: Context) {
        super.onAttach(context)
        executor = ContextCompat.getMainExecutor(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup BiometricPrompt here
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onAuthResult(true)
                    parentFragmentManager.beginTransaction().remove(this@BiometricHelperFragment)
                        .commitAllowingStateLoss()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onAuthResult(false)
                    parentFragmentManager.beginTransaction().remove(this@BiometricHelperFragment)
                        .commitAllowingStateLoss()
                }

                override fun onAuthenticationFailed() {
                    onAuthResult(false)
                }
            }
        )

        // Build prompt info
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Babel")
            .setSubtitle("Authenticate to continue")
            .setNegativeButtonText("Cancel")
            .build()
    }

    override fun onResume() {
        super.onResume()
        // Start authentication only after fragment is resumed and ready
        biometricPrompt.authenticate(promptInfo)
    }
}