package com.compensar.tienda.ui.model.epayco

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.EpaycoModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EpaycoCreateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldApi: EditText
    private lateinit var fieldState: EditText
    private lateinit var fieldIdOrder: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("epayco")
    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_epayco_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSelectors()
        loadNextRegister()
    }

    private fun initViews() {
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        ModuleView.bindTitle(titleHeader, "Crear", "epayco", "Epayco")
        fieldApi = findViewById(R.id.fieldApi)
        fieldState = findViewById(R.id.fieldState)
        fieldIdOrder = findViewById(R.id.fieldIdOrder)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadSelectors() {
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdOrder,
            collectionName = "order",
            labelFields = listOf("reference"),
            selectedId = 0
        )
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
            }
    }

    private fun actionOperate() {
        val register = generatedRegister
        if (register == null || register <= 0) {
            Toast.makeText(this, "No fue posible generar el ID automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return
        }

        val idOrder = FirestoreSelectHelper.getSelectedId(fieldIdOrder)
        if (idOrder == null) {
            Toast.makeText(this, "Debe seleccionar una orden válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = EpaycoModel(
            register = register,
            api = fieldApi.text.toString().trim().ifEmpty { null },
            state = fieldState.text.toString().trim().ifEmpty { null },
            idOrder = idOrder
        )

        collection.document(register.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "El ID automático ya existe. Intentando generar otro ID.", Toast.LENGTH_SHORT).show()
                    loadNextRegister()
                } else {
                    collection.document(register.toString()).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro creado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error al guardar: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
    }
}
