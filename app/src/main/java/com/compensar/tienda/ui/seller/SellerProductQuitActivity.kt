package com.compensar.tienda.ui.seller

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ImagePreviewHelper
import com.compensar.tienda.ui.model.common.FirestoreRelationLabelHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.compensar.tienda.model.ProductModel

/**
 * Clase [SellerProductQuitActivity].
 *
 * Responsable de la logica asociada al pantalla del flujo de vendedor.
 */
class SellerProductQuitActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: TextView
    private lateinit var fieldName: TextView
    private lateinit var fieldDetail: TextView
    private lateinit var fieldStock: TextView
    private lateinit var fieldPrice: TextView
    private lateinit var fieldIdCategory: TextView
    private lateinit var fieldIdShop: TextView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("product")

    private var register: Long = 0
    private var data: ProductModel? = null

    /**
     * Se ejecuta al crear la pantalla.
     * Inicializa vista, estado y eventos principales.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seller_product_quit)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadRegister()
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initViews() {
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        titleHeader.text = "Eliminar Productos"
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldStock = findViewById(R.id.fieldStock)
        fieldPrice = findViewById(R.id.fieldPrice)
        fieldIdCategory = findViewById(R.id.fieldIdCategory)
        fieldIdShop = findViewById(R.id.fieldIdShop)
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
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

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun showRegister() {
        val current = data ?: return

        ImagePreviewHelper.addPreviewToCard(this, findViewById(R.id.cardProductDelete), current.storefire)
        fieldRegister.text = current.register.toString()
        fieldName.text = current.name.toString()
        fieldDetail.text = current.detail.toString()
        fieldStock.text = current.stock.toString()
        fieldPrice.text = current.price.toString()
        FirestoreRelationLabelHelper.load(fieldIdCategory, "category", current.idCategory, listOf("name"))
        FirestoreRelationLabelHelper.load(fieldIdShop, "shop", current.idShop, listOf("name"))
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("purchase")
            .whereEqualTo("idProduct", register)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    Toast.makeText(this, "No se puede eliminar porque el producto está relacionado con otros registros", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
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
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al validar relaciones: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}

