package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.home.HomeProductActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [ProfileShareActivity].
 *
 * Responsable de la logica asociada al pantalla de perfil de usuario.
 */
class ProfileShareActivity : AppCompatActivity() {

    private lateinit var actionOverlayClose: View
    private lateinit var profileSharePanel: LinearLayout

    private lateinit var imageProfile: ImageView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView

    private lateinit var btnSettings: LinearLayout
    private lateinit var btnSupport: LinearLayout
    private lateinit var btnLogout: LinearLayout

    private val db = FirebaseFirestore.getInstance()

    private var startX: Float = 0f
    private var endX: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_share)

        initViews()
        initEvents()
        loadSession()
        loadUserProfileImage()
    }

    override fun onResume() {
        super.onResume()
        loadSession()
        loadUserProfileImage()
    }

    private fun initViews() {
        actionOverlayClose = findViewById(R.id.actionOverlayClose)
        profileSharePanel = findViewById(R.id.profileSharePanel)

        imageProfile = findViewById(R.id.imgPerfilDrawer)
        textName = findViewById(R.id.textName)
        textEmail = findViewById(R.id.textEmail)

        btnSettings = findViewById(R.id.btnSettings)
        btnSupport = findViewById(R.id.btnSupport)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun initEvents() {
        actionOverlayClose.setOnClickListener {
            finish()
        }

        profileSharePanel.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                }

                MotionEvent.ACTION_UP -> {
                    endX = event.rawX

                    val distance = endX - startX

                    if (distance < -120) {
                        finish()
                    }
                }
            }

            false
        }

        btnSettings.setOnClickListener {
            Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
        }

        btnSupport.setOnClickListener {
            Toast.makeText(this, "Soporte", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            closeSession()
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }

    private fun loadUserProfileImage() {
        val register = SessionManager.getRegister(this)

        if (register <= 0) {
            showDefaultImage()
            return
        }

        db.collection("user")
            .document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)
                val imageUrl = user?.storefire

                if (imageUrl.isNullOrBlank()) {
                    showDefaultImage()
                } else {
                    imageProfile.visibility = View.VISIBLE
                    imageProfile.setPadding(0, 0, 0, 0)

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .centerCrop()
                        .into(imageProfile)
                }
            }
            .addOnFailureListener {
                showDefaultImage()
            }
    }

    private fun showDefaultImage() {
        imageProfile.visibility = View.VISIBLE
        imageProfile.setImageResource(R.drawable.ic_profile)
        imageProfile.setPadding(12, 12, 12, 12)
    }

    private fun closeSession() {
        SessionManager.clear(this)

        val intent = Intent(this, HomeProductActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}

