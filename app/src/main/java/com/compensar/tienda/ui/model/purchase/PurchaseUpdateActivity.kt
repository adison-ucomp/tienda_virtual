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
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore

class PurchaseUpdateActivity : AppCompatActivity() {
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
    private var register: Long = 0
    private var currentData: PurchaseModel? = null

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
            Toast.makeText(this, "Registro no valido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentData = document.toObject(PurchaseModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontro el registro", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showRegister() {
        val data = currentData ?: return

        fieldAmount.setText(data.amount?.toString() ?: "")
        fieldValue.setText(data.value?.toString() ?: "")
        fieldTotal.setText(data.total?.toString() ?: "")
        FirestoreSelectHelper.load(this, fieldIdProduct, "product", listOf("name"), data.idProduct)
        FirestoreSelectHelper.load(this, fieldIdMethod, "payment", listOf("name"), data.idMethod)
        FirestoreSelectHelper.load(this, fieldIdGangway, "gateway", listOf("name"), data.idGangway)
        FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names", "srnms", "email"), data.idUser)
        FirestoreSelectHelper.load(this, fieldIdOrder, "order", listOf("reference", "address"), data.idOrder)
    }

    private fun actionOperate() {
        val idProduct = FirestoreSelectHelper.getSelectedId(fieldIdProduct)
        val idMethod = FirestoreSelectHelper.getSelectedId(fieldIdMethod)
        val idGangway = FirestoreSelectHelper.getSelectedId(fieldIdGangway)
        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)
        val idOrder = FirestoreSelectHelper.getSelectedId(fieldIdOrder)

        if (idProduct == null || idMethod == null || idGangway == null || idUser == null || idOrder == null) {
            Toast.makeText(this, "Debe seleccionar producto, metodo, pasarela, usuario y orden", Toast.LENGTH_SHORT).show()
            return
        }

        val data = PurchaseModel(
            register = register,
            amount = fieldAmount.text.toString().trim().toIntOrNull(),
            value = fieldValue.text.toString().trim().toDoubleOrNull(),
            total = fieldTotal.text.toString().trim().toDoubleOrNull(),
            idProduct = idProduct,
            idMethod = idMethod,
            idGangway = idGangway,
            idUser = idUser,
            idOrder = idOrder
        )

        collection.document(register.toString())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
