package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.home.HomeProductActivity

class ProfileSellerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var actionLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_seller)

        initViews()
        initEvents()
        loadSession()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        textName = findViewById(R.id.textName)
        textEmail = findViewById(R.id.textEmail)
        actionLogout = findViewById(R.id.actionLogout)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener {
            finish()
        }

        actionLogout.setOnClickListener {
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
