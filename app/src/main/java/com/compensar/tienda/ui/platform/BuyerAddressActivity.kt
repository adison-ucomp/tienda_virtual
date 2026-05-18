package com.compensar.tienda.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R

class BuyerAddressActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnNew: Button

    private lateinit var btnEditMainAddress: TextView
    private lateinit var btnEditOfficeAddress: TextView
    private lateinit var btnEditOtherAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_address)

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
        btnNew = findViewById(R.id.btnAddressNew)

        btnEditMainAddress = findViewById(R.id.btnEditMainAddress)
        btnEditOfficeAddress = findViewById(R.id.btnEditOfficeAddress)
        btnEditOtherAddress = findViewById(R.id.btnEditOtherAddress)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            // finish()
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNew.setOnClickListener {
            val intent = Intent(this, BuyerMapsActivity::class.java)
            startActivity(intent)
        }

        btnEditMainAddress.setOnClickListener {
            Toast.makeText(this, "Editar dirección principal", Toast.LENGTH_SHORT).show()
        }

        btnEditOfficeAddress.setOnClickListener {
            Toast.makeText(this, "Editar dirección oficina", Toast.LENGTH_SHORT).show()
        }

        btnEditOtherAddress.setOnClickListener {
            Toast.makeText(this, "Editar otra dirección", Toast.LENGTH_SHORT).show()
        }
    }
}