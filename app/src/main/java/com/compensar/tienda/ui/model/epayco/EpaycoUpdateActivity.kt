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

class EpaycoUpdateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldApi: EditText
    private lateinit var fieldState: EditText
    private lateinit var fieldIdOrder: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("epayco")
    private var register: Long = 0
    private var current: EpaycoModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_epayco_update)
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
        ModuleView.bindTitle(titleHeader, "Actualizar", "epayco", "Epayco")
        fieldApi = findViewById(R.id.fieldApi)
        fieldState = findViewById(R.id.fieldState)
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
                    current = document.toObject(EpaycoModel::class.java)
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
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdOrder,
            collectionName = "order",
            labelFields = listOf("reference"),
            selectedId = data.idOrder
        )
    }

    private fun actionOperate() {
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
