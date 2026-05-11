package com.example.tiendavirtual.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class ProfileBuyerActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var switchBiometric: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_buyer)

        applyWindowInsets()
        initViews()
        initEvents()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        switchBiometric = findViewById(R.id.switchBiometric)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            finish()
        }

        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                validateBiometric()
            } else {
                Toast.makeText(this, "Biometría desactivada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateBiometric() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                switchBiometric.isChecked = false
                Toast.makeText(this, "El dispositivo no tiene lector biométrico", Toast.LENGTH_SHORT).show()
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                switchBiometric.isChecked = false
                Toast.makeText(this, "El lector biométrico no está disponible", Toast.LENGTH_SHORT).show()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                switchBiometric.isChecked = false
                Toast.makeText(this, "No tienes huella o biometría configurada", Toast.LENGTH_SHORT).show()

                val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                startActivity(intent)
            }

            else -> {
                switchBiometric.isChecked = false
                Toast.makeText(this, "No se puede usar autenticación biométrica", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    switchBiometric.isChecked = true
                    Toast.makeText(
                        this@ProfileBuyerActivity,
                        "Biometría activada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    switchBiometric.isChecked = false
                    Toast.makeText(
                        this@ProfileBuyerActivity,
                        errString,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(
                        this@ProfileBuyerActivity,
                        "No se pudo validar la huella",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Valida tu identidad para activar la huella")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}