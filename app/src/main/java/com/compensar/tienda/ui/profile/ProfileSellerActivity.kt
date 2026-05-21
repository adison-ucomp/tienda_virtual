package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.seller.SellerDashboardActivity
import com.compensar.tienda.ui.model.product.ProductSelectActivity
import com.compensar.tienda.ui.model.purchase.PurchaseSelectActivity

class ProfileSellerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView

    private lateinit var navHome: LinearLayout
    private lateinit var navProducts: LinearLayout
    private lateinit var navOrders: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_seller)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSession()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        textName = findViewById(R.id.textName)
        textEmail = findViewById(R.id.textEmail)

        navHome = findViewById(R.id.navHome)
        navProducts = findViewById(R.id.navProducts)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener {
            finish()
        }

        navHome.setOnClickListener {
            val intent = Intent(this, SellerDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        navProducts.setOnClickListener {
            val intent = Intent(this, ProductSelectActivity::class.java)
            startActivity(intent)
        }

        navOrders.setOnClickListener {
            val intent = Intent(this, PurchaseSelectActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            // Ya se encuentra en el perfil de vendedor.
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }
}
