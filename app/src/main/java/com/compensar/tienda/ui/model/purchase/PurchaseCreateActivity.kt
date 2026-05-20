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
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.domain.model.PurchaseModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PurchaseCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldDate: EditText
    private lateinit var fieldHour: EditText
    private lateinit var fieldAmount: EditText
    private lateinit var fieldValue: EditText
    private lateinit var fieldTotal: EditText
    private lateinit var fieldIdProduct: Spinner
    private lateinit var fieldIdMethod: Spinner
    private lateinit var fieldIdGangway: Spinner
    private lateinit var fieldIdUser: Spinner
    private lateinit var fieldIdOrder: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("purchase")

    private var generatedRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_purchase_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSelectors()
        loadNextRegister()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        fieldDate = findViewById(R.id.fieldDate)
        fieldHour = findViewById(R.id.fieldHour)
        fieldAmount = findViewById(R.id.fieldAmount)
        fieldValue = findViewById(R.id.fieldValue)
        fieldTotal = findViewById(R.id.fieldTotal)
        fieldIdProduct = findViewById(R.id.fieldIdProduct)
        fieldIdMethod = findViewById(R.id.fieldIdMethod)
        fieldIdGangway = findViewById(R.id.fieldIdGangway)
        fieldIdUser = findViewById(R.id.fieldIdUser)
        fieldIdOrder = findViewById(R.id.fieldIdOrder)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, DashboardAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadSelectors() {
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdProduct,
            collectionName = "product",
            labelFields = listOf("name"),
            selectedId = 0
        )

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdMethod,
            collectionName = "payment",
            labelFields = listOf("name"),
            selectedId = 0
        )

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdGangway,
            collectionName = "gateway",
            labelFields = listOf("name"),
            selectedId = 0
        )

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdUser,
            collectionName = "user",
            labelFields = listOf("names", "srnms", "email"),
            selectedId = 0
        )

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdOrder,
            collectionName = "order",
            labelFields = listOf("reference", "address"),
            selectedId = 0
        )
    }

    private fun actionOperate() {
        val register = generatedRegister

        if (register == null || register <= 0) {
            Toast.makeText(this, "No fue posible generar el ID automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return
        }

        val date = fieldDate.text.toString().trim().ifEmpty { null }
        val hour = fieldHour.text.toString().trim().ifEmpty { null }
        val amount = fieldAmount.text.toString().trim().toIntOrNull()
        val value = fieldValue.text.toString().trim().toDoubleOrNull()
        val total = fieldTotal.text.toString().trim().toDoubleOrNull()



        val idProduct = FirestoreSelectHelper.getSelectedId(fieldIdProduct)
        val idMethod = FirestoreSelectHelper.getSelectedId(fieldIdMethod)
        val idGangway = FirestoreSelectHelper.getSelectedId(fieldIdGangway)
        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)
        val idOrder = FirestoreSelectHelper.getSelectedId(fieldIdOrder)

        if (idProduct == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }
        if (idMethod == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }
        if (idGangway == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }
        if (idUser == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }
        if (idOrder == null) {
            Toast.makeText(this, "Debe seleccionar una orden válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = PurchaseModel(
            register = register,
            date = date,
            hour = hour,
            amount = amount,
            value = value,
            total = total,
            idProduct = idProduct,
            idMethod = idMethod,
            idGangway = idGangway,
            idUser = idUser,
            idOrder = idOrder
        )

        collection.document(register.toString())
            .get()
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

    private fun loadNextRegister() {
        actionExecute.isEnabled = false

        collection
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val lastRegister = result.documents
                    .firstOrNull()
                    ?.getLong("register")
                    ?: 0L

                generatedRegister = lastRegister + 1L
                actionExecute.isEnabled = true
            }
            .addOnFailureListener { exception ->
                generatedRegister = null
                actionExecute.isEnabled = true
                Toast.makeText(this, "Error al generar ID automático: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveRegister(data: PurchaseModel) {
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
