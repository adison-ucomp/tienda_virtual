package com.compensar.tienda.ui.admin

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.model.ShopModel
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Clase [AdminShopStoreActivity].
 *
 * Responsable de la logica asociada al pantalla del panel administrativo.
 */
class AdminShopStoreActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldName: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var fieldIdSeller: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shop")

    private var generatedRegister: Long? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_shop_store)
        SessionNavigation.bindProfile(this)

        initViews()
        displayImagePreview(null)
        initEvents()
        loadSelectors()
        loadNextRegister()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        actionGallery = findViewById(R.id.actionGallery)
        actionCamera = findViewById(R.id.actionCamera)

        fieldName = findViewById(R.id.fieldName)
        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
        fieldIdSeller = findViewById(R.id.fieldIdSeller)
    }

    private fun initEvents() {

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
            spinner = fieldIdSeller,
            collectionName = "seller",
            labelFields = listOf("company", "nit"),
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

        val name = fieldName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar name", Toast.LENGTH_SHORT).show()
            return
        }

        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }

        val idSeller = FirestoreSelectHelper.getSelectedId(fieldIdSeller)

        if (idSeller == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = ShopModel(
            register = register,
            name = name,
            storefire = storefire,
            idSeller = idSeller
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

    private fun saveRegister(data: ShopModel) {
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
            module = "shop",
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
            module = "shop",
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

