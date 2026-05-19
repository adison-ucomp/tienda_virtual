package com.compensar.tienda.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class HomePasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnBackLogin: TextView
    private lateinit var btnContinue: Button
    private lateinit var txtNewPassword: EditText
    private lateinit var txtConfirmPassword: EditText
    private lateinit var loaderPassword: ProgressBar

    private var isPasswordLoading = false

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")
    private val resetCollection = db.collection("password")

    private var token: String = ""
    private var userRegister: Long = 0
    private var tokenValid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_password)

        token = intent?.data?.getQueryParameter("token")
            ?: intent.getStringExtra("token")
            ?: ""

        applyWindowInsets()
        initViews()
        initEvents()
        validateToken()
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
        btnBackLogin = findViewById(R.id.btnBackLogin)
        btnContinue = findViewById(R.id.btnContinue)
        txtNewPassword = findViewById(R.id.txtNewPassword)
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword)
        loaderPassword = findViewById(R.id.loaderPassword)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            goToLogin()
        }

        btnBackLogin.setOnClickListener {
            goToLogin()
        }

        btnContinue.setOnClickListener {
            if (!isPasswordLoading) {
                actionUpdatePassword()
            }
        }
    }

    private fun validateToken() {
        if (token.isEmpty()) {
            btnContinue.isEnabled = false
            Toast.makeText(this, "Enlace de recuperación no válido", Toast.LENGTH_LONG).show()
            return
        }

        resetCollection.document(token)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    btnContinue.isEnabled = false
                    Toast.makeText(this, "La solicitud no existe", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                val used = document.getBoolean("used") ?: false
                val codeValidated = document.getBoolean("codeValidated") ?: false
                val expiresAt = document.getLong("expiresAt") ?: 0
                val register = document.getLong("userRegister") ?: 0

                if (used) {
                    btnContinue.isEnabled = false
                    Toast.makeText(this, "Este enlace ya fue utilizado", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (System.currentTimeMillis() > expiresAt) {
                    btnContinue.isEnabled = false
                    Toast.makeText(this, "El código de recuperación expiró", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (!codeValidated) {
                    btnContinue.isEnabled = false
                    Toast.makeText(this, "Primero debes validar el código enviado al correo", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (register <= 0) {
                    btnContinue.isEnabled = false
                    Toast.makeText(this, "Usuario no válido", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                userRegister = register
                tokenValid = true
                btnContinue.isEnabled = true
            }
            .addOnFailureListener { exception ->
                btnContinue.isEnabled = false
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun actionUpdatePassword() {
        if (!tokenValid || userRegister <= 0) {
            Toast.makeText(this, "Solicitud no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val newPassword = txtNewPassword.text.toString().trim()
        val confirmPassword = txtConfirmPassword.text.toString().trim()

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la nueva contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Debes confirmar la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        setPasswordLoading(true)

        val encryptedPassword = encryptPassword(newPassword)

        userCollection.document(userRegister.toString())
            .update("password", encryptedPassword)
            .addOnSuccessListener {
                resetCollection.document(token)
                    .update("used", true)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        goToLogin()
                    }
                    .addOnFailureListener { exception ->
                        setPasswordLoading(false)
                        Toast.makeText(this, "Error actualizando solicitud: ${exception.message}", Toast.LENGTH_LONG).show()
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                setPasswordLoading(false)
                Toast.makeText(this, "Error actualizando contraseña: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun setPasswordLoading(isLoading: Boolean) {
        isPasswordLoading = isLoading
        btnContinue.isEnabled = !isLoading
        btnBack.isEnabled = !isLoading
        btnBackLogin.isEnabled = !isLoading
        txtNewPassword.isEnabled = !isLoading
        txtConfirmPassword.isEnabled = !isLoading
        loaderPassword.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnContinue.text = if (isLoading) "Guardando..." else "Continuar"
    }

    private fun encryptPassword(password: String): String {
        val salt = "com.compensar.tienda.user.password"

        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
