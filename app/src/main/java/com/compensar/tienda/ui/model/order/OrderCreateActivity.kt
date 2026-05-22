package com.compensar.tienda.ui.model.order

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
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.model.common.ModuleView
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrderCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldReference: EditText
    private lateinit var fieldAddress: EditText
    private lateinit var fieldTotal: EditText
    private lateinit var fieldDate: EditText
    private lateinit var fieldHour: EditText
    private lateinit var fieldIdShop: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("order")
    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initDefaultValues()
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

        ModuleView.bindTitle(titleHeader, "Crear", "order", "Ordenes")
        fieldReference = findViewById(R.id.fieldReference)
        fieldAddress = findViewById(R.id.fieldAddress)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldDate = findViewById(R.id.fieldDate)
        fieldHour = findViewById(R.id.fieldHour)
        fieldIdShop = findViewById(R.id.fieldIdShop)
    }

    private fun initDefaultValues() {
        fieldReference.setText(UUID.randomUUID().toString().replace("-", "").take(12).uppercase())
        fieldDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        fieldHour.setText(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
    }

    private fun loadSelectors() {
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdShop,
            collectionName = "shop",
            labelFields = listOf("name"),
            selectedId = 0
        )
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
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

        val reference = fieldReference.text.toString().trim()
        val address = fieldAddress.text.toString().trim()
        val total = fieldTotal.text.toString().trim().toDoubleOrNull()
        val date = fieldDate.text.toString().trim()
        val hour = fieldHour.text.toString().trim()
        val idShop = FirestoreSelectHelper.getSelectedId(fieldIdShop)

        if (idShop == null) {
            Toast.makeText(this, "Debe seleccionar una tienda válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (reference.isEmpty() || address.isEmpty() || total == null || date.isEmpty() || hour.isEmpty()) {
            Toast.makeText(this, "Debes ingresar referencia, dirección, total, fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        val data = OrderModel(
            register = register,
            reference = reference,
            address = address,
            total = total,
            date = date,
            hour = hour,
            idShop = idShop
        )

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

    private fun saveRegister(data: OrderModel) {
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
