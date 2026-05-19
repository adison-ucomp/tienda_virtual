package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.UserModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.setting.SettingAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var actionHome: LinearLayout
    private lateinit var actionAccount: LinearLayout
    private lateinit var actionEditProfile: LinearLayout

    private lateinit var imageProfile: ImageView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNumberCategories: TextView
    private lateinit var textNumberShops: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_admin)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSession()
        loadUserProfileImage()
        loadCounters()
    }

    override fun onResume() {
        super.onResume()
        loadSession()
        loadUserProfileImage()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionHome = findViewById(R.id.actionHome)
        actionAccount = findViewById(R.id.actionAccount)
        actionEditProfile = findViewById(R.id.btnEditProfile)

        imageProfile = findViewById(R.id.imgProfile)
        textName = findViewById(R.id.txtNameAdmin)
        textEmail = findViewById(R.id.txtEmail)
        textNumberCategories = findViewById(R.id.txtNumberCategories)
        textNumberShops = findViewById(R.id.txtShops)
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
            // Ya se encuentra en el perfil de administrador.
        }

        actionEditProfile.setOnClickListener {
            val intent = Intent(this, SettingAdminActivity::class.java)
            startActivity(intent)
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

    private fun loadCounters() {
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                textNumberCategories.text = result.size().toString()
            }
            .addOnFailureListener {
                textNumberCategories.text = "0"
            }

        db.collection("shop")
            .get()
            .addOnSuccessListener { result ->
                textNumberShops.text = result.size().toString()
            }
            .addOnFailureListener {
                textNumberShops.text = "0"
            }
    }
}
