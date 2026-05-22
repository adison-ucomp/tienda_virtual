package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SellerDataHelper
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.seller.SellerDashboardActivity
import com.compensar.tienda.ui.seller.SellerOrderListActivity
import com.compensar.tienda.ui.seller.SellerProductListActivity
import com.compensar.tienda.ui.seller.SellerShopListActivity
import com.compensar.tienda.ui.setting.SettingSellerActivity

class ProfileSellerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var textProductCount: TextView
    private lateinit var textOrderCount: TextView
    private lateinit var textShopCount: TextView
    private lateinit var btnEditProfile: LinearLayout
    private lateinit var cardProducts: CardView
    private lateinit var cardOrders: CardView
    private lateinit var cardShops: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_seller)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSession()
        loadCounters()
    }

    override fun onResume() {
        super.onResume()
        loadSession()
        loadCounters()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        textName = findViewById(R.id.textName)
        textEmail = findViewById(R.id.textEmail)
        textProductCount = findViewById(R.id.textProductCount)
        textOrderCount = findViewById(R.id.textOrderCount)
        textShopCount = findViewById(R.id.textShopCount)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        cardProducts = findViewById(R.id.cardProducts)
        cardOrders = findViewById(R.id.cardOrders)
        cardShops = findViewById(R.id.cardShops)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, SettingSellerActivity::class.java))
        }

        cardProducts.setOnClickListener {
            startActivity(Intent(this, SellerProductListActivity::class.java))
        }

        cardOrders.setOnClickListener {
            startActivity(Intent(this, SellerOrderListActivity::class.java))
        }

        cardShops.setOnClickListener {
            startActivity(Intent(this, SellerShopListActivity::class.java))
        }

        findViewById<LinearLayout?>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, SellerDashboardActivity::class.java))
            finish()
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }

    private fun loadCounters() {
        SellerDataHelper.loadSellerData(
            context = this,
            onSuccess = { data ->
                textProductCount.text = data.products.size.toString()
                textShopCount.text = data.shops.size.toString()
                textOrderCount.text = SellerDataHelper.buildSellerOrders(data).size.toString()
            },
            onFailure = { exception ->
                exception.printStackTrace()
                Toast.makeText(this, "Error al cargar conteos: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
