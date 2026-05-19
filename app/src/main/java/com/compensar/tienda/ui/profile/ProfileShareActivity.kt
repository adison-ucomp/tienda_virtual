package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.home.HomeProductActivity

class ProfileShareActivity : AppCompatActivity() {

    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var btnSettings: LinearLayout
    private lateinit var btnSupport: LinearLayout
    private lateinit var btnLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_share)

        initViews()
        initEvents()
        loadSession()
    }

    private fun initViews() {
        textName = findViewById(R.id.textName)
        textEmail = findViewById(R.id.textEmail)
        btnSettings = findViewById(R.id.btnSettings)
        btnSupport = findViewById(R.id.btnSupport)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun initEvents() {
        btnSettings.setOnClickListener {
            Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
        }

        btnSupport.setOnClickListener {
            Toast.makeText(this, "Soporte", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            SessionManager.clear(this)

            val intent = Intent(this, HomeProductActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }
}
