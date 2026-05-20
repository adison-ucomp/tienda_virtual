package com.compensar.tienda.ui.model.order

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrderCreateActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldAddress: EditText
    private lateinit var fieldReference: EditText
    private lateinit var fieldTotal: EditText
    private lateinit var fieldDate: EditText
    private lateinit var fieldHour: EditText
    private lateinit var fieldIdShipment: Spinner
    private lateinit var fieldIdUser: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("order")
    private var generatedRegister: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_create)
        initViews()
        initEvents()
        loadNextRegister()
        FirestoreSelectHelper.load(this, fieldIdShipment, "shipment", listOf("name"), 1)
        FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names", "srnms", "email"), 0)
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        fieldAddress = findViewById(R.id.fieldAddress)
        fieldReference = findViewById(R.id.fieldReference)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldDate = findViewById(R.id.fieldDate)
        fieldHour = findViewById(R.id.fieldHour)
        fieldIdShipment = findViewById(R.id.fieldIdShipment)
        fieldIdUser = findViewById(R.id.fieldIdUser)

        fieldReference.setText(UUID.randomUUID().toString().replace("-", "").take(12).uppercase())
        fieldDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        fieldHour.setText(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { save() }
    }

    private fun loadNextRegister() {
        actionExecute.isEnabled = false
        collection.orderBy("register", Query.Direction.DESCENDING).limit(1).get()
            .addOnSuccessListener {
                generatedRegister = (it.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                actionExecute.isEnabled = true
            }
            .addOnFailureListener { actionExecute.isEnabled = true }
    }

    private fun save() {
        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)
        val idShipment = FirestoreSelectHelper.getSelectedId(fieldIdShipment)
        if (idUser == null) {
            Toast.makeText(this, "Debe seleccionar usuario", Toast.LENGTH_SHORT).show()
            return
        }
        if (idShipment == null) {
            Toast.makeText(this, "Debe seleccionar estado", Toast.LENGTH_SHORT).show()
            return
        }

        val data = OrderModel(
            register = generatedRegister,
            address = fieldAddress.text.toString().trim(),
            reference = fieldReference.text.toString().trim(),
            total = fieldTotal.text.toString().toDoubleOrNull(),
            date = fieldDate.text.toString().trim(),
            hour = fieldHour.text.toString().trim(),
            idShipment = idShipment,
            idUser = idUser
        )

        collection.document(generatedRegister.toString()).set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Orden creada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
    }
}
