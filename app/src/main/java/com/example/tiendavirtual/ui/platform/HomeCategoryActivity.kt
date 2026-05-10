package com.example.tiendavirtual.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R

class HomeCategoryActivity : AppCompatActivity() {

    private lateinit var btnCart: TextView

    private lateinit var navHome: LinearLayout
    private lateinit var navCategory: LinearLayout
    private lateinit var navAccount: LinearLayout

    private lateinit var categoryTechnology: LinearLayout
    private lateinit var categorySport: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_category)

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
        btnCart = findViewById(R.id.btnCart)

        navHome = findViewById(R.id.navHome)
        navCategory = findViewById(R.id.navCategory)
        navAccount = findViewById(R.id.navAccount)

        categoryTechnology = findViewById(R.id.categoryTechnology)
        categorySport = findViewById(R.id.categorySport)
    }

    private fun initEvents() {
        btnCart.setOnClickListener {
            val intent = Intent(this, HomeLoginActivity::class.java)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        navCategory.setOnClickListener {
            Toast.makeText(this, "Categorías", Toast.LENGTH_SHORT).show()
        }

        navAccount.setOnClickListener {
            val intent = Intent(this, HomeLoginActivity::class.java)
            startActivity(intent)
        }

        categoryTechnology.setOnClickListener {
            Toast.makeText(this, "Tecnología", Toast.LENGTH_SHORT).show()
        }

        categorySport.setOnClickListener {
            Toast.makeText(this, "Deporte", Toast.LENGTH_SHORT).show()
        }
    }
}