package com.example.tiendavirtual.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class HomeRestoreActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnBackLogin: TextView
    private lateinit var btnRestore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_restore)

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
        btnRestore = findViewById(R.id.btnRestore)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            goToLogin()
        }

        btnBackLogin.setOnClickListener {
            goToLogin()
        }

        btnRestore.setOnClickListener {
            // Aquí después puedes agregar la lógica para recuperar contraseña
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, HomeLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}