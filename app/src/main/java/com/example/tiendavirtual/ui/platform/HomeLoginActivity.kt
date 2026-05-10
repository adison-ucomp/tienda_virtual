package com.example.tiendavirtual.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class HomeLoginActivity : AppCompatActivity() {
    private lateinit var navInicio: LinearLayout

    private lateinit var btnRegisterBuyer: TextView
    private lateinit var btnRegisterSeller: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_login)

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
        navInicio = findViewById(R.id.navInicio)

        btnRegisterBuyer = findViewById(R.id.btnRegisterBuyer)
        btnRegisterSeller = findViewById(R.id.btnRegisterSeller)
    }

    private fun initEvents() {
        navInicio.setOnClickListener {
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegisterBuyer.setOnClickListener {
            val intent = Intent(this, RegisterBuyerActivity::class.java)
            startActivity(intent)
        }

        btnRegisterSeller.setOnClickListener {
            val intent = Intent(this, RegisterSellerActivity::class.java)
            startActivity(intent)
        }
    }
}