package com.compensar.tienda.ui.profile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_admin)

        initViews()
        initEvents()
        loadSession()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        textName = findViewById(R.id.txtNameAdmin)
        textEmail = findViewById(R.id.txtEmail)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener {
            finish()
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }
}
