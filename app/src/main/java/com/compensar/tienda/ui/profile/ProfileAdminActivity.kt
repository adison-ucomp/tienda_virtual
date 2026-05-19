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

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var actionHome: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private lateinit var textName: TextView
    private lateinit var textEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_admin)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSession()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionHome = findViewById(R.id.actionHome)
        actionAccount = findViewById(R.id.actionAccount)

        textName = findViewById(R.id.txtNameAdmin)
        textEmail = findViewById(R.id.txtEmail)
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
}
