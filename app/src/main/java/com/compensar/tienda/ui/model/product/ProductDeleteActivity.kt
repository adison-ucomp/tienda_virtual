package com.compensar.tienda.ui.model.product

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreRelationLabelHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.compensar.tienda.domain.model.ProductModel

class ProductDeleteActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: TextView
    private lateinit var fieldName: TextView
    private lateinit var fieldDetail: TextView
    private lateinit var fieldStock: TextView
    private lateinit var fieldPrice: TextView
    private lateinit var fieldStorefire: TextView
    private lateinit var fieldIdCategory: TextView
    private lateinit var fieldIdShop: TextView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("product")

    private var register: Long = 0
    private var data: ProductModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_product_delete)
        SessionNavigation.bindProfile(this)

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
        fieldName = findViewById(R.id.fieldName)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldStock = findViewById(R.id.fieldStock)
        fieldPrice = findViewById(R.id.fieldPrice)
        fieldStorefire = findViewById(R.id.fieldStorefire)
        fieldIdCategory = findViewById(R.id.fieldIdCategory)
        fieldIdShop = findViewById(R.id.fieldIdShop)
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
                    data = document.toObject(ProductModel::class.java)
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
        fieldName.text = current.name.toString()
        fieldDetail.text = current.detail.toString()
        fieldStock.text = current.stock.toString()
        fieldPrice.text = current.price.toString()
        fieldStorefire.text = current.storefire.toString()
        FirestoreRelationLabelHelper.load(fieldIdCategory, "category", current.idCategory, listOf("name"))
        FirestoreRelationLabelHelper.load(fieldIdShop, "shop", current.idShop, listOf("name"))
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString())
            .delete()
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
