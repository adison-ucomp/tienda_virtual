package com.compensar.tienda.ui.model.module

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.ModuleModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Clase [ModuleCreateActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class ModuleCreateActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldName: EditText
    private lateinit var fieldModel: EditText
    private lateinit var fieldDetail: EditText
    private lateinit var fieldState: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("module")

    private var generatedRegister: Long? = null

    private val stateLabels = listOf("Activo", "Apagado")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_module_create)
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
        fieldName = findViewById(R.id.fieldName)
        fieldModel = findViewById(R.id.fieldModel)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldState = findViewById(R.id.fieldState)

        fieldState.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stateLabels
        )

        ModuleView.bindTitle(titleHeader, "Crear", "module", "Modulos")
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

        val data = ModuleModel(
            register = register,
            name = fieldName.text.toString().trim().ifEmpty { null },
            model = fieldModel.text.toString().trim().ifEmpty { null },
            detail = fieldDetail.text.toString().trim().ifEmpty { null },
            state = fieldState.selectedItemPosition == 0
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

    private fun saveRegister(data: ModuleModel) {
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

