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

class ShipmentUpdateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldName: EditText

    private val collection = FirebaseFirestore.getInstance().collection("shipment")
    private var register: Long = 0
    private var data: ShipmentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_shipment_update)
        SessionNavigation.bindProfile(this)
        register = intent.getLongExtra("register", 0)
        initViews()
        initEvents()
        loadRegister()
    }

    private fun initViews() {
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Actualizar", "shipment", "Envios")
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadRegister() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        collection.document(register.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    data = document.toObject(ShipmentModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontró el registro", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showRegister() {
        val current = data ?: return
        fieldName.setText(current.name.orEmpty())
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }
        val name = fieldName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }
        collection.document(register.toString()).set(ShipmentModel(register = register, name = name))
            .addOnSuccessListener {
                Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}
