package com.compensar.tienda.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.compensar.tienda.ui.seller.SellerDashboardActivity
import com.compensar.tienda.ui.register.RegisterBuyerActivity
import com.compensar.tienda.ui.register.RegisterSellerActivity
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.BiometricSessionManager
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

/**
 * Clase [HomeLoginActivity].
 *
 * Responsable de la logica asociada al pantalla o helper del flujo de inicio/autenticacion/compra.
 */
class HomeLoginActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private lateinit var actRestore: TextView

    private lateinit var actionBuyer: TextView
    private lateinit var actionSeller: TextView

    private lateinit var fieldEmail: EditText
    private lateinit var fieldPassword: EditText
    private lateinit var actionExecute: Button
    private lateinit var actionBiometric: TextView
    private lateinit var loaderLogin: ProgressBar

    private var isLoginLoading = false

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_login)

        applyWindowInsets()
        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        configureBiometricLogin()
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
        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)

        actRestore = findViewById(R.id.txtForgotPassword)

        actionBuyer = findViewById(R.id.actionBuyer)
        actionSeller = findViewById(R.id.actionSeller)

        fieldEmail = findViewById(R.id.fieldEmail)
        fieldPassword = findViewById(R.id.fieldPassword)
        actionExecute = findViewById(R.id.actionExecute)
        actionBiometric = findViewById(R.id.actionBiometric)
        loaderLogin = findViewById(R.id.loaderLogin)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionCategory.setOnClickListener {
            val intent = Intent(this, HomeCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionShopping.setOnClickListener {
            val intent = Intent(this, BuyerShoppingActivity::class.java)
            startActivity(intent)
        }

        actionAddress.setOnClickListener {
            val intent = Intent(this, BuyerAddressActivity::class.java)
            startActivity(intent)
        }

        actionAccount.setOnClickListener {
            Toast.makeText(this, "Cuenta", Toast.LENGTH_SHORT).show()
        }

        actRestore.setOnClickListener {
            val intent = Intent(this, HomeRestoreActivity::class.java)
            startActivity(intent)
        }

        actionBuyer.setOnClickListener {
            val intent = Intent(this, RegisterBuyerActivity::class.java)
            startActivity(intent)
        }

        actionSeller.setOnClickListener {
            val intent = Intent(this, RegisterSellerActivity::class.java)
            startActivity(intent)
        }

        actionExecute.setOnClickListener {
            if (!isLoginLoading) {
                actionLogin()
            }
        }

        actionBiometric.setOnClickListener {
            if (!isLoginLoading) {
                actionBiometricLogin()
            }
        }
    }

    private fun configureBiometricLogin() {
        val biometricEnabled = BiometricSessionManager.isEnabled(this)
        val biometricRegister = BiometricSessionManager.getRegister(this)
        val biometricRole = BiometricSessionManager.getRole(this)

        actionBiometric.visibility = if (biometricEnabled && biometricRegister > 0L && biometricRole == 3L) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun actionBiometricLogin() {
        val biometricRegister = BiometricSessionManager.getRegister(this)

        if (!BiometricSessionManager.isEnabled(this) || biometricRegister <= 0L) {
            Toast.makeText(this, "No tienes la huella configurada", Toast.LENGTH_SHORT).show()
            configureBiometricLogin()
            return
        }

        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Debes configurar huella en el dispositivo", Toast.LENGTH_LONG).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    loadUserByBiometric(biometricRegister)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@HomeLoginActivity, errString, Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@HomeLoginActivity, "No se pudo validar la huella", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Ingresar con huella")
            .setSubtitle("Confirma tu huella para iniciar sesión")
            .setNegativeButtonText("Cancelar")
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun loadUserByBiometric(userRegister: Long) {
        setLoginLoading(true)

        collection.document(userRegister.toString())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)

                if (user == null || !user.biometric || user.idRole != 3L) {
                    setLoginLoading(false)
                    BiometricSessionManager.clear(this)
                    configureBiometricLogin()
                    Toast.makeText(this, "La huella ya no está activa para esta cuenta", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                SessionManager.save(this, user)
                redirectByRole(user)
            }
            .addOnFailureListener { exception ->
                setLoginLoading(false)
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun actionLogin() {
        val email = fieldEmail.text.toString().trim()
        val password = fieldPassword.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el correo", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        setLoginLoading(true)

        val encryptedPassword = encryptPassword(password)

        collection
            .whereEqualTo("email", email)
            .whereEqualTo("password", encryptedPassword)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    setLoginLoading(false)
                    Toast.makeText(
                        this,
                        "Correo o contraseña incorrectos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                val user = result.documents.firstOrNull()
                    ?.toObject(UserModel::class.java)

                if (user == null) {
                    setLoginLoading(false)
                    Toast.makeText(
                        this,
                        "No se pudo obtener la información del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                SessionManager.save(this, user)
                redirectByRole(user)
            }
            .addOnFailureListener { exception ->
                setLoginLoading(false)
                Toast.makeText(
                    this,
                    "Error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()

                exception.printStackTrace()
            }
    }

    private fun setLoginLoading(isLoading: Boolean) {
        isLoginLoading = isLoading
        actionExecute.isEnabled = !isLoading
        actionBiometric.isEnabled = !isLoading
        fieldEmail.isEnabled = !isLoading
        fieldPassword.isEnabled = !isLoading
        actRestore.isEnabled = !isLoading
        actionBuyer.isEnabled = !isLoading
        actionSeller.isEnabled = !isLoading
        loaderLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        actionExecute.text = if (isLoading) "Validando..." else "Iniciar Sesión"
    }

    private fun redirectByRole(user: UserModel) {
        val intent = when (user.idRole) {
            1L -> Intent(this, AdminDashboardActivity::class.java)
            2L -> Intent(this, SellerDashboardActivity::class.java)
            3L -> Intent(this, HomeProductActivity::class.java)
            else -> null
        }

        if (intent == null) {
            setLoginLoading(false)
            Toast.makeText(this, "Rol no autorizado", Toast.LENGTH_SHORT).show()
            return
        }

        intent.putExtra("userRegister", user.register)
        intent.putExtra("userEmail", user.email)
        intent.putExtra("userRole", user.idRole)

        startActivity(intent)
        finish()
    }

    private fun encryptPassword(password: String): String {
        val salt = "com.compensar.tienda.user.password"

        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }
}

