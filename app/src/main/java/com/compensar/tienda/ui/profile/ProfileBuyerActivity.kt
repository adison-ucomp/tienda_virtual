package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.BiometricSessionManager
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.home.HomeProductActivity
import com.compensar.tienda.ui.setting.SettingBuyerActivity
import com.compensar.tienda.ui.setting.SettingPasswordActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class ProfileBuyerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var imageProfile: ShapeableImageView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var rowInformation: LinearLayout
    private lateinit var rowAddress: LinearLayout
    private lateinit var rowShopping: LinearLayout
    private lateinit var rowPassword: LinearLayout
    private lateinit var rowNotifications: LinearLayout
    private lateinit var rowLanguage: LinearLayout
    private lateinit var rowDarkMode: LinearLayout
    private lateinit var rowSupport: LinearLayout
    private lateinit var rowTerms: LinearLayout
    private lateinit var actionLogout: TextView
    private lateinit var switchBiometric: Switch
    private lateinit var textShoppingCount: TextView
    private lateinit var textAddressCount: TextView

    private val db = FirebaseFirestore.getInstance()
    private var loadingBiometricState = false
    private var currentUser: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_buyer)
        SessionNavigation.bindProfile(this)

        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        initEvents()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        imageProfile = findViewById(R.id.imgProfile)
        textName = findViewById(R.id.txtUserName)
        textEmail = findViewById(R.id.txtUserEmail)
        rowInformation = findViewById(R.id.rowInformation)
        rowAddress = findViewById(R.id.rowAddress)
        rowShopping = findViewById(R.id.rowShopping)
        rowPassword = findViewById(R.id.rowPassword)
        rowNotifications = findViewById(R.id.rowNotifications)
        rowLanguage = findViewById(R.id.rowLanguage)
        rowDarkMode = findViewById(R.id.rowDarkMode)
        rowSupport = findViewById(R.id.rowSupport)
        rowTerms = findViewById(R.id.rowTerms)
        actionLogout = findViewById(R.id.actionLogout)
        switchBiometric = findViewById(R.id.switchBiometric)
        textShoppingCount = findViewById(R.id.txtShoppingCount)
        textAddressCount = findViewById(R.id.txtAddressCount)

        findViewById<TextView?>(R.id.btnCart)?.apply {
            visibility = View.GONE
            isEnabled = false
            isClickable = false
        }
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }

        rowInformation.setOnClickListener {
            startActivity(Intent(this, SettingBuyerActivity::class.java))
        }

        rowAddress.setOnClickListener {
            startActivity(Intent(this, BuyerAddressActivity::class.java))
        }

        rowShopping.setOnClickListener {
            startActivity(Intent(this, BuyerShoppingActivity::class.java))
        }

        rowPassword.setOnClickListener {
            startActivity(Intent(this, SettingPasswordActivity::class.java))
        }

        switchBiometric.setOnCheckedChangeListener { _, checked ->
            if (loadingBiometricState) {
                return@setOnCheckedChangeListener
            }

            if (checked) {
                requestBiometricToEnable()
            } else {
                updateBiometric(false)
            }
        }

        val waitingMessage = View.OnClickListener {
            Toast.makeText(this, "Funcionalidad pendiente por implementar", Toast.LENGTH_SHORT).show()
        }

        rowNotifications.setOnClickListener(waitingMessage)
        rowLanguage.setOnClickListener(waitingMessage)
        rowDarkMode.setOnClickListener(waitingMessage)
        rowSupport.setOnClickListener(waitingMessage)
        rowTerms.setOnClickListener(waitingMessage)

        actionLogout.setOnClickListener {
            SessionManager.clear(this)
            val intent = Intent(this, HomeProductActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadData() {
        val userRegister = SessionManager.getRegister(this)

        if (userRegister <= 0L) {
            textName.text = "Sin Informacion"
            textEmail.text = "Sin Informacion"
            imageProfile.setImageResource(R.drawable.ic_profile)
            return
        }

        db.collection("user")
            .document(userRegister.toString())
            .get()
            .addOnSuccessListener { document ->
                currentUser = document.toObject(UserModel::class.java)
                showUser(currentUser)
            }
            .addOnFailureListener {
                showUser(null)
            }

        loadCounters(userRegister)
    }

    private fun showUser(user: UserModel?) {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)

        val image = user?.storefire.orEmpty()
        if (image.isNotBlank()) {
            Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .centerCrop()
                .into(imageProfile)
        } else {
            imageProfile.setImageResource(R.drawable.ic_profile)
        }

        loadingBiometricState = true
        switchBiometric.isChecked = user?.biometric == true && BiometricSessionManager.isEnabled(this)
        loadingBiometricState = false
    }

    private fun loadCounters(userRegister: Long) {
        db.collection("order")
            .whereEqualTo("idUser", userRegister)
            .get()
            .addOnSuccessListener { result ->
                textShoppingCount.text = result.size().toString()
            }
            .addOnFailureListener {
                textShoppingCount.text = "0"
            }

        db.collection("address")
            .whereEqualTo("idUser", userRegister)
            .get()
            .addOnSuccessListener { result ->
                textAddressCount.text = result.size().toString()
            }
            .addOnFailureListener {
                textAddressCount.text = "0"
            }
    }

    private fun requestBiometricToEnable() {
        val user = currentUser

        if (user == null || user.register <= 0L) {
            Toast.makeText(this, "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show()
            setSwitchWithoutEvent(false)
            return
        }

        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Debes configurar huella en el dispositivo", Toast.LENGTH_LONG).show()
            setSwitchWithoutEvent(false)
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    updateBiometric(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@ProfileBuyerActivity, errString, Toast.LENGTH_SHORT).show()
                    setSwitchWithoutEvent(false)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@ProfileBuyerActivity, "No se pudo validar la huella", Toast.LENGTH_SHORT).show()
                    setSwitchWithoutEvent(false)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Activar autenticación biométrica")
            .setSubtitle("Confirma tu huella para activar el acceso biométrico")
            .setNegativeButtonText("Cancelar")
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun updateBiometric(enabled: Boolean) {
        val user = currentUser

        if (user == null || user.register <= 0L) {
            setSwitchWithoutEvent(false)
            return
        }

        db.collection("user")
            .document(user.register.toString())
            .update("biometric", enabled)
            .addOnSuccessListener {
                if (enabled) {
                    BiometricSessionManager.save(this, user.copy(biometric = true))
                    Toast.makeText(this, "Autenticación biométrica activada", Toast.LENGTH_SHORT).show()
                } else {
                    BiometricSessionManager.clear(this)
                    Toast.makeText(this, "Autenticación biométrica desactivada", Toast.LENGTH_SHORT).show()
                }

                currentUser = user.copy(biometric = enabled)
                setSwitchWithoutEvent(enabled)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                setSwitchWithoutEvent(!enabled)
            }
    }

    private fun setSwitchWithoutEvent(value: Boolean) {
        loadingBiometricState = true
        switchBiometric.isChecked = value
        loadingBiometricState = false
    }
}
