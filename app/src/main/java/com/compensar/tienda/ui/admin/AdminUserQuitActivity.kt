package com.compensar.tienda.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ImagePreviewHelper
import com.compensar.tienda.ui.model.common.FirestoreRelationLabelHelper
import com.compensar.tienda.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [AdminUserQuitActivity].
 *
 * Responsable de la logica asociada al pantalla del panel administrativo.
 */
class AdminUserQuitActivity : AppCompatActivity() {
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

    /**
     * Se ejecuta al crear la pantalla.
     * Inicializa vista, estado y eventos principales.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_user_quit)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadRegister()
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        fieldRegister = findViewById(R.id.fieldRegister)
        fieldNames = findViewById(R.id.fieldNames)
        fieldSurnames = findViewById(R.id.fieldSurnames)
        fieldEmail = findViewById(R.id.fieldEmail)
        fieldIdRole = findViewById(R.id.fieldIdRole)
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initEvents() {

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

    /**
     * Carga informacion desde origen local o remoto.
     */
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

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun showRegister() {
        val currentUser = user ?: return

        ImagePreviewHelper.addPreviewToCard(this, findViewById(R.id.cardUserDeleteInfo), currentUser.storefire)

        fieldRegister.text = currentUser.register.toString()
        fieldNames.text = currentUser.names ?: ""
        fieldSurnames.text = currentUser.srnms ?: ""
        fieldEmail.text = currentUser.email ?: ""
        FirestoreRelationLabelHelper.load(fieldIdRole, "role", currentUser.idRole, listOf("name"))
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
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

