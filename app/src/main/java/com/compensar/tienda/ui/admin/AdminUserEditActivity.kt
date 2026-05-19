package com.compensar.tienda.ui.admin

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import com.compensar.tienda.domain.model.SellerModel
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore

class AdminUserEditActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var actionGallery: Button
    private lateinit var actionCamera: Button

    private lateinit var fieldNames: EditText
    private lateinit var fieldSurnames: EditText
    private lateinit var fieldEmail: EditText
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

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uploadImageFromGallery(uri)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        uploadImageFromCamera(bitmap)
    }

    private var register: Long = 0
    private var currentData: UserModel? = null
    private var currentSellerRegister: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_user_edit)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)

        initViews()
        configureSellerFields(false, clearFields = false)
        initEvents()
        loadRegister()
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
        actionExecute.setOnClickListener { actionOperate() }

        fieldIdRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                configureSellerFields(isSellerRoleSelected(), clearFields = !isSellerRoleSelected())
                if (isSellerRoleSelected()) {
                    loadSellerByUser(register)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                configureSellerFields(false)
            }
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
                    currentData = document.toObject(UserModel::class.java)
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

        fieldNames.setText(currentData.names?.toString() ?: "")
        fieldSurnames.setText(currentData.srnms?.toString() ?: "")
        fieldEmail.setText(currentData.email?.toString() ?: "")
        fieldStorefire.setText(currentData.storefire?.toString() ?: "")
        displayImagePreview(currentData.storefire?.toString())

        configureSellerFields(currentData.idRole == SELLER_ROLE_ID, clearFields = false)
        if (currentData.idRole == SELLER_ROLE_ID) {
            loadSellerByUser(currentData.register)
        }

        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdRole,
            collectionName = "role",
            labelFields = listOf("name"),
            selectedId = currentData.idRole
        )
    }

    private fun loadSellerByUser(userRegister: Long) {
        sellerCollection
            .whereEqualTo("idUser", userRegister)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                val seller = document?.toObject(SellerModel::class.java)

                currentSellerRegister = seller?.register ?: document?.id?.toLongOrNull()

                fieldSellerCompany.setText(seller?.company?.toString() ?: "")
                fieldSellerNit.setText(seller?.nit?.toString() ?: "")
                fieldSellerAddress.setText(seller?.address?.toString() ?: "")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun actionOperate() {
        val currentData = currentData ?: return

        val names = fieldNames.text.toString().trim()
        val srnms = fieldSurnames.text.toString().trim()
        val email = fieldEmail.text.toString().trim()

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
            password = currentData.password,
            storefire = storefire,
            idRole = idRole
        )

        collection.document(register.toString())
            .set(data)
            .addOnSuccessListener {
                if (sellerData != null) {
                    saveSellerRegister(sellerData)
                } else {
                    deleteSellerByUser(register)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveSellerRegister(data: SellerModel) {
        val sellerRegister = currentSellerRegister ?: data.register
        val normalized = data.copy(register = sellerRegister)

        sellerCollection.document(sellerRegister.toString())
            .set(normalized)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Usuario actualizado, pero error al guardar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun deleteSellerByUser(userRegister: Long) {
        sellerCollection
            .whereEqualTo("idUser", userRegister)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                var pending = result.size()
                var hasError = false

                result.documents.forEach { document ->
                    document.reference.delete()
                        .addOnSuccessListener {
                            pending--
                            if (pending == 0 && !hasError) {
                                Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        .addOnFailureListener { exception ->
                            hasError = true
                            Toast.makeText(this, "Usuario actualizado, pero error al eliminar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                            exception.printStackTrace()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Usuario actualizado, pero error al validar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
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
            register = currentSellerRegister ?: userRegister,
            company = company,
            nit = nit,
            address = address,
            idUser = userRegister
        )
    }

    private fun configureSellerFields(show: Boolean, clearFields: Boolean = true) {
        sellerFieldsContainer.visibility = if (show) View.VISIBLE else View.GONE
        fieldSellerCompany.isEnabled = show
        fieldSellerNit.isEnabled = show
        fieldSellerAddress.isEnabled = show

        if (!show && clearFields) {
            fieldSellerCompany.setText("")
            fieldSellerNit.setText("")
            fieldSellerAddress.setText("")
        }
    }

    private fun isSellerRoleSelected(): Boolean {
        return FirestoreSelectHelper.getSelectedId(fieldIdRole) == SELLER_ROLE_ID
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
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido para cargar la imagen", Toast.LENGTH_SHORT).show()
            return null
        }

        return register
    }

    companion object {
        private const val SELLER_ROLE_ID = 2L
    }
}
