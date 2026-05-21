package com.compensar.tienda.ui.model.category

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.bumptech.glide.Glide
import com.compensar.tienda.model.CategoryModel
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper

class CategoryUpdateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldName: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("category")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    private var register: Long = 0
    private var category: CategoryModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_category_update)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadRegister()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        ModuleView.bindTitle(titleHeader, "Actualizar", "category", "Categorias")
        actionGallery = findViewById(R.id.actionGallery)
        actionCamera = findViewById(R.id.actionCamera)

        fieldName = findViewById(R.id.fieldName)
        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener {
            finish()
        }

        actionCancel.setOnClickListener {
            finish()
        }

        actionGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        actionCamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

        actionExecute.setOnClickListener {
            actionOperate()
        }
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
                    category = document.toObject(CategoryModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontró la categoría", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showRegister() {
        val currentCategory = category ?: return
        fieldName.setText(currentCategory.name ?: "")
        fieldStorefire.setText(currentCategory.storefire ?: "")
    }

    private fun actionOperate() {
        val name = fieldName.text.toString().trim()
        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedCategory = CategoryModel(
            register = register,
            name = name,
            storefire = storefire
        )

        collection.document(register.toString())
            .set(updatedCategory)
            .addOnSuccessListener {
                Toast.makeText(this, "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show()
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
            module = "category",
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
            module = "category",
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
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido para cargar la imagen", Toast.LENGTH_SHORT).show()
            return null
        }

        return register
    }
}
