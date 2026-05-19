package com.compensar.tienda.ui.model.seller

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.google.firebase.firestore.FirebaseFirestore
import com.compensar.tienda.domain.model.SellerModel

class SellerUpdateActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldCompany: EditText
    private lateinit var fieldNit: EditText
    private lateinit var fieldAddress: EditText
    private lateinit var fieldIdUser: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("seller")

    private var register: Long = 0
    private var data: SellerModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_seller_update)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadRegister()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldCompany = findViewById(R.id.fieldCompany)
        fieldNit = findViewById(R.id.fieldNit)
        fieldAddress = findViewById(R.id.fieldAddress)
        fieldIdUser = findViewById(R.id.fieldIdUser)
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
                    data = document.toObject(SellerModel::class.java)
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
        fieldRegister.setText(current.register.toString())
        fieldCompany.setText(current.company.toString())
        fieldNit.setText(current.nit.toString())
        fieldAddress.setText(current.address.toString())
        fieldIdUser.setText(current.idUser.toString())
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = SellerModel(
            register = register,
            company = fieldCompany.text.toString().trim().ifEmpty { null },
            nit = fieldNit.text.toString().trim().ifEmpty { null },
            address = fieldAddress.text.toString().trim().ifEmpty { null },
            idUser = fieldIdUser.text.toString().trim().toLongOrNull() ?: 0
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
