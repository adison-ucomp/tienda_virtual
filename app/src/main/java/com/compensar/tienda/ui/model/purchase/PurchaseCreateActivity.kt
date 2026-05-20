package com.compensar.tienda.ui.model.purchase

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PurchaseCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldAmount: EditText
    private lateinit var fieldValue: EditText
    private lateinit var fieldTotal: EditText
    private lateinit var fieldIdProduct: Spinner
    private lateinit var fieldIdMethod: Spinner
    private lateinit var fieldIdGangway: Spinner
    private lateinit var fieldIdUser: Spinner
    private lateinit var fieldIdOrder: Spinner

    private val collection = FirebaseFirestore.getInstance().collection("purchase")
    private var generatedRegister: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_purchase_create)
        SessionNavigation.bindProfile(this)
        initViews()
        initEvents()
        loadNextRegister()
        loadRelations()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
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
        actionHome.setOnClickListener { startActivity(Intent(this, DashboardAdminActivity::class.java)); finish() }
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { save() }
    }

    private fun loadRelations() {
        FirestoreSelectHelper.load(this, fieldIdProduct, "product", listOf("name"), 0)
        FirestoreSelectHelper.load(this, fieldIdMethod, "payment", listOf("name"), 0)
        FirestoreSelectHelper.load(this, fieldIdGangway, "gateway", listOf("name"), 0)
        FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names", "srnms", "email"), 0)
        FirestoreSelectHelper.load(this, fieldIdOrder, "order", listOf("reference", "address"), 0)
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
        val idProduct = FirestoreSelectHelper.getSelectedId(fieldIdProduct)
        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)
        val idOrder = FirestoreSelectHelper.getSelectedId(fieldIdOrder)
        if (idProduct == null || idUser == null || idOrder == null) {
            Toast.makeText(this, "Debe seleccionar producto, usuario y orden", Toast.LENGTH_SHORT).show()
            return
        }

        val data = PurchaseModel(
            register = generatedRegister,
            amount = fieldAmount.text.toString().toIntOrNull(),
            value = fieldValue.text.toString().toDoubleOrNull(),
            total = fieldTotal.text.toString().toDoubleOrNull(),
            idProduct = idProduct,
            idMethod = FirestoreSelectHelper.getSelectedId(fieldIdMethod) ?: 0L,
            idGangway = FirestoreSelectHelper.getSelectedId(fieldIdGangway) ?: 0L,
            idUser = idUser,
            idOrder = idOrder
        )

        collection.document(generatedRegister.toString()).set(data)
            .addOnSuccessListener { Toast.makeText(this, "Compra creada", Toast.LENGTH_SHORT).show(); finish() }
            .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
    }
}
