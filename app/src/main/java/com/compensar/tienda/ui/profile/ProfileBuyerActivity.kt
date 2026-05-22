package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.home.HomeCategoryActivity
import com.compensar.tienda.ui.home.HomeProductActivity
import com.compensar.tienda.ui.setting.SettingBuyerActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class ProfileBuyerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var imageProfile: ShapeableImageView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var buttonEditProfile: LinearLayout

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private lateinit var cardBuyerShopping: CardView
    private lateinit var cardBuyerAddress: CardView
    private lateinit var textShoppingCount: TextView
    private lateinit var textAddressCount: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_buyer)
        SessionNavigation.bindProfile(this)

        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        initEvents()
        loadSession()
        loadPhoto()
        loadCounters()
    }

    override fun onResume() {
        super.onResume()
        loadSession()
        loadPhoto()
        loadCounters()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        imageProfile = findViewById(R.id.imgProfile)
        textName = findViewById(R.id.txtUserName)
        textEmail = findViewById(R.id.txtUserEmail)
        buttonEditProfile = findViewById(R.id.btnEditProfile)

        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)

        cardBuyerShopping = findViewById(R.id.cardBuyerShopping)
        cardBuyerAddress = findViewById(R.id.cardBuyerAddress)
        textShoppingCount = findViewById(R.id.txtShoppingCount)
        textAddressCount = findViewById(R.id.txtAddressCount)

        findViewById<TextView?>(R.id.btnCart)?.apply {
            visibility = View.GONE
            isEnabled = false
            isClickable = false
        }
    }

    private fun initEvents() {
        actionReturn.setOnClickListener {
            finish()
        }

        buttonEditProfile.setOnClickListener {
            startActivity(Intent(this, SettingBuyerActivity::class.java))
        }

        actionHome.setOnClickListener {
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionCategory.setOnClickListener {
            val intent = Intent(this, HomeCategoryActivity::class.java)
            startActivity(intent)
        }

        actionShopping.setOnClickListener {
            val intent = Intent(this, BuyerShoppingActivity::class.java)
            startActivity(intent)
        }

        actionAddress.setOnClickListener {
            val intent = Intent(this, BuyerAddressActivity::class.java)
            startActivity(intent)
        }

        actionAccount.setOnClickListener {
            // Ya se encuentra en el perfil de comprador.
        }

        cardBuyerShopping.setOnClickListener {
            val intent = Intent(this, BuyerShoppingActivity::class.java)
            startActivity(intent)
        }

        cardBuyerAddress.setOnClickListener {
            val intent = Intent(this, BuyerAddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }

    private fun loadPhoto() {
        val userRegister = SessionManager.getRegister(this)
        if (userRegister <= 0) {
            imageProfile.setImageResource(R.drawable.ic_profile)
            return
        }

        db.collection("user")
            .document(userRegister.toString())
            .get()
            .addOnSuccessListener { document ->
                val image = document.getString("storefire").orEmpty()
                if (image.isNotBlank()) {
                    Glide.with(this)
                        .load(image)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(imageProfile)
                } else {
                    imageProfile.setImageResource(R.drawable.ic_profile)
                }
            }
            .addOnFailureListener {
                imageProfile.setImageResource(R.drawable.ic_profile)
            }
    }

    private fun loadCounters() {
        val userRegister = SessionManager.getRegister(this)
        if (userRegister <= 0) {
            textShoppingCount.text = "0"
            textAddressCount.text = "0"
            return
        }

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
}
