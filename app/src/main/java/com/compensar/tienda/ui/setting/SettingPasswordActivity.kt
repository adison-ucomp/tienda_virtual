package com.compensar.tienda.ui.setting

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class SettingPasswordActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var fieldCurrentPassword: EditText
    private lateinit var fieldNewPassword: EditText
    private lateinit var fieldConfirmPassword: EditText
    private lateinit var actionUpdatePassword: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_password)
        SessionNavigation.bindProfile(this)
        SessionNavigation.applyBuyerInferiorVisibility(this)

        initViews()
        initEvents()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        fieldCurrentPassword = findViewById(R.id.fieldCurrentPassword)
        fieldNewPassword = findViewById(R.id.fieldNewPassword)
        fieldConfirmPassword = findViewById(R.id.fieldConfirmPassword)
        actionUpdatePassword = findViewById(R.id.actionUpdatePassword)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionUpdatePassword.setOnClickListener { updatePassword() }
    }

    private fun updatePassword() {
        val userRegister = SessionManager.getRegister(this)
        val currentPassword = fieldCurrentPassword.text.toString().trim()
        val newPassword = fieldNewPassword.text.toString().trim()
        val confirmPassword = fieldConfirmPassword.text.toString().trim()

        if (userRegister <= 0L) {
            Toast.makeText(this, "No se encontró la sesión del usuario", Toast.LENGTH_LONG).show()
            return
        }

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "La nueva contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "La confirmación no coincide", Toast.LENGTH_SHORT).show()
            return
        }

        userCollection.document(userRegister.toString())
            .get()
            .addOnSuccessListener { document ->
                val savedPassword = document.getString("password").orEmpty()

                if (savedPassword != encryptPassword(currentPassword)) {
                    Toast.makeText(this, "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                userCollection.document(userRegister.toString())
                    .update("password", encryptPassword(newPassword))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun encryptPassword(password: String): String {
        val salt = "com.compensar.tienda.user.password"

        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }
}
