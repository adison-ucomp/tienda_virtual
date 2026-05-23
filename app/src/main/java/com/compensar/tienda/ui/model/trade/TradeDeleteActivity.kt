package com.compensar.tienda.ui.model.trade

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.TradeModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreRelationLabelHelper
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [TradeDeleteActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class TradeDeleteActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldRegister: TextView
    private lateinit var fieldApi: TextView
    private lateinit var fieldState: TextView
    private lateinit var fieldIdGateway: TextView
    private lateinit var fieldIdOrder: TextView

    private val collection = FirebaseFirestore.getInstance().collection("trade")
    private var register: Long = 0
    private var current: TradeModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_trade_delete)
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
        ModuleView.bindTitle(titleHeader, "Eliminar", "trade", "Transacciones")
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldApi = findViewById(R.id.fieldApi)
        fieldState = findViewById(R.id.fieldState)
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
    }

    private fun showRegister() {
        val data = current ?: return
        fieldRegister.text = data.register.toString()
        fieldApi.text = data.api ?: "-"
        fieldState.text = data.state ?: "-"
        FirestoreRelationLabelHelper.load(fieldIdGateway, "gateway", data.idGateway, listOf("name"))
        FirestoreRelationLabelHelper.load(fieldIdOrder, "order", data.idOrder, listOf("reference"))
    }

    private fun actionOperate() {
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

