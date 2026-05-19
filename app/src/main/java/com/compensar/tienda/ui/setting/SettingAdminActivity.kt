package com.compensar.tienda.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.compensar.tienda.ui.profile.ProfileAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class SettingAdminActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var actionHome: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private lateinit var imageProfile: ImageView
    private lateinit var actionChangePhoto: TextView
    private lateinit var fieldName: EditText
    private lateinit var fieldSurname: EditText
    private lateinit var fieldEmail: EditText
    private lateinit var actionSaveProfile: LinearLayout
    private lateinit var progressSaveProfile: ProgressBar

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("user")

    private var register: Long = 0
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
        imageProfile.setPadding(0, 0, 0, 0)
        imageProfile.setImageURI(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap == null) {
            Toast.makeText(this, "No se capturó ninguna imagen", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        selectedImageBitmap = bitmap
        selectedImageUri = null
        imageProfile.setPadding(0, 0, 0, 0)
        imageProfile.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_admin)
        SessionNavigation.bindProfile(this)

        register = SessionManager.getRegister(this)

        initViews()
        initEvents()
        loadCurrentUser()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionHome = findViewById(R.id.actionHome)
        actionAccount = findViewById(R.id.actionAccount)

        imageProfile = findViewById(R.id.imgProfile)
        actionChangePhoto = findViewById(R.id.btnChangePhoto)
        fieldName = findViewById(R.id.etName)
        fieldSurname = findViewById(R.id.etSurname)
        fieldEmail = findViewById(R.id.etEmail)
        actionSaveProfile = findViewById(R.id.btnSaveProfile)
        progressSaveProfile = findViewById(R.id.progressSaveProfile)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener {
            finish()
        }

        actionHome.setOnClickListener {
            val intent = Intent(this, DashboardAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionAccount.setOnClickListener {
            val intent = Intent(this, ProfileAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionChangePhoto.setOnClickListener {
            showImageSourceDialog()
        }

        actionSaveProfile.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadCurrentUser() {
        if (register <= 0) {
            Toast.makeText(this, "No se encontró la sesión del usuario", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setLoading(true)

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)

                if (user == null) {
                    setLoading(false)
                    Toast.makeText(this, "No se encontró la información del usuario", Toast.LENGTH_LONG).show()
                    finish()
                    return@addOnSuccessListener
                }

                currentUser = user
                showCurrentUser(user)
                setLoading(false)
            }
            .addOnFailureListener { exception ->
                setLoading(false)
                Toast.makeText(this, "Error al cargar perfil: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showCurrentUser(user: UserModel) {
        fieldName.setText(user.names.orEmpty())
        fieldSurname.setText(user.srnms.orEmpty())
        fieldEmail.setText(user.email.orEmpty())
        showProfileImage(user.storefire)
    }

    private fun showProfileImage(imageUrl: String?) {
        if (imageUrl.isNullOrBlank()) {
            imageProfile.setImageResource(R.drawable.ic_profile)
            imageProfile.setPadding(12, 12, 12, 12)
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
        val surnames = fieldSurname.text.toString().trim()
        val email = fieldEmail.text.toString().trim()

        if (names.isBlank()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }

        if (surnames.isBlank()) {
            Toast.makeText(this, "Debes ingresar el apellido", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isBlank()) {
            Toast.makeText(this, "Debes ingresar el correo", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        when {
            selectedImageUri != null -> uploadSelectedUriAndUpdate(user, names, surnames, email)
            selectedImageBitmap != null -> uploadSelectedBitmapAndUpdate(user, names, surnames, email)
            else -> updateUserProfile(user, names, surnames, email, user.storefire)
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
            updateUserProfile(user, names, surnames, email, user.storefire)
            return
        }

        FirebaseStorageImageHelper.uploadFromUri(
            module = "user",
            register = register,
            uri = uri,
            onSuccess = { url ->
                updateUserProfile(user, names, surnames, email, url)
            },
            onFailure = { exception ->
                setLoading(false)
                Toast.makeText(this, "Error al subir foto: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
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
            updateUserProfile(user, names, surnames, email, user.storefire)
            return
        }

        FirebaseStorageImageHelper.uploadFromBitmap(
            module = "user",
            register = register,
            bitmap = bitmap,
            onSuccess = { url ->
                updateUserProfile(user, names, surnames, email, url)
            },
            onFailure = { exception ->
                setLoading(false)
                Toast.makeText(this, "Error al subir foto: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        )
    }

    private fun updateUserProfile(
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

        collection.document(register.toString())
            .set(updatedUser)
            .addOnSuccessListener {
                currentUser = updatedUser
                selectedImageUri = null
                selectedImageBitmap = null
                SessionManager.updateProfile(this, names, surnames, email)
                setLoading(false)
                Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                setLoading(false)
                Toast.makeText(this, "Error al guardar cambios: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressSaveProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        actionSaveProfile.isEnabled = !isLoading
        actionChangePhoto.isEnabled = !isLoading
        fieldName.isEnabled = !isLoading
        fieldSurname.isEnabled = !isLoading
        fieldEmail.isEnabled = !isLoading
        actionSaveProfile.alpha = if (isLoading) 0.6f else 1f
        actionChangePhoto.alpha = if (isLoading) 0.6f else 1f
    }
}
