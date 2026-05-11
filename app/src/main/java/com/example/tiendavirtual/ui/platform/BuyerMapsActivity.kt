package com.example.tiendavirtual.ui.platform

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class BuyerMapsActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnUseCurrentLocation: TextView
    private lateinit var btnSaveAddress: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_maps)

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
        btnUseCurrentLocation = findViewById(R.id.btnUseCurrentLocation)
        btnSaveAddress = findViewById(R.id.btnSaveAddress)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            finish()
        }

        btnUseCurrentLocation.setOnClickListener {
            Toast.makeText(this, "Usar ubicación actual", Toast.LENGTH_SHORT).show()
        }

        btnSaveAddress.setOnClickListener {
            Toast.makeText(this, "Dirección guardada", Toast.LENGTH_SHORT).show()
        }
    }
}