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
import com.compensar.tienda.ui.common.EmailBackHelper
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
        btnBack.setOnClickListener { goToLogin() }
        btnBackLogin.setOnClickListener { goToLogin() }
        btnRestore.setOnClickListener { actionRestore() }
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
        val code = generateCode()
        val now = System.currentTimeMillis()
        val expiresAt = now + 30 * 60 * 1000
        val email = user.email ?: ""
        val name = listOfNotNull(user.names, user.srnms)
            .joinToString(" ")
            .trim()
            .ifEmpty { "Usuario" }

        val data = hashMapOf(
            "token" to token,
            "code" to code,
            "email" to email,
            "userRegister" to user.register,
            "createdAt" to now,
            "expiresAt" to expiresAt,
            "codeValidated" to false,
            "used" to false
        )

        resetCollection.document(token)
            .set(data)
            .addOnSuccessListener {
                sendRestoreCode(
                    token = token,
                    email = email,
                    code = code,
                    name = name
                )
            }
            .addOnFailureListener { exception ->
                btnRestore.isEnabled = true
                Toast.makeText(this, "Error creando solicitud: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun sendRestoreCode(
        token: String,
        email: String,
        code: String,
        name: String
    ) {
        EmailBackHelper.sendCode(
            email = email,
            code = code,
            name = name,
            onSuccess = {
                btnRestore.isEnabled = true
                Toast.makeText(this, "Código enviado correctamente", Toast.LENGTH_LONG).show()

                val intent = Intent(this, HomePassCodeActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("email", email)
                startActivity(intent)
            },
            onFailure = { exception ->
                btnRestore.isEnabled = true
                Toast.makeText(
                    this,
                    "No se pudo enviar el código: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                exception.printStackTrace()
            }
        )
    }

    private fun generateCode(): String {
        return (100000..999999).random().toString()
    }

    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
