package com.compensar.tienda.ui.model.purchase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.google.firebase.firestore.FirebaseFirestore
import com.compensar.tienda.domain.model.PurchaseModel

class PurchaseUpdateActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldDate: EditText
    private lateinit var fieldHour: EditText
    private lateinit var fieldAmount: EditText
    private lateinit var fieldValue: EditText
    private lateinit var fieldTotal: EditText
    private lateinit var fieldIdProduct: EditText
    private lateinit var fieldIdMethod: EditText
    private lateinit var fieldIdGangway: EditText
    private lateinit var fieldIdUser: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("purchase")

    private var register: Long = 0
    private var data: PurchaseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_purchase_update)

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
        fieldDate = findViewById(R.id.fieldDate)
        fieldHour = findViewById(R.id.fieldHour)
        fieldAmount = findViewById(R.id.fieldAmount)
        fieldValue = findViewById(R.id.fieldValue)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldIdProduct = findViewById(R.id.fieldIdProduct)
        fieldIdMethod = findViewById(R.id.fieldIdMethod)
        fieldIdGangway = findViewById(R.id.fieldIdGangway)
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
                    data = document.toObject(PurchaseModel::class.java)
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
        fieldDate.setText(current.date.toString())
        fieldHour.setText(current.hour.toString())
        fieldAmount.setText(current.amount.toString())
        fieldValue.setText(current.value.toString())
        fieldTotal.setText(current.total.toString())
        fieldIdProduct.setText(current.idProduct.toString())
        fieldIdMethod.setText(current.idMethod.toString())
        fieldIdGangway.setText(current.idGangway.toString())
        fieldIdUser.setText(current.idUser.toString())
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = PurchaseModel(
            register = register,
            date = fieldDate.text.toString().trim().ifEmpty { null },
            hour = fieldHour.text.toString().trim().ifEmpty { null },
            amount = fieldAmount.text.toString().trim().toIntOrNull(),
            value = fieldValue.text.toString().trim().toDoubleOrNull(),
            total = fieldTotal.text.toString().trim().toDoubleOrNull(),
            idProduct = fieldIdProduct.text.toString().trim().toLongOrNull() ?: 0,
            idMethod = fieldIdMethod.text.toString().trim().toLongOrNull() ?: 0,
            idGangway = fieldIdGangway.text.toString().trim().toLongOrNull() ?: 0,
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
