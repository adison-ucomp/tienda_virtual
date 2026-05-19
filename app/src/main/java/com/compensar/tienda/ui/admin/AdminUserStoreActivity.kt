package com.compensar.tienda.ui.admin

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class AdminUserStoreActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldNames: EditText
    private lateinit var fieldSurnames: EditText
    private lateinit var fieldEmail: EditText
    private lateinit var fieldPassword: EditText
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var fieldIdRole: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("user")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_user_store)
        SessionNavigation.bindProfile(this)

        initViews()
        displayImagePreview(null)
        initEvents()
        loadSelectors()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        actionGallery = findViewById(R.id.actionGallery)
        actionCamera = findViewById(R.id.actionCamera)

        fieldRegister = findViewById(R.id.fieldRegister)
        fieldNames = findViewById(R.id.fieldNames)
        fieldSurnames = findViewById(R.id.fieldSurnames)
        fieldEmail = findViewById(R.id.fieldEmail)
        fieldPassword = findViewById(R.id.fieldPassword)
        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
        fieldIdRole = findViewById(R.id.fieldIdRole)
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
            spinner = fieldIdRole,
            collectionName = "role",
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

        val names = fieldNames.text.toString().trim()
        val srnms = fieldSurnames.text.toString().trim()
        val email = fieldEmail.text.toString().trim()
        val passwordText = fieldPassword.text.toString().trim()

        if (names.isEmpty()) {
            Toast.makeText(this, "Debes ingresar names", Toast.LENGTH_SHORT).show()
            return
        }
        if (srnms.isEmpty()) {
            Toast.makeText(this, "Debes ingresar srnms", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Debes ingresar email", Toast.LENGTH_SHORT).show()
            return
        }
        if (passwordText.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El correo no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }

        val idRole = FirestoreSelectHelper.getSelectedId(fieldIdRole)

        if (idRole == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = UserModel(
            register = register,
            names = names,
            srnms = srnms,
            email = email,
            password = encryptPassword(passwordText),
            storefire = storefire,
            idRole = idRole
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

    private fun saveRegister(data: UserModel) {
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

    private fun encryptPassword(password: String): String {
        val salt = "com.compensar.tienda.user.password"
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun uploadImageFromGallery(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val register = getRegisterForImageUpload() ?: return

        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        FirebaseStorageImageHelper.uploadFromUri(
            module = "user",
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
            module = "user",
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
