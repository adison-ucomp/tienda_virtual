package com.compensar.tienda.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.ui.buyer.BuyerCartShopActivity
import com.compensar.tienda.ui.buyer.BuyerPurchaseActivity
import com.compensar.tienda.ui.common.SessionNavigation

class HomeDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnCart: TextView

    private lateinit var btnAddCart: Button
    private lateinit var btnBuyNow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_detail)

        applyWindowInsets()
        initViews()
        initEvents()
        SessionNavigation.bindProfile(this)
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
        btnCart = findViewById(R.id.btnCart)

        btnAddCart = findViewById(R.id.btnAddCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, BuyerCartShopActivity::class.java)
            startActivity(intent)
        }

        btnAddCart.setOnClickListener {
            val intent = Intent(this, BuyerCartShopActivity::class.java)
            startActivity(intent)
        }

        btnBuyNow.setOnClickListener {
            val intent = Intent(this, BuyerPurchaseActivity::class.java)
            startActivity(intent)
        }
    }
}