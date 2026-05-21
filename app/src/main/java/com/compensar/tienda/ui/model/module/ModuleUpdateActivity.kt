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

class ModuleUpdateActivity : AppCompatActivity() {
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

    private var register: Long = 0
    private var data: ModuleModel? = null

    private val stateLabels = listOf("Activo", "Apagado")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_module_update)
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
        fieldName = findViewById(R.id.fieldName)
        fieldModel = findViewById(R.id.fieldModel)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldState = findViewById(R.id.fieldState)

        fieldState.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stateLabels
        )

        ModuleView.bindTitle(titleHeader, "Actualizar", "module", "Modulos")
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

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    data = document.toObject(ModuleModel::class.java)
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
        fieldName.setText(current.name.toString())
        fieldModel.setText(current.model.toString())
        fieldDetail.setText(current.detail.toString())
        fieldState.setSelection(if (current.state == false) 1 else 0)
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = ModuleModel(
            register = register,
            name = fieldName.text.toString().trim().ifEmpty { null },
            model = fieldModel.text.toString().trim().ifEmpty { null },
            detail = fieldDetail.text.toString().trim().ifEmpty { null },
            state = fieldState.selectedItemPosition == 0
        )

        collection.document(register.toString())
            .set(updatedData)
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
