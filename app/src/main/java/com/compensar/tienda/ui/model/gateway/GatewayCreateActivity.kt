package com.compensar.tienda.ui.model.gateway

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.compensar.tienda.model.GatewayModel

/**
 * Clase [GatewayCreateActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class GatewayCreateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldName: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("gateway")

    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_gateway_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadNextRegister()
    }

    private fun initViews() {
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Crear", "gateway", "Pasarelas")
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun actionOperate() {

        val register = generatedRegister

        if (register == null || register <= 0) {
            Toast.makeText(this, "No fue posible generar el ID automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return
        }

        val data = GatewayModel(
            register = register,
            name = fieldName.text.toString().trim().ifEmpty { null }
        )

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "El ID automático ya existe. Intentando generar otro ID.", Toast.LENGTH_SHORT).show()
                    loadNextRegister()
                } else {
                    saveRegister(data)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun loadNextRegister() {
        actionExecute.isEnabled = false

        collection
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val lastRegister = result.documents
                    .firstOrNull()
                    ?.getLong("register")
                    ?: 0L

                generatedRegister = lastRegister + 1L
                actionExecute.isEnabled = true
            }
            .addOnFailureListener { exception ->
                generatedRegister = null
                actionExecute.isEnabled = true
                Toast.makeText(this, "Error al generar ID automático: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveRegister(data: GatewayModel) {
        collection.document(data.register.toString())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro creado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}

