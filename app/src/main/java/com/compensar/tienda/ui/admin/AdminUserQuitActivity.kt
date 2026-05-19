package com.compensar.tienda.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminUserQuitActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: TextView
    private lateinit var fieldNames: TextView
    private lateinit var fieldSurnames: TextView
    private lateinit var fieldEmail: TextView
    private lateinit var fieldIdRole: TextView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("user")

    private var register: Long = 0
    private var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_user_quit)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadRegister()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        fieldRegister = findViewById(R.id.fieldRegister)
        fieldNames = findViewById(R.id.fieldNames)
        fieldSurnames = findViewById(R.id.fieldSurnames)
        fieldEmail = findViewById(R.id.fieldEmail)
        fieldIdRole = findViewById(R.id.fieldIdRole)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, DashboardAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener {
            finish()
        }

        actionCancel.setOnClickListener {
            finish()
        }

        actionExecute.setOnClickListener {
            actionOperate()
        }
    }

    private fun loadRegister() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    user = document.toObject(UserModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showRegister() {
        val currentUser = user ?: return

        fieldRegister.text = currentUser.register.toString()
        fieldNames.text = currentUser.names ?: ""
        fieldSurnames.text = currentUser.srnms ?: ""
        fieldEmail.text = currentUser.email ?: ""
        fieldIdRole.text = currentUser.idRole.toString()
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}
