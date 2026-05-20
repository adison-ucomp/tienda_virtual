package com.compensar.tienda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.home.HomeCategoryActivity
import com.compensar.tienda.ui.home.HomeProductActivity

class ProfileBuyerActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private lateinit var cardBuyerShopping: CardView
    private lateinit var cardBuyerAddress: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_buyer)
        SessionNavigation.bindProfile(this)

        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        initEvents()
        loadSession()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        textName = findViewById(R.id.txtUserName)
        textEmail = findViewById(R.id.txtUserEmail)

        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)

        cardBuyerShopping = findViewById(R.id.cardBuyerShopping)
        cardBuyerAddress = findViewById(R.id.cardBuyerAddress)

        /*
         * En profile_buyer se usa navbar_buyer_return, que internamente incluye
         * navbar_buyer_superior. Por eso el carrito del navbar superior debe
         * ocultarse solo en esta vista, para dejar visible únicamente la flecha
         * de retorno.
         */
        findViewById<TextView?>(R.id.btnCart)?.apply {
            visibility = View.GONE
            isEnabled = false
            isClickable = false
        }
    }

    private fun initEvents() {
        actionReturn.setOnClickListener {
            finish()
        }

        actionHome.setOnClickListener {
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
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
            // Ya se encuentra en el perfil de comprador.
        }

        cardBuyerShopping.setOnClickListener {
            val intent = Intent(this, BuyerShoppingActivity::class.java)
            startActivity(intent)
        }

        cardBuyerAddress.setOnClickListener {
            val intent = Intent(this, BuyerAddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadSession() {
        textName.text = SessionManager.getFullName(this)
        textEmail.text = SessionManager.getEmail(this)
    }
}
