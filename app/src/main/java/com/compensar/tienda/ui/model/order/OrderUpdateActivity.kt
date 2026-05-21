package com.compensar.tienda.ui.model.order

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class OrderUpdateActivity : AppCompatActivity() {
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

    private val collection = FirebaseFirestore.getInstance().collection("order")
    private var register: Long = 0
    private var data: OrderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_update)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)
        initViews()
        initEvents()
        loadRegister()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Actualizar", "order", "Ordenes")
        fieldReference = findViewById(R.id.fieldReference)
        fieldAddress = findViewById(R.id.fieldAddress)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldDate = findViewById(R.id.fieldDate)
        fieldHour = findViewById(R.id.fieldHour)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            startActivity(Intent(this, DashboardAdminActivity::class.java))
            finish()
        }
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
                    data = document.toObject(OrderModel::class.java)
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
        fieldReference.setText(current.reference.orEmpty())
        fieldAddress.setText(current.address.orEmpty())
        fieldTotal.setText(current.total?.toString() ?: "")
        fieldDate.setText(current.date.orEmpty())
        fieldHour.setText(current.hour.orEmpty())
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val reference = fieldReference.text.toString().trim()
        val address = fieldAddress.text.toString().trim()
        val total = fieldTotal.text.toString().trim().toDoubleOrNull()
        val date = fieldDate.text.toString().trim()
        val hour = fieldHour.text.toString().trim()

        if (reference.isEmpty() || address.isEmpty() || total == null || date.isEmpty() || hour.isEmpty()) {
            Toast.makeText(this, "Debes ingresar referencia, dirección, total, fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = OrderModel(
            register = register,
            reference = reference,
            address = address,
            total = total,
            date = date,
            hour = hour
        )

        collection.document(register.toString()).set(updatedData)
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
