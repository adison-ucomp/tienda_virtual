package com.example.tiendavirtual.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class HomePasswordActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnBackLogin: TextView
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_password)

        applyWindowInsets()
        initViews()
        initEvents()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnBackLogin = findViewById(R.id.btnBackLogin)
        btnContinue = findViewById(R.id.btnContinue)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            goToLogin()
        }

        btnBackLogin.setOnClickListener {
            goToLogin()
        }

        btnContinue.setOnClickListener {
            // Pendiente
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}