package com.compensar.tienda.ui.platform

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.model.shop.ShopSelectActivity
import com.compensar.tienda.ui.model.user.UserSelectActivity

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var cardUser: CardView
    private lateinit var cardShop: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_admin)

        initViews()
        initEvents()
    }

    private fun initViews() {
        cardUser = findViewById(R.id.cardUser)
        cardShop = findViewById(R.id.cardShop)
    }

    private fun initEvents() {
        cardUser.setOnClickListener {
            val intent = Intent(this, UserSelectActivity::class.java)
            startActivity(intent)
        }

        cardShop.setOnClickListener {
            val intent = Intent(this, ShopSelectActivity::class.java)
            startActivity(intent)
        }
    }
}