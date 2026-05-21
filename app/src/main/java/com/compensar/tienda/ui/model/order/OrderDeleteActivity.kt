package com.compensar.tienda.ui.model.order

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore

class OrderDeleteActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: TextView
    private lateinit var fieldReference: TextView
    private lateinit var fieldAddress: TextView
    private lateinit var fieldTotal: TextView
    private lateinit var fieldDate: TextView
    private lateinit var fieldHour: TextView

    private val collection = FirebaseFirestore.getInstance().collection("order")
    private var register: Long = 0
    private var data: OrderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_delete)
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

        ModuleView.bindTitle(titleHeader, "Eliminar", "order", "Ordenes")
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldReference = findViewById(R.id.fieldReference)
        fieldAddress = findViewById(R.id.fieldAddress)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldDate = findViewById(R.id.fieldDate)
        fieldHour = findViewById(R.id.fieldHour)
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
        fieldRegister.text = current.register.toString()
        fieldReference.text = current.reference ?: "-"
        fieldAddress.text = current.address ?: "-"
        fieldTotal.text = current.total?.toString() ?: "-"
        fieldDate.text = current.date ?: "-"
        fieldHour.text = current.hour ?: "-"
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}
