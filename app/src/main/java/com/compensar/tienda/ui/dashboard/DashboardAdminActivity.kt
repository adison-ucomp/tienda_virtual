package com.compensar.tienda.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.admin.AdminUserListActivity
import com.compensar.tienda.ui.admin.AdminShopListActivity
import com.compensar.tienda.ui.model.address.AddressSelectActivity
import com.compensar.tienda.ui.model.category.CategorySelectActivity
import com.compensar.tienda.ui.model.gateway.GatewaySelectActivity
import com.compensar.tienda.ui.model.image.ImageSelectActivity
import com.compensar.tienda.ui.model.order.OrderSelectActivity
import com.compensar.tienda.ui.model.payment.PaymentSelectActivity
import com.compensar.tienda.ui.model.product.ProductSelectActivity
import com.compensar.tienda.ui.model.purchase.PurchaseSelectActivity
import com.compensar.tienda.ui.model.role.RoleSelectActivity
import com.compensar.tienda.ui.model.seller.SellerSelectActivity
import com.compensar.tienda.ui.model.shop.ShopSelectActivity
import com.compensar.tienda.ui.model.specify.SpecifySelectActivity
import com.compensar.tienda.ui.model.user.UserSelectActivity
import com.compensar.tienda.ui.model.ubication.UbicationSelectActivity

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var dataShop: CardView
    private lateinit var dataUser: CardView

    private lateinit var cardAddress: CardView
    private lateinit var cardCategory: CardView
    private lateinit var cardGateway: CardView
    private lateinit var cardImage: CardView
    private lateinit var cardOrder: CardView
    private lateinit var cardPayment: CardView
    private lateinit var cardProduct: CardView
    private lateinit var cardPurchase: CardView
    private lateinit var cardRole: CardView
    private lateinit var cardSeller: CardView
    private lateinit var cardShop: CardView
    private lateinit var cardSpecify: CardView
    private lateinit var cardUbication: CardView
    private lateinit var cardUser: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_admin)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
    }

    private fun initViews() {
        dataShop = findViewById(R.id.dataShop)
        dataUser = findViewById(R.id.dataUser)

        cardAddress = findViewById(R.id.cardAddress)
        cardCategory = findViewById(R.id.cardCategory)
        cardGateway = findViewById(R.id.cardGateway)
        cardImage = findViewById(R.id.cardImage)
        cardOrder = findViewById(R.id.cardOrder)
        cardPayment = findViewById(R.id.cardPayment)
        cardProduct = findViewById(R.id.cardProduct)
        cardPurchase = findViewById(R.id.cardPurchase)
        cardRole = findViewById(R.id.cardRole)
        cardSeller = findViewById(R.id.cardSeller)
        cardShop = findViewById(R.id.cardShop)
        cardSpecify = findViewById(R.id.cardSpecify)
        cardUbication = findViewById(R.id.cardUbication)
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

        cardCategory.setOnClickListener {
            val intent = Intent(this, CategorySelectActivity::class.java)
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

        cardOrder.setOnClickListener {
            val intent = Intent(this, OrderSelectActivity::class.java)
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

        cardUbication.setOnClickListener {
            val intent = Intent(this, UbicationSelectActivity::class.java)
            startActivity(intent)
        }

        cardUser.setOnClickListener {
            val intent = Intent(this, UserSelectActivity::class.java)
            startActivity(intent)
        }
    }
}
