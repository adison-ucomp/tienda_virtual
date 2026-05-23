package com.compensar.tienda.ui.model.shipment

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.ShipmentModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [ShipmentDeleteActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class ShipmentDeleteActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldRegister: TextView
    private lateinit var fieldName: TextView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shipment")

    private var register: Long = 0
    private var data: ShipmentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_shipment_delete)
        SessionNavigation.bindProfile(this)
        register = intent.getLongExtra("register", 0)
        initViews()
        initEvents()
        load()
    }

    private fun initViews() {
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Eliminar", "shipment", "Envios")
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun load() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no valido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    data = document.toObject(ShipmentModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontro el registro", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showRegister() {
        val current = data ?: return
        fieldRegister.text = current.register.toString()
        fieldName.text = current.name.toString()
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no valido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}

