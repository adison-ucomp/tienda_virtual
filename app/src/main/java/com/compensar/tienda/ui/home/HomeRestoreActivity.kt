package com.compensar.tienda.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.common.SmtpEmailHelper
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class HomeRestoreActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnBackLogin: TextView
    private lateinit var btnRestore: Button
    private lateinit var txtEmail: EditText

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")
    private val resetCollection = db.collection("password")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_restore)

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
        btnBackLogin = findViewById(R.id.btnBackLogin)
        btnRestore = findViewById(R.id.btnRestore)
        txtEmail = findViewById(R.id.txtEmail)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            goToLogin()
        }

        btnBackLogin.setOnClickListener {
            goToLogin()
        }

        btnRestore.setOnClickListener {
            actionRestore()
        }
    }

    private fun actionRestore() {
        val email = txtEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el correo", Toast.LENGTH_SHORT).show()
            return
        }

        btnRestore.isEnabled = false

        userCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    btnRestore.isEnabled = true
                    Toast.makeText(this, "El correo no existe en el sistema", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val user = result.documents.firstOrNull()?.toObject(UserModel::class.java)

                if (user == null || user.register <= 0) {
                    btnRestore.isEnabled = true
                    Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                createRestoreRequest(user)
            }
            .addOnFailureListener { exception ->
                btnRestore.isEnabled = true
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun createRestoreRequest(user: UserModel) {
        val token = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val expiresAt = now + 30 * 60 * 1000
        val resetLink = "emptio://password/reset?token=$token"

        val data = hashMapOf(
            "token" to token,
            "email" to (user.email ?: ""),
            "userRegister" to user.register,
            "createdAt" to now,
            "expiresAt" to expiresAt,
            "used" to false
        )

        resetCollection.document(token)
            .set(data)
            .addOnSuccessListener {
                sendRestoreEmail(user.email ?: "", resetLink)
            }
            .addOnFailureListener { exception ->
                btnRestore.isEnabled = true
                Toast.makeText(this, "Error creando solicitud: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun sendRestoreEmail(email: String, resetLink: String) {
        SmtpEmailHelper.sendPasswordRestoreEmail(
            toEmail = email,
            resetLink = resetLink,
            onSuccess = {
                btnRestore.isEnabled = true
                Toast.makeText(
                    this,
                    "Se envió el enlace de recuperación al correo",
                    Toast.LENGTH_LONG
                ).show()
            },
            onFailure = { exception ->
                btnRestore.isEnabled = true
                Toast.makeText(
                    this,
                    "No se pudo enviar el correo: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                exception.printStackTrace()
            }
        )
    }

    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
