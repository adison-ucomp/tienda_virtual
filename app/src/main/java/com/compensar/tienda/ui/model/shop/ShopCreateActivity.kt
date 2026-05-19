package com.compensar.tienda.ui.model.shop

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
import com.bumptech.glide.Glide
import com.compensar.tienda.domain.model.ShopModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper

class ShopCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldName: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var fieldIdSeller: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shop")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_shop_create)

        initViews()
        displayImagePreview(null)
        initEvents()
        loadSelectors()
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
        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
        fieldIdSeller = findViewById(R.id.fieldIdSeller)
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
        val registerText = fieldRegister.text.toString().trim()
        val register = registerText.toLongOrNull()

        if (register == null || register <= 0) {
            Toast.makeText(this, "Debes ingresar un ID válido antes de cargar la imagen", Toast.LENGTH_SHORT).show()
            return null
        }

        return register
    }
}
