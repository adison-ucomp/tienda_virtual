package com.compensar.tienda.ui.model.shipment

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.ShipmentModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ShipmentCreateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldName: EditText

    private val collection = FirebaseFirestore.getInstance().collection("shipment")
    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_shipment_create)
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

        ModuleView.bindTitle(titleHeader, "Crear", "shipment", "Envios")
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadNextRegister() {
        actionExecute.isEnabled = false
        collection.orderBy("register", Query.Direction.DESCENDING).limit(1).get()
            .addOnSuccessListener { result ->
                generatedRegister = (result.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                actionExecute.isEnabled = true
            }
            .addOnFailureListener { exception ->
                generatedRegister = null
                actionExecute.isEnabled = true
                Toast.makeText(this, "Error al generar ID automático: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun actionOperate() {
        val register = generatedRegister
        if (register == null || register <= 0) {
            Toast.makeText(this, "No fue posible generar el ID automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return
        }

        val name = fieldName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val data = ShipmentModel(register = register, name = name)
        collection.document(register.toString()).get()
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

    private fun saveRegister(data: ShipmentModel) {
        collection.document(data.register.toString()).set(data)
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
