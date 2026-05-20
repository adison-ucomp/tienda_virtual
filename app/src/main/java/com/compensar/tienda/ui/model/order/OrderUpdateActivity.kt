package com.compensar.tienda.ui.model.order

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore

class OrderUpdateActivity : AppCompatActivity() {
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
    private var register: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_update)
        register = intent.getLongExtra("register", 0)
        initViews()
        initEvents()
        load()
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
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { save() }
    }

    private fun load() {
        collection.document(register.toString()).get().addOnSuccessListener { doc ->
            val data = doc.toObject(OrderModel::class.java) ?: return@addOnSuccessListener
            fieldAddress.setText(data.address ?: "")
            fieldReference.setText(data.reference ?: "")
            fieldTotal.setText(data.total?.toString() ?: "")
            fieldDate.setText(data.date ?: "")
            fieldHour.setText(data.hour ?: "")
            FirestoreSelectHelper.load(this, fieldIdShipment, "shipment", listOf("name"), data.idShipment)
            FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names", "srnms", "email"), data.idUser)
        }
    }

    private fun save() {
        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)
        val idShipment = FirestoreSelectHelper.getSelectedId(fieldIdShipment)
        if (idUser == null || idShipment == null) {
            Toast.makeText(this, "Debe seleccionar usuario y estado", Toast.LENGTH_SHORT).show()
            return
        }

        val data = OrderModel(
            register = register,
            address = fieldAddress.text.toString().trim(),
            reference = fieldReference.text.toString().trim(),
            total = fieldTotal.text.toString().toDoubleOrNull(),
            date = fieldDate.text.toString().trim(),
            hour = fieldHour.text.toString().trim(),
            idShipment = idShipment,
            idUser = idUser
        )

        collection.document(register.toString()).set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Orden actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
    }
}
