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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.dashboard.DashboardSellerActivity
import com.compensar.tienda.ui.register.RegisterBuyerActivity
import com.compensar.tienda.ui.register.RegisterSellerActivity
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

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
            1L -> Intent(this, DashboardAdminActivity::class.java)
            2L -> Intent(this, DashboardSellerActivity::class.java)
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