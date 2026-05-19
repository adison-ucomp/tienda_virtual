package com.compensar.tienda.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.model.address.AddressSelectActivity
import com.compensar.tienda.ui.model.gateway.GatewaySelectActivity
import com.compensar.tienda.ui.model.image.ImageSelectActivity
import com.compensar.tienda.ui.model.payment.PaymentSelectActivity
import com.compensar.tienda.ui.model.product.ProductSelectActivity
import com.compensar.tienda.ui.model.purchase.PurchaseSelectActivity
import com.compensar.tienda.ui.model.role.RoleSelectActivity
import com.compensar.tienda.ui.model.seller.SellerSelectActivity
import com.compensar.tienda.ui.model.shop.ShopSelectActivity
import com.compensar.tienda.ui.model.specify.SpecifySelectActivity
import com.compensar.tienda.ui.model.user.UserSelectActivity

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var dataShop: CardView
    private lateinit var dataUser: CardView

    private lateinit var cardAddress: CardView
    private lateinit var cardGateway: CardView
    private lateinit var cardImage: CardView
    private lateinit var cardPayment: CardView
    private lateinit var cardProduct: CardView
    private lateinit var cardPurchase: CardView
    private lateinit var cardRole: CardView
    private lateinit var cardSeller: CardView
    private lateinit var cardShop: CardView
    private lateinit var cardSpecify: CardView
    private lateinit var cardUser: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_admin)

        initViews()
        initEvents()
    }

    private fun initViews() {
        dataShop = findViewById(R.id.dataShop)
        dataUser = findViewById(R.id.dataUser)

        cardAddress = findViewById(R.id.cardAddress)
        cardGateway = findViewById(R.id.cardGateway)
        cardImage = findViewById(R.id.cardImage)
        cardPayment = findViewById(R.id.cardPayment)
        cardProduct = findViewById(R.id.cardProduct)
        cardPurchase = findViewById(R.id.cardPurchase)
        cardRole = findViewById(R.id.cardRole)
        cardSeller = findViewById(R.id.cardSeller)
        cardShop = findViewById(R.id.cardShop)
        cardSpecify = findViewById(R.id.cardSpecify)
        cardUser = findViewById(R.id.cardUser)
    }

    private fun initEvents() {
        dataShop.setOnClickListener {
            val intent = Intent(this, AdminShopListActivity::class.java)
            startActivity(intent)
        }

        dataUser.setOnClickListener {
            val intent = Intent(this, AdminUserListActivity::class.java)
            startActivity(intent)
        }





        cardAddress.setOnClickListener {
            val intent = Intent(this, AddressSelectActivity::class.java)
            startActivity(intent)
        }

        cardGateway.setOnClickListener {
            val intent = Intent(this, GatewaySelectActivity::class.java)
            startActivity(intent)
        }

        cardImage.setOnClickListener {
            val intent = Intent(this, ImageSelectActivity::class.java)
            startActivity(intent)
        }

        cardPayment.setOnClickListener {
            val intent = Intent(this, PaymentSelectActivity::class.java)
            startActivity(intent)
        }

        cardProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectActivity::class.java)
            startActivity(intent)
        }

        cardPurchase.setOnClickListener {
            val intent = Intent(this, PurchaseSelectActivity::class.java)
            startActivity(intent)
        }

        cardRole.setOnClickListener {
            val intent = Intent(this, RoleSelectActivity::class.java)
            startActivity(intent)
        }

        cardSeller.setOnClickListener {
            val intent = Intent(this, SellerSelectActivity::class.java)
            startActivity(intent)
        }

        cardShop.setOnClickListener {
            val intent = Intent(this, ShopSelectActivity::class.java)
            startActivity(intent)
        }

        cardSpecify.setOnClickListener {
            val intent = Intent(this, SpecifySelectActivity::class.java)
            startActivity(intent)
        }

        cardUser.setOnClickListener {
            val intent = Intent(this, UserSelectActivity::class.java)
            startActivity(intent)
        }
    }
}