package com.compensar.tienda.ui.seller

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.model.ShopModel
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [SellerShopEditActivity].
 *
 * Responsable de la logica asociada al pantalla del flujo de vendedor.
 */
class SellerShopEditActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldName: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shop")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    private var register: Long = 0
    private var currentData: ShopModel? = null
    private var currentSellerRegister: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seller_shop_edit)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadCurrentSeller()
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

    private fun loadCurrentSeller() {
        val userRegister = SessionManager.getRegister(this)

        db.collection("seller")
            .get()
            .addOnSuccessListener { result ->
                currentSellerRegister = result.documents.firstOrNull { document ->
                    val register = document.getLong("register") ?: 0L
                    val idUser = document.getLong("idUser") ?: 0L
                    idUser == userRegister || register == userRegister
                }?.getLong("register") ?: userRegister

                if (currentSellerRegister <= 0L) {
                    Toast.makeText(this, "No se encontró el vendedor de la sesión", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                loadRegister()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
                finish()
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
                    currentData = document.toObject(ShopModel::class.java)
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

        if (currentData.idSeller != currentSellerRegister) {
            Toast.makeText(this, "No puedes editar una tienda de otro vendedor", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fieldName.setText(currentData.name?.toString() ?: "")
        fieldStorefire.setText(currentData.storefire?.toString() ?: "")
        displayImagePreview(currentData.storefire?.toString())
    }

    private fun actionOperate() {
        val currentData = currentData ?: return

        val name = fieldName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar name", Toast.LENGTH_SHORT).show()
            return
        }

        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }

        if (currentData.idSeller != currentSellerRegister) {
            Toast.makeText(this, "No puedes modificar una tienda de otro vendedor", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val data = ShopModel(
            register = register,
            name = name,
            storefire = storefire,
            idSeller = currentSellerRegister
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
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido para cargar la imagen", Toast.LENGTH_SHORT).show()
            return null
        }

        return register
    }
}

