package com.compensar.tienda.ui.setting

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [SettingBuyerActivity].
 *
 * Responsable de la logica asociada al pantalla de configuracion.
 */
class SettingBuyerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var imageProfile: ShapeableImageView
    private lateinit var actionChangePhoto: TextView
    private lateinit var fieldName: EditText
    private lateinit var fieldSurnames: EditText
    private lateinit var fieldEmail: EditText
    private lateinit var actionSaveProfile: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")

    private var userRegister: Long = 0L
    private var currentUser: UserModel? = null
    private var selectedImageUri: Uri? = null
    private var selectedImageBitmap: Bitmap? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        selectedImageUri = uri
        selectedImageBitmap = null
        imageProfile.setImageURI(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap == null) {
            Toast.makeText(this, "No se capturó ninguna imagen", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        selectedImageBitmap = bitmap
        selectedImageUri = null
        imageProfile.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_buyer)
        SessionNavigation.bindProfile(this)
        SessionNavigation.applyBuyerInferiorVisibility(this)

        userRegister = SessionManager.getRegister(this)

        initViews()
        initEvents()
        loadCurrentData()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        imageProfile = findViewById(R.id.imgProfile)
        actionChangePhoto = findViewById(R.id.btnChangePhoto)
        fieldName = findViewById(R.id.etName)
        fieldSurnames = findViewById(R.id.etSurnames)
        fieldEmail = findViewById(R.id.etEmail)
        actionSaveProfile = findViewById(R.id.btnSaveProfile)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionChangePhoto.setOnClickListener { showImageSourceDialog() }
        actionSaveProfile.setOnClickListener { saveChanges() }
    }

    private fun loadCurrentData() {
        if (userRegister <= 0L) {
            Toast.makeText(this, "No se encontró la sesión del usuario", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        userCollection.document(userRegister.toString())
            .get()
            .addOnSuccessListener { userDocument ->
                currentUser = userDocument.toObject(UserModel::class.java)
                showUser(currentUser)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar usuario: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showUser(user: UserModel?) {
        fieldName.setText(user?.names.orEmpty())
        fieldSurnames.setText(user?.srnms.orEmpty())
        fieldEmail.setText(user?.email.orEmpty())
        showProfileImage(user?.storefire)
    }

    private fun showProfileImage(imageUrl: String?) {
        if (imageUrl.isNullOrBlank()) {
            imageProfile.setImageResource(R.drawable.ic_profile)
            imageProfile.setPadding(8, 8, 8, 8)
            return
        }

        imageProfile.setPadding(0, 0, 0, 0)

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .centerCrop()
            .into(imageProfile)
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cambiar foto")
            .setItems(arrayOf("Buscar en el dispositivo", "Tomar foto")) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> cameraLauncher.launch(null)
                }
            }
            .show()
    }

    private fun saveChanges() {
        val user = currentUser ?: return
        val names = fieldName.text.toString().trim()
        val surnames = fieldSurnames.text.toString().trim()
        val email = fieldEmail.text.toString().trim()

        if (names.isBlank() || surnames.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            selectedImageUri != null -> uploadSelectedUriAndUpdate(user, names, surnames, email)
            selectedImageBitmap != null -> uploadSelectedBitmapAndUpdate(user, names, surnames, email)
            else -> updateUser(user, names, surnames, email, user.storefire)
        }
    }

    private fun uploadSelectedUriAndUpdate(
        user: UserModel,
        names: String,
        surnames: String,
        email: String
    ) {
        val uri = selectedImageUri

        if (uri == null) {
            updateUser(user, names, surnames, email, user.storefire)
            return
        }

        FirebaseStorageImageHelper.uploadFromUri(
            module = "user",
            register = userRegister,
            uri = uri,
            onSuccess = { url ->
                updateUser(user, names, surnames, email, url)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir foto: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun uploadSelectedBitmapAndUpdate(
        user: UserModel,
        names: String,
        surnames: String,
        email: String
    ) {
        val bitmap = selectedImageBitmap

        if (bitmap == null) {
            updateUser(user, names, surnames, email, user.storefire)
            return
        }

        FirebaseStorageImageHelper.uploadFromBitmap(
            module = "user",
            register = userRegister,
            bitmap = bitmap,
            onSuccess = { url ->
                updateUser(user, names, surnames, email, url)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir foto: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun updateUser(
        user: UserModel,
        names: String,
        surnames: String,
        email: String,
        imageUrl: String?
    ) {
        val updatedUser = user.copy(
            names = names,
            srnms = surnames,
            email = email,
            storefire = imageUrl
        )

        userCollection.document(userRegister.toString())
            .set(updatedUser)
            .addOnSuccessListener {
                SessionManager.updateProfile(this, names, surnames, email)
                Toast.makeText(this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}

