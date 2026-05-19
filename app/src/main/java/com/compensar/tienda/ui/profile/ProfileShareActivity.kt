package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.home.HomeProductActivity

class ProfileShareActivity : AppCompatActivity() {

    private lateinit var actionOverlayClose: View
    private lateinit var profileSharePanel: LinearLayout

    private lateinit var textName: TextView
    private lateinit var textEmail: TextView

    private lateinit var btnSettings: LinearLayout
    private lateinit var btnSupport: LinearLayout
    private lateinit var btnLogout: LinearLayout

    private var startX: Float = 0f
    private var endX: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_share)

        initViews()
        initEvents()
        loadSession()
    }

    private fun initViews() {
        actionOverlayClose = findViewById(R.id.actionOverlayClose)
        profileSharePanel = findViewById(R.id.profileSharePanel)

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

    private fun closeSession() {
        SessionManager.clear(this)

        val intent = Intent(this, HomeProductActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}