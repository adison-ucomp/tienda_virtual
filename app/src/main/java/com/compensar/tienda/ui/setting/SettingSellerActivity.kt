package com.compensar.tienda.ui.setting

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.SellerModel
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirebaseStorageImageHelper
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [SettingSellerActivity].
 *
 * Responsable de la logica asociada al pantalla de configuracion.
 */
class SettingSellerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var imageProfile: ImageView
    private lateinit var actionChangePhoto: TextView
    private lateinit var fieldNit: EditText
    private lateinit var fieldCompany: EditText
    private lateinit var fieldAddress: EditText
    private lateinit var fieldName: EditText
    private lateinit var fieldSurnames: EditText
    private lateinit var fieldEmail: EditText
    private lateinit var actionSaveProfile: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("user")
    private val sellerCollection = db.collection("seller")

    private var userRegister: Long = 0L
    private var sellerRegister: Long = 0L
    private var currentUser: UserModel? = null
    private var currentSeller: SellerModel? = null
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
        setContentView(R.layout.setting_seller)
        SessionNavigation.bindProfile(this)

        userRegister = SessionManager.getRegister(this)

        initViews()
        initEvents()
        loadCurrentData()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        imageProfile = findViewById(R.id.imgProfile)
        actionChangePhoto = findViewById(R.id.btnChangePhoto)
        fieldNit = findViewById(R.id.etNit)
        fieldCompany = findViewById(R.id.etCompany)
        fieldAddress = findViewById(R.id.etAddress)
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
                loadSeller()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar usuario: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun loadSeller() {
        sellerCollection
            .whereEqualTo("idUser", userRegister)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                sellerRegister = document?.getLong("register") ?: 0L
                currentSeller = document?.toObject(SellerModel::class.java)
                showSeller(currentSeller)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showUser(user: UserModel?) {
        fieldName.setText(user?.names.orEmpty())
        fieldSurnames.setText(user?.srnms.orEmpty())
        fieldEmail.setText(user?.email.orEmpty())
        showProfileImage(user?.storefire)
    }

    private fun showSeller(seller: SellerModel?) {
        fieldNit.setText(seller?.nit.orEmpty())
        fieldCompany.setText(seller?.company.orEmpty())
        fieldAddress.setText(seller?.address.orEmpty())
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
        val seller = currentSeller

        val nit = fieldNit.text.toString().trim()
        val company = fieldCompany.text.toString().trim()
        val address = fieldAddress.text.toString().trim()
        val names = fieldName.text.toString().trim()
        val surnames = fieldSurnames.text.toString().trim()
        val email = fieldEmail.text.toString().trim()

        if (names.isBlank() || surnames.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Debes completar los datos del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        if (nit.isBlank() || company.isBlank() || address.isBlank()) {
            Toast.makeText(this, "Debes completar los datos del vendedor", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            selectedImageUri != null -> uploadSelectedUriAndUpdate(user, seller, names, surnames, email, nit, company, address)
            selectedImageBitmap != null -> uploadSelectedBitmapAndUpdate(user, seller, names, surnames, email, nit, company, address)
            else -> updateData(user, seller, names, surnames, email, nit, company, address, user.storefire)
        }
    }

    private fun uploadSelectedUriAndUpdate(
        user: UserModel,
        seller: SellerModel?,
        names: String,
        surnames: String,
        email: String,
        nit: String,
        company: String,
        address: String
    ) {
        val uri = selectedImageUri

        if (uri == null) {
            updateData(user, seller, names, surnames, email, nit, company, address, user.storefire)
            return
        }

        FirebaseStorageImageHelper.uploadFromUri(
            module = "user",
            register = userRegister,
            uri = uri,
            onSuccess = { url ->
                updateData(user, seller, names, surnames, email, nit, company, address, url)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir foto: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        )
    }

    private fun uploadSelectedBitmapAndUpdate(
        user: UserModel,
        seller: SellerModel?,
        names: String,
        surnames: String,
        email: String,
        nit: String,
        company: String,
        address: String
    ) {
        val bitmap = selectedImageBitmap

        if (bitmap == null) {
            updateData(user, seller, names, surnames, email, nit, company, address, user.storefire)
            return
        }

        FirebaseStorageImageHelper.uploadFromBitmap(
            module = "user",
            register = userRegister,
            bitmap = bitmap,
            onSuccess = { url ->
                updateData(user, seller, names, surnames, email, nit, company, address, url)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir foto: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        )
    }

    private fun updateData(
        user: UserModel,
        seller: SellerModel?,
        names: String,
        surnames: String,
        email: String,
        nit: String,
        company: String,
        address: String,
        imageUrl: String?
    ) {
        val updatedUser = user.copy(
            names = names,
            srnms = surnames,
            email = email,
            storefire = imageUrl
        )

        val updatedSeller = seller?.copy(
            company = company,
            nit = nit,
            address = address
        ) ?: SellerModel(
            register = sellerRegister,
            company = company,
            nit = nit,
            address = address,
            idUser = userRegister
        )

        userCollection.document(userRegister.toString())
            .set(updatedUser)
            .addOnSuccessListener {
                val finalSellerRegister = updatedSeller.register

                if (finalSellerRegister <= 0L) {
                    Toast.makeText(this, "No se encontró el registro del vendedor", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                sellerCollection.document(finalSellerRegister.toString())
                    .set(updatedSeller)
                    .addOnSuccessListener {
                        SessionManager.updateProfile(this, names, surnames, email)
                        Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error al actualizar vendedor: ${exception.message}", Toast.LENGTH_LONG).show()
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar usuario: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}

