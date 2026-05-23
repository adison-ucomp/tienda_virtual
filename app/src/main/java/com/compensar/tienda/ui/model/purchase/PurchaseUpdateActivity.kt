package com.compensar.tienda.ui.model.purchase

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
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [PurchaseUpdateActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class PurchaseUpdateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldAmount: EditText
    private lateinit var fieldValue: EditText
    private lateinit var fieldTotal: EditText
    private lateinit var fieldIdOrder: Spinner
    private lateinit var fieldIdProduct: Spinner
    private lateinit var fieldIdUser: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("purchase")
    private var register: Long = 0
    private var data: PurchaseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_purchase_update)
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

        ModuleView.bindTitle(titleHeader, "Actualizar", "purchase", "Compras")
        fieldAmount = findViewById(R.id.fieldAmount)
        fieldValue = findViewById(R.id.fieldValue)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldIdOrder = findViewById(R.id.fieldIdOrder)
        fieldIdProduct = findViewById(R.id.fieldIdProduct)
        fieldIdUser = findViewById(R.id.fieldIdUser)
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

    private fun loadRegister() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString()).get()
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
        fieldAmount.setText(current.amount?.toString() ?: "")
        fieldValue.setText(current.value?.toString() ?: "")
        fieldTotal.setText(current.total?.toString() ?: "")
        FirestoreSelectHelper.load(this, fieldIdOrder, "order", listOf("reference"), current.idOrder)
        FirestoreSelectHelper.load(this, fieldIdProduct, "product", listOf("name"), current.idProduct)
        FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names", "srnms", "email"), current.idUser)
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = fieldAmount.text.toString().trim().toIntOrNull()
        val value = fieldValue.text.toString().trim().toDoubleOrNull()
        val total = fieldTotal.text.toString().trim().toDoubleOrNull()
        val idOrder = FirestoreSelectHelper.getSelectedId(fieldIdOrder)
        val idProduct = FirestoreSelectHelper.getSelectedId(fieldIdProduct)
        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)

        if (amount == null || value == null || total == null) {
            Toast.makeText(this, "Debes ingresar cantidad, valor y total", Toast.LENGTH_SHORT).show()
            return
        }

        if (idOrder == null || idProduct == null || idUser == null) {
            Toast.makeText(this, "Debe seleccionar referencia, producto y usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = PurchaseModel(
            register = register,
            amount = amount,
            value = value,
            total = total,
            idOrder = idOrder,
            idProduct = idProduct,
            idUser = idUser
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

