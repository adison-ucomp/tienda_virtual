package com.compensar.tienda.ui.admin

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.SellerModel
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.security.MessageDigest

class AdminUserStoreActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldNames: EditText
    private lateinit var fieldSurnames: EditText
    private lateinit var fieldEmail: EditText
    private lateinit var fieldPassword: EditText
    private lateinit var btnShowPassword: TextView
    private lateinit var fieldStorefire: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var fieldIdRole: Spinner

    private lateinit var sellerFieldsContainer: LinearLayout
    private lateinit var fieldSellerCompany: EditText
    private lateinit var fieldSellerNit: EditText
    private lateinit var fieldSellerAddress: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("user")
    private val sellerCollection = db.collection("seller")

    private var generatedRegister: Long? = null
    private var isPasswordVisible = false

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
        configureSellerFields(false)
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

        fieldNames = findViewById(R.id.fieldNames)
        fieldSurnames = findViewById(R.id.fieldSurnames)
        fieldEmail = findViewById(R.id.fieldEmail)
        fieldPassword = findViewById(R.id.fieldPassword)
        btnShowPassword = findViewById(R.id.btnShowPassword)
        fieldStorefire = findViewById(R.id.fieldStorefire)
        imagePreview = findViewById(R.id.imagePreview)
        fieldIdRole = findViewById(R.id.fieldIdRole)

        sellerFieldsContainer = findViewById(R.id.sellerFieldsContainer)
        fieldSellerCompany = findViewById(R.id.fieldSellerCompany)
        fieldSellerNit = findViewById(R.id.fieldSellerNit)
        fieldSellerAddress = findViewById(R.id.fieldSellerAddress)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        actionCamera.setOnClickListener { cameraLauncher.launch(null) }
        btnShowPassword.setOnClickListener {
            togglePasswordVisibility()
        }
        actionExecute.setOnClickListener { actionOperate() }

        fieldIdRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                configureSellerFields(isSellerRoleSelected())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                configureSellerFields(false)
            }
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        fieldPassword.transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }

        fieldPassword.setSelection(fieldPassword.text.length)
        btnShowPassword.text = if (isPasswordVisible) "◌" else "◉"
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
        val register = generatedRegister

        if (register == null || register <= 0) {
            Toast.makeText(this, "No fue posible generar el ID automático", Toast.LENGTH_SHORT).show()
            loadNextRegister()
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

        val idRole = FirestoreSelectHelper.getSelectedId(fieldIdRole)

        if (idRole == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }

        val sellerData = if (idRole == SELLER_ROLE_ID) {
            buildSellerData(register) ?: return
        } else {
            null
        }

        val storefire = fieldStorefire.text.toString().trim().ifEmpty { null }

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
                    Toast.makeText(this, "El ID automático ya existe. Intentando generar otro ID.", Toast.LENGTH_SHORT).show()
                    loadNextRegister()
                } else {
                    saveRegister(data, sellerData)
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

    private fun saveRegister(data: UserModel, sellerData: SellerModel?) {
        collection.document(data.register.toString())
            .set(data)
            .addOnSuccessListener {
                if (sellerData != null) {
                    saveSellerRegister(sellerData)
                } else {
                    Toast.makeText(this, "Registro creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveSellerRegister(data: SellerModel) {
        sellerCollection.document(data.register.toString())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario vendedor creado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Usuario creado, pero error al guardar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun buildSellerData(userRegister: Long): SellerModel? {
        val company = fieldSellerCompany.text.toString().trim()
        val nit = fieldSellerNit.text.toString().trim()
        val address = fieldSellerAddress.text.toString().trim()

        if (company.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la empresa del vendedor", Toast.LENGTH_SHORT).show()
            return null
        }
        if (nit.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el NIT del vendedor", Toast.LENGTH_SHORT).show()
            return null
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la dirección del vendedor", Toast.LENGTH_SHORT).show()
            return null
        }

        return SellerModel(
            register = userRegister,
            company = company,
            nit = nit,
            address = address,
            idUser = userRegister
        )
    }

    private fun configureSellerFields(show: Boolean) {
        sellerFieldsContainer.visibility = if (show) View.VISIBLE else View.GONE
        fieldSellerCompany.isEnabled = show
        fieldSellerNit.isEnabled = show
        fieldSellerAddress.isEnabled = show

        if (!show) {
            fieldSellerCompany.setText("")
            fieldSellerNit.setText("")
            fieldSellerAddress.setText("")
        }
    }

    private fun isSellerRoleSelected(): Boolean {
        return FirestoreSelectHelper.getSelectedId(fieldIdRole) == SELLER_ROLE_ID
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
        val register = generatedRegister

        if (register == null || register <= 0) {
            Toast.makeText(this, "Espera a que se genere el ID automático antes de cargar la imagen", Toast.LENGTH_SHORT).show()
            loadNextRegister()
            return null
        }

        return register
    }

    companion object {
        private const val SELLER_ROLE_ID = 2L
    }
}
