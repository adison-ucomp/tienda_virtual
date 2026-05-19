package com.compensar.tienda.ui.model.gateway

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import com.compensar.tienda.domain.model.GatewayModel

class GatewayCreateActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldName: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("gateway")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_gateway_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun actionOperate() {
        val registerText = fieldRegister.text.toString().trim()
        if (registerText.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el ID", Toast.LENGTH_SHORT).show()
            return
        }

        val register = registerText.toLongOrNull()
        if (register == null || register <= 0) {
            Toast.makeText(this, "El ID no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        val data = GatewayModel(
            register = register,
            name = fieldName.text.toString().trim().ifEmpty { null }
        )

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "Ya existe un registro con ese ID", Toast.LENGTH_SHORT).show()
                } else {
                    saveRegister(data)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveRegister(data: GatewayModel) {
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
