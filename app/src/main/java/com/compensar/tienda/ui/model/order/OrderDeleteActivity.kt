package com.compensar.tienda.ui.model.order

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class OrderDeleteActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var txtInfo: TextView

    private val collection = FirebaseFirestore.getInstance().collection("order")
    private var register: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_delete)
        SessionNavigation.bindProfile(this)
        register = intent.getLongExtra("register", 0)
        initViews()
        initEvents()
        load()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        txtInfo = findViewById(R.id.txtInfo)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun load() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no valido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString()).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Toast.makeText(this, "No se encontro el registro", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                val data = document.toObject(OrderModel::class.java)
                txtInfo.text = "Registro: ${data?.register}\n" +
                    "Referencia: ${data?.reference}\n" +
                    "Direccion: ${data?.address}\n" +
                    "Total: ${data?.total}\n" +
                    "Fecha: ${data?.date}\n" +
                    "Hora: ${data?.hour}\n" +
                    "Estado: ${data?.idShipment}\n" +
                    "Usuario: ${data?.idUser}"
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no valido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Orden eliminada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
