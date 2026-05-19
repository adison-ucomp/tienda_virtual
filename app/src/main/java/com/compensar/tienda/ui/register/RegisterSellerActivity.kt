package com.compensar.tienda.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.SellerModel
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.home.HomeLoginActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.security.MessageDigest

class RegisterSellerActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView
    private lateinit var btnRegister: Button
    private lateinit var btnShowPassword: TextView

    private lateinit var txtNames: EditText
    private lateinit var txtLastNames: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtCompany: EditText
    private lateinit var txtNit: EditText
    private lateinit var txtAddress: EditText

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")
    private val sellerCollection = db.collection("seller")

    private var passwordVisible = false
    private var isProcessing = false
    private var generatedUserRegister: Long? = null
    private var generatedSellerRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register_seller)

        applyWindowInsets()
        initViews()
        initEvents()
        loadNextRegisters()
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
        btnRegister = findViewById(R.id.btnRegister)
        btnShowPassword = findViewById(R.id.btnShowPassword)

        txtNames = findViewById(R.id.txtNames)
        txtLastNames = findViewById(R.id.txtLastNames)
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        txtCompany = findViewById(R.id.txtCompany)
        txtNit = findViewById(R.id.txtNit)
        txtAddress = findViewById(R.id.txtAddress)
    }

    private fun initEvents() {
        btnBack.setOnClickListener { goToLogin() }
        btnShowPassword.setOnClickListener { togglePasswordVisibility() }
        btnRegister.setOnClickListener { registerSeller() }
    }

    private fun registerSeller() {
        if (isProcessing) return

        val userRegister = generatedUserRegister
        val sellerRegister = generatedSellerRegister

        if (userRegister == null || userRegister <= 0L || sellerRegister == null || sellerRegister <= 0L) {
            Toast.makeText(this, "Espera a que se generen los registros automáticos", Toast.LENGTH_SHORT).show()
            loadNextRegisters()
            return
        }

        val names = txtNames.text.toString().trim()
        val lastNames = txtLastNames.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()
        val company = txtCompany.text.toString().trim()
        val nit = txtNit.text.toString().trim()
        val address = txtAddress.text.toString().trim()

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

        if (company.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la empresa", Toast.LENGTH_SHORT).show()
            return
        }

        if (nit.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el NIT", Toast.LENGTH_SHORT).show()
            return
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la dirección", Toast.LENGTH_SHORT).show()
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
                    val userData = UserModel(
                        register = userRegister,
                        names = names,
                        srnms = lastNames,
                        email = email,
                        password = encryptPassword(password),
                        storefire = null,
                        idRole = SELLER_ROLE_ID
                    )

                    val sellerData = SellerModel(
                        register = sellerRegister,
                        company = company,
                        nit = nit,
                        address = address,
                        idUser = userRegister
                    )

                    saveSellerUser(userData, sellerData)
                }
            }
            .addOnFailureListener { exception ->
                setProcessing(false)
                Toast.makeText(this, "Error al validar correo: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveSellerUser(userData: UserModel, sellerData: SellerModel) {
        userCollection.document(userData.register.toString())
            .get()
            .addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    setProcessing(false)
                    Toast.makeText(this, "El ID automático de usuario ya existe. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                    loadNextRegisters()
                    return@addOnSuccessListener
                }

                sellerCollection.document(sellerData.register.toString())
                    .get()
                    .addOnSuccessListener { sellerDocument ->
                        if (sellerDocument.exists()) {
                            setProcessing(false)
                            Toast.makeText(this, "El ID automático de vendedor ya existe. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                            loadNextRegisters()
                            return@addOnSuccessListener
                        }

                        db.runBatch { batch ->
                            batch.set(userCollection.document(userData.register.toString()), userData)
                            batch.set(sellerCollection.document(sellerData.register.toString()), sellerData)
                        }
                            .addOnSuccessListener {
                                Toast.makeText(this, "Vendedor registrado correctamente", Toast.LENGTH_SHORT).show()
                                goToLogin()
                            }
                            .addOnFailureListener { exception ->
                                setProcessing(false)
                                Toast.makeText(this, "Error al registrar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                                exception.printStackTrace()
                            }
                    }
                    .addOnFailureListener { exception ->
                        setProcessing(false)
                        Toast.makeText(this, "Error al validar ID de vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                setProcessing(false)
                Toast.makeText(this, "Error al validar ID de usuario: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun loadNextRegisters() {
        btnRegister.isEnabled = false
        loadNextUserRegister()
        loadNextSellerRegister()
    }

    private fun loadNextUserRegister() {
        userCollection
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val lastRegister = result.documents.firstOrNull()?.getLong("register") ?: 0L
                generatedUserRegister = lastRegister + 1L
                enableRegisterIfReady()
            }
            .addOnFailureListener { exception ->
                generatedUserRegister = null
                btnRegister.isEnabled = !isProcessing
                Toast.makeText(this, "Error al generar ID de usuario: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun loadNextSellerRegister() {
        sellerCollection
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val lastRegister = result.documents.firstOrNull()?.getLong("register") ?: 0L
                generatedSellerRegister = lastRegister + 1L
                enableRegisterIfReady()
            }
            .addOnFailureListener { exception ->
                generatedSellerRegister = null
                btnRegister.isEnabled = !isProcessing
                Toast.makeText(this, "Error al generar ID de vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun enableRegisterIfReady() {
        btnRegister.isEnabled = !isProcessing && generatedUserRegister != null && generatedSellerRegister != null
    }

    private fun setProcessing(processing: Boolean) {
        isProcessing = processing
        btnRegister.isEnabled = !processing
        btnRegister.text = if (processing) "Registrando..." else "Registrarme"

        txtNames.isEnabled = !processing
        txtLastNames.isEnabled = !processing
        txtEmail.isEnabled = !processing
        txtPassword.isEnabled = !processing
        txtCompany.isEnabled = !processing
        txtNit.isEnabled = !processing
        txtAddress.isEnabled = !processing
        btnShowPassword.isEnabled = !processing
        btnBack.isEnabled = !processing
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
        private const val SELLER_ROLE_ID = 2L
    }
}
