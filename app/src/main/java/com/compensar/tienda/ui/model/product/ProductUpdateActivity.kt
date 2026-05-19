package com.compensar.tienda.ui.model.product

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.bumptech.glide.Glide
import com.compensar.tienda.domain.model.ProductModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper

class ProductUpdateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldName: EditText
    private lateinit var fieldDetail: EditText
    private lateinit var fieldStock: EditText
    private lateinit var fieldPrice: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var fieldIdCategory: Spinner
    private lateinit var fieldIdShop: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("product")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    private var register: Long = 0
    private var currentData: ProductModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_product_update)
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
        actionGallery = findViewById(R.id.actionGallery)
        actionCamera = findViewById(R.id.actionCamera)

        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldStock = findViewById(R.id.fieldStock)
        fieldPrice = findViewById(R.id.fieldPrice)
        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
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
        actionGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        actionCamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

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
                    currentData = document.toObject(ProductModel::class.java)
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
        val currentData = currentData ?: return

        fieldRegister.setText(currentData.register.toString())
        fieldName.setText(currentData.name?.toString() ?: "")
        fieldDetail.setText(currentData.detail?.toString() ?: "")
        fieldStock.setText(currentData.stock.toString())
        fieldPrice.setText(currentData.price.toString())
        fieldStorefire.setText(currentData.storefire?.toString() ?: "")
        displayImagePreview(currentData.storefire?.toString())

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdCategory,
            collectionName = "category",
            labelFields = listOf("name"),
            selectedId = currentData.idCategory
        )

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdShop,
            collectionName = "shop",
            labelFields = listOf("name"),
            selectedId = currentData.idShop
        )
    }

    private fun actionOperate() {
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
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun uploadImageFromGallery(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val register = getRegisterForImageUpload() ?: return

        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        FirebaseStorageImageHelper.uploadFromUri(
            module = "product",
            register = register,
            uri = uri,
            onSuccess = { url ->
                fieldStorefire.setText(url)
                displayImagePreview(url)
                Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        )
    }

    private fun uploadImageFromCamera(bitmap: Bitmap?) {
        if (bitmap == null) {
            Toast.makeText(this, "No se capturó imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val register = getRegisterForImageUpload() ?: return

        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        FirebaseStorageImageHelper.uploadFromBitmap(
            module = "product",
            register = register,
            bitmap = bitmap,
            onSuccess = { url ->
                fieldStorefire.setText(url)
                displayImagePreview(url)
                Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        )
    }


    private fun displayImagePreview(url: String?) {
        if (url.isNullOrBlank()) {
            imagePreview.setImageDrawable(null)
            imagePreview.visibility = View.GONE
            return
        }

        imagePreview.visibility = View.VISIBLE

        Glide.with(this)
            .load(url)
            .centerCrop()
            .into(imagePreview)
    }

    private fun getRegisterForImageUpload(): Long? {
        val registerText = fieldRegister.text.toString().trim()
        val register = registerText.toLongOrNull()

        if (register == null || register <= 0) {
            Toast.makeText(this, "Debes ingresar un ID válido antes de cargar la imagen", Toast.LENGTH_SHORT).show()
            return null
        }

        return register
    }
}
