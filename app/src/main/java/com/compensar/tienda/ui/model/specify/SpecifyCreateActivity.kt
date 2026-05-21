package com.compensar.tienda.ui.model.specify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.compensar.tienda.model.SpecifyModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.google.firebase.firestore.FirebaseFirestore

class SpecifyCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldName: EditText
    private lateinit var fieldDetail: EditText
    private lateinit var fieldIdProduct: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("specify")

    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_specify_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSelectors()
        loadNextRegister()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Crear", "specify", "Especifaciones")

        fieldName = findViewById(R.id.fieldName)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldIdProduct = findViewById(R.id.fieldIdProduct)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadSelectors() {
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdProduct,
            collectionName = "product",
            labelFields = listOf("name"),
            selectedId = 0
        )
    }

    private fun actionOperate() {
        val register = generatedRegister

        if (register == null || register <= 0) {
            Toast.makeText(this, "No fue posible generar el ID automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return
        }

        val name = fieldName.text.toString().trim()
        val detail = fieldDetail.text.toString().trim().ifEmpty { null }

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar name", Toast.LENGTH_SHORT).show()
            return
        }

        val idProduct = FirestoreSelectHelper.getSelectedId(fieldIdProduct)

        if (idProduct == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = SpecifyModel(
            register = register,
            name = name,
            detail = detail,
            idProduct = idProduct
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
            .get()
            .addOnSuccessListener { result ->
                val lastRegister = result.documents.maxOfOrNull { document ->
                    val byField = document.getLong("register") ?: 0L
                    val byDocId = document.id.toLongOrNull() ?: 0L
                    maxOf(byField, byDocId)
                } ?: 0L

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

    private fun saveRegister(data: SpecifyModel) {
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
