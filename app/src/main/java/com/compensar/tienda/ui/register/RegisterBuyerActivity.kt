package com.compensar.tienda.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.home.HomeLoginActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.security.MessageDigest

class RegisterBuyerActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView
    private lateinit var btnGoLogin: TextView
    private lateinit var btnRegister: Button
    private lateinit var btnShowPassword: TextView
    private lateinit var checkTerms: CheckBox

    private lateinit var txtNames: EditText
    private lateinit var txtLastNames: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")

    private var passwordVisible = false
    private var isProcessing = false
    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register_buyer)

        applyWindowInsets()
        initViews()
        initEvents()
        loadNextRegister()
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
        btnGoLogin = findViewById(R.id.btnGoLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnShowPassword = findViewById(R.id.btnShowPassword)
        checkTerms = findViewById(R.id.checkTerms)

        txtNames = findViewById(R.id.txtNames)
        txtLastNames = findViewById(R.id.txtLastNames)
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
    }

    private fun initEvents() {
        btnBack.setOnClickListener { goToLogin() }
        btnGoLogin.setOnClickListener { goToLogin() }
        btnShowPassword.setOnClickListener { togglePasswordVisibility() }
        btnRegister.setOnClickListener { registerBuyer() }
    }

    private fun registerBuyer() {
        if (isProcessing) return

        val register = generatedRegister
        if (register == null || register <= 0L) {
            Toast.makeText(this, "Espera a que se genere el registro automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return
        }

        val names = txtNames.text.toString().trim()
        val lastNames = txtLastNames.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()

        if (names.isEmpty()) {
            Toast.makeText(this, "Debes ingresar los nombres", Toast.LENGTH_SHORT).show()
            return
        }

        if (lastNames.isEmpty()) {
            Toast.makeText(this, "Debes ingresar los apellidos", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el correo electrónico", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 10) {
            Toast.makeText(this, "La contraseña debe tener mínimo 10 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkTerms.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            return
        }

        setProcessing(true)

        userCollection
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    setProcessing(false)
                    Toast.makeText(this, "Ya existe una cuenta registrada con este correo", Toast.LENGTH_LONG).show()
                } else {
                    val data = UserModel(
                        register = register,
                        names = names,
                        srnms = lastNames,
                        email = email,
                        password = encryptPassword(password),
                        storefire = null,
                        idRole = BUYER_ROLE_ID
                    )

                    saveBuyer(data)
                }
            }
            .addOnFailureListener { exception ->
                setProcessing(false)
                Toast.makeText(this, "Error al validar correo: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveBuyer(data: UserModel) {
        userCollection.document(data.register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    setProcessing(false)
                    Toast.makeText(this, "El ID automático ya existe. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                    loadNextRegister()
                    return@addOnSuccessListener
                }

                userCollection.document(data.register.toString())
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Comprador registrado correctamente", Toast.LENGTH_SHORT).show()
                        goToLogin()
                    }
                    .addOnFailureListener { exception ->
                        setProcessing(false)
                        Toast.makeText(this, "Error al registrar comprador: ${exception.message}", Toast.LENGTH_LONG).show()
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                setProcessing(false)
                Toast.makeText(this, "Error al validar ID automático: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun loadNextRegister() {
        btnRegister.isEnabled = false

        userCollection
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val lastRegister = result.documents.firstOrNull()?.getLong("register") ?: 0L
                generatedRegister = lastRegister + 1L
                btnRegister.isEnabled = !isProcessing
            }
            .addOnFailureListener { exception ->
                generatedRegister = null
                btnRegister.isEnabled = !isProcessing
                Toast.makeText(this, "Error al generar ID automático: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun setProcessing(processing: Boolean) {
        isProcessing = processing
        btnRegister.isEnabled = !processing
        btnRegister.text = if (processing) "Registrando..." else "Registrarme"

        txtNames.isEnabled = !processing
        txtLastNames.isEnabled = !processing
        txtEmail.isEnabled = !processing
        txtPassword.isEnabled = !processing
        checkTerms.isEnabled = !processing
        btnShowPassword.isEnabled = !processing
        btnBack.isEnabled = !processing
        btnGoLogin.isEnabled = !processing
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        txtPassword.inputType = if (passwordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        txtPassword.setSelection(txtPassword.text?.length ?: 0)
        btnShowPassword.text = if (passwordVisible) "◌" else "◉"
    }

    private fun encryptPassword(password: String): String {
        val salt = "com.compensar.tienda.user.password"
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val BUYER_ROLE_ID = 3L
    }
}
