package com.compensar.tienda.ui.model.image

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
import com.compensar.tienda.ui.model.common.ModuleView
import com.bumptech.glide.Glide
import com.compensar.tienda.model.ImageModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper

class ImageCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var fieldIdProduct: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("image")

    private var generatedRegister: Long? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_image_create)
        SessionNavigation.bindProfile(this)

        initViews()
        displayImagePreview(null)
        initEvents()
        loadSelectors()
        loadNextRegister()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Crear", "image", "Imágenes")
        actionGallery = findViewById(R.id.actionGallery)
        actionCamera = findViewById(R.id.actionCamera)

        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
        fieldIdProduct = findViewById(R.id.fieldIdProduct)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
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

    private fun loadSelectors() {
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdProduct,
            collectionName = "product",
            labelFields = listOf("name"),
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

        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }



        val idProduct = FirestoreSelectHelper.getSelectedId(fieldIdProduct)

        if (idProduct == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = ImageModel(
            register = register,
            storefire = storefire,
            idProduct = idProduct
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

    private fun saveRegister(data: ImageModel) {
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

    private fun uploadImageFromGallery(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val register = getRegisterForImageUpload() ?: return

        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        FirebaseStorageImageHelper.uploadFromUri(
            module = "image",
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
            module = "image",
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
        val register = generatedRegister

        if (register == null || register <= 0) {
            Toast.makeText(this, "Espera a que se genere el ID automático antes de cargar la imagen", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return null
        }

        return register
    }
}
