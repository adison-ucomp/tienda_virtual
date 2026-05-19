package com.compensar.tienda.ui.model.product

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
import com.compensar.tienda.domain.model.ProductModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.platform.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProductCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldName: EditText
    private lateinit var fieldDetail: EditText
    private lateinit var fieldStock: EditText
    private lateinit var fieldPrice: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var fieldIdCategory: Spinner
    private lateinit var fieldIdShop: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("product")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_product_create)

        initViews()
        initEvents()
        loadSelectors()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
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
            spinner = fieldIdCategory,
            collectionName = "category",
            labelFields = listOf("name"),
            selectedId = 0
        )

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdShop,
            collectionName = "shop",
            labelFields = listOf("name"),
            selectedId = 0
        )
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

        val name = fieldName.text.toString().trim()
        val detail = fieldDetail.text.toString().trim().ifEmpty { null }
        val stockText = fieldStock.text.toString().trim()
        val priceText = fieldPrice.text.toString().trim()
        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }

        if (stockText.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el stock", Toast.LENGTH_SHORT).show()
            return
        }

        val stock = stockText.toIntOrNull()

        if (stock == null || stock < 0) {
            Toast.makeText(this, "El stock no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (priceText.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el precio", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull()

        if (price == null || price < 0.0) {
            Toast.makeText(this, "El precio no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        val idCategory = FirestoreSelectHelper.getSelectedId(fieldIdCategory)
        val idShop = FirestoreSelectHelper.getSelectedId(fieldIdShop)

        if (idCategory == null) {
            Toast.makeText(this, "Debe seleccionar una categoría válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (idShop == null) {
            Toast.makeText(this, "Debe seleccionar una tienda válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = ProductModel(
            register = register,
            name = name,
            detail = detail,
            stock = stock,
            price = price,
            storefire = storefire,
            idCategory = idCategory,
            idShop = idShop
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

    private fun saveRegister(data: ProductModel) {
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
