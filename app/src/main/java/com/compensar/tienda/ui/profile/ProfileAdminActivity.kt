package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var actionHome: LinearLayout
    private lateinit var actionAccount: LinearLayout

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
        loadCounters()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionHome = findViewById(R.id.actionHome)
        actionAccount = findViewById(R.id.actionAccount)

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
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
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
