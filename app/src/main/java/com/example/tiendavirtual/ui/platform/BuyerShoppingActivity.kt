package com.example.tiendavirtual.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class BuyerShoppingActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navCategory: LinearLayout
    private lateinit var navShopping: LinearLayout
    private lateinit var navAddress: LinearLayout
    private lateinit var navAccount: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_shopping)

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
        navHome = findViewById(R.id.navHome)
        navCategory = findViewById(R.id.navCategory)
        navShopping = findViewById(R.id.navShopping)
        navAddress = findViewById(R.id.navAddress)
        navAccount = findViewById(R.id.navAccount)
    }

    private fun initEvents() {
        navHome.setOnClickListener {
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        navCategory.setOnClickListener {
            val intent = Intent(this, HomeCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        navShopping.setOnClickListener {
            Toast.makeText(this, "Compras", Toast.LENGTH_SHORT).show()
        }

        navAddress.setOnClickListener {
            val intent = Intent(this, BuyerAddressActivity::class.java)
            startActivity(intent)
            finish()
        }

        navAccount.setOnClickListener {
            val intent = Intent(this, HomeLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}