package com.compensar.tienda.ui.platform

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.ui.model.category.CategorySelectActivity

class HomeProductActivity : AppCompatActivity() {

    // private lateinit var btnMenu: TextView
    private lateinit var btnCart: TextView

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionAccount: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout

    private lateinit var productOne: LinearLayout
    private lateinit var productTwo: LinearLayout
    private lateinit var productThree: LinearLayout
    private lateinit var productFour: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_product)

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
        // btnMenu = findViewById(R.id.btnMenu)
        btnCart = findViewById(R.id.btnCart)

        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)

        productOne = findViewById(R.id.productOne)
        productTwo = findViewById(R.id.productTwo)
        productThree = findViewById(R.id.productThree)
        productFour = findViewById(R.id.productFour)
    }

    private fun initEvents() {
        /*btnMenu.setOnClickListener {
            Toast.makeText(this, "Menú", Toast.LENGTH_SHORT).show()
        }*/

        /*btnCart.setOnClickListener {
            val intent = Intent(this, HomeLoginActivity::class.java)
            startActivity(intent)
        }*/

        btnCart.setOnClickListener {
            val intent = Intent(this, CategorySelectActivity::class.java)
            startActivity(intent)
        }

        actionHome.setOnClickListener {
            Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
        }

        actionCategory.setOnClickListener {
            val intent = Intent(this, HomeCategoryActivity::class.java)
            startActivity(intent)
        }

        actionShopping.setOnClickListener {
            val intent = Intent(this, BuyerShoppingActivity::class.java)
            startActivity(intent)
        }

        actionAddress.setOnClickListener {
            val intent = Intent(this, BuyerAddressActivity::class.java)
            startActivity(intent)
        }

        actionAccount.setOnClickListener {
            val intent = Intent(this, HomeLoginActivity::class.java)
            startActivity(intent)
        }

        /*productOne.setOnClickListener {
            Toast.makeText(this, "Smartphone Vertex Pro Max 5G", Toast.LENGTH_SHORT).show()
        }

        productTwo.setOnClickListener {
            Toast.makeText(this, "Iphone 11", Toast.LENGTH_SHORT).show()
        }

        productThree.setOnClickListener {
            Toast.makeText(this, "Guante Sable", Toast.LENGTH_SHORT).show()
        }

        productFour.setOnClickListener {
            Toast.makeText(this, "Lámpara de Estudio", Toast.LENGTH_SHORT).show()
        }*/

        productOne.setOnClickListener {
            val intent = Intent(this, HomeDetailActivity::class.java)
            startActivity(intent)
        }

        productTwo.setOnClickListener {
            val intent = Intent(this, HomeDetailActivity::class.java)
            startActivity(intent)
        }

        productThree.setOnClickListener {
            val intent = Intent(this, HomeDetailActivity::class.java)
            startActivity(intent)
        }

        productFour.setOnClickListener {
            val intent = Intent(this, HomeDetailActivity::class.java)
            startActivity(intent)
        }
    }
}