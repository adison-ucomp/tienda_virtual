package com.compensar.tienda.ui.model.trade

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.TradeModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [TradeUpdateActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class TradeUpdateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldApi: EditText
    private lateinit var fieldState: EditText
    private lateinit var fieldReference: EditText
    private lateinit var fieldIdGateway: Spinner
    private lateinit var fieldIdOrder: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("trade")
    private var register: Long = 0
    private var current: TradeModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_trade_update)
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
        ModuleView.bindTitle(titleHeader, "Actualizar", "trade", "Transacciones")
        fieldApi = findViewById(R.id.fieldApi)
        fieldState = findViewById(R.id.fieldState)
        fieldReference = findViewById(R.id.fieldReference)
        fieldIdGateway = findViewById(R.id.fieldIdGateway)
        fieldIdOrder = findViewById(R.id.fieldIdOrder)
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
                    current = document.toObject(TradeModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontró el registro", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showRegister() {
        val data = current ?: return
        fieldApi.setText(data.api.orEmpty())
        fieldState.setText(data.state.orEmpty())
        fieldReference.setText(data.reference.orEmpty())
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdGateway,
            collectionName = "gateway",
            labelFields = listOf("name"),
            selectedId = data.idGateway
        )
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdOrder,
            collectionName = "order",
            labelFields = listOf("reference"),
            selectedId = data.idOrder
        )
    }

    private fun actionOperate() {
        val idGateway = FirestoreSelectHelper.getSelectedId(fieldIdGateway)
        val idOrder = FirestoreSelectHelper.getSelectedId(fieldIdOrder)
        if (idGateway == null) {
            Toast.makeText(this, "Debe seleccionar una pasarela válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (idOrder == null) {
            Toast.makeText(this, "Debe seleccionar una orden válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = TradeModel(
            register = register,
            api = fieldApi.text.toString().trim().ifEmpty { null },
            state = fieldState.text.toString().trim().ifEmpty { null },
            reference = fieldReference.text.toString().trim().ifEmpty { null },
            idGateway = idGateway,
            idOrder = idOrder
        )

        collection.document(register.toString()).set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}

