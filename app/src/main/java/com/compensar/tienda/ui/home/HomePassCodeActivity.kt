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

/**
 * Clase [HomePassCodeActivity].
 *
 * Responsable de la logica asociada al pantalla o helper del flujo de inicio/autenticacion/compra.
 */
class HomePassCodeActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnBackLogin: TextView
    private lateinit var btnContinue: Button
    private lateinit var txtCode: EditText
    private lateinit var txtEmailInfo: TextView
    private lateinit var loaderPassCode: ProgressBar

    private var isCodeLoading = false

    private val db = FirebaseFirestore.getInstance()
    private val resetCollection = db.collection("password")

    private var token: String = ""
    private var email: String = ""

    /**
     * Se ejecuta al crear la pantalla.
     * Inicializa vista, estado y eventos principales.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_pass_code)

        token = intent.getStringExtra("token") ?: ""
        email = intent.getStringExtra("email") ?: ""

        applyWindowInsets()
        initViews()
        initEvents()
        validateInitialData()
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
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

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnBackLogin = findViewById(R.id.btnBackLogin)
        btnContinue = findViewById(R.id.btnContinue)
        txtCode = findViewById(R.id.txtCode)
        txtEmailInfo = findViewById(R.id.txtEmailInfo)
        loaderPassCode = findViewById(R.id.loaderPassCode)
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initEvents() {
        btnBack.setOnClickListener { goToRestore() }
        btnBackLogin.setOnClickListener { goToLogin() }
        btnContinue.setOnClickListener {
            if (!isCodeLoading) {
                actionValidateCode()
            }
        }
    }

    /**
     * Valida reglas de negocio antes de continuar el flujo.
     */
    private fun validateInitialData() {
        if (token.isBlank() || email.isBlank()) {
            btnContinue.isEnabled = false
            Toast.makeText(this, "Solicitud de recuperación no válida", Toast.LENGTH_LONG).show()
            return
        }

        txtEmailInfo.text = "Enviamos un código de 6 dígitos a:\n$email"
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun actionValidateCode() {
        val code = txtCode.text.toString().trim()

        if (code.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el código", Toast.LENGTH_SHORT).show()
            return
        }

        if (code.length != 6) {
            Toast.makeText(this, "El código debe tener 6 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        setCodeLoading(true)

        resetCollection.document(token)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    setCodeLoading(false)
                    Toast.makeText(this, "La solicitud no existe", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                val savedCode = document.getString("code") ?: ""
                val savedEmail = document.getString("email") ?: ""
                val used = document.getBoolean("used") ?: false
                val expiresAt = document.getLong("expiresAt") ?: 0

                if (used) {
                    setCodeLoading(false)
                    Toast.makeText(this, "Este código ya fue utilizado", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (System.currentTimeMillis() > expiresAt) {
                    setCodeLoading(false)
                    Toast.makeText(this, "El código de recuperación expiró", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (!savedEmail.equals(email, ignoreCase = true)) {
                    setCodeLoading(false)
                    Toast.makeText(this, "El correo de la solicitud no coincide", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                if (savedCode != code) {
                    setCodeLoading(false)
                    Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                resetCollection.document(token)
                    .update("validate", true)
                    .addOnSuccessListener {
                        val intent = Intent(this, HomePasswordActivity::class.java)
                        intent.putExtra("token", token)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        setCodeLoading(false)
                        Toast.makeText(this, "Error validando código: ${exception.message}", Toast.LENGTH_LONG).show()
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                setCodeLoading(false)
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun setCodeLoading(isLoading: Boolean) {
        isCodeLoading = isLoading
        btnContinue.isEnabled = !isLoading
        btnBack.isEnabled = !isLoading
        btnBackLogin.isEnabled = !isLoading
        txtCode.isEnabled = !isLoading
        loaderPassCode.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnContinue.text = if (isLoading) "Validando..." else "Validar código"
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun goToRestore() {
        val intent = Intent(this, HomeRestoreActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

