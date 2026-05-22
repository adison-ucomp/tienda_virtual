package com.compensar.tienda.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.ModuleModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.address.AddressSelectActivity
import com.compensar.tienda.ui.model.category.CategorySelectActivity
import com.compensar.tienda.ui.model.gateway.GatewaySelectActivity
import com.compensar.tienda.ui.model.epayco.EpaycoSelectActivity
import com.compensar.tienda.ui.model.image.ImageSelectActivity
import com.compensar.tienda.ui.model.module.ModuleSelectActivity
import com.compensar.tienda.ui.model.order.OrderSelectActivity
import com.compensar.tienda.ui.model.payment.PaymentSelectActivity
import com.compensar.tienda.ui.model.product.ProductSelectActivity
import com.compensar.tienda.ui.model.purchase.PurchaseSelectActivity
import com.compensar.tienda.ui.model.role.RoleSelectActivity
import com.compensar.tienda.ui.model.seller.SellerSelectActivity
import com.compensar.tienda.ui.model.shipment.ShipmentSelectActivity
import com.compensar.tienda.ui.model.shop.ShopSelectActivity
import com.compensar.tienda.ui.model.specify.SpecifySelectActivity
import com.compensar.tienda.ui.model.ubication.UbicationSelectActivity
import com.compensar.tienda.ui.model.user.UserSelectActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var dataShop: CardView
    private lateinit var dataUser: CardView

    private lateinit var cardModule: CardView
    private lateinit var cardAddress: CardView
    private lateinit var cardCategory: CardView
    private lateinit var cardGateway: CardView
    private lateinit var cardImage: CardView
    private lateinit var cardOrder: CardView
    private lateinit var cardPayment: CardView
    private lateinit var cardEpayco: CardView
    private lateinit var cardProduct: CardView
    private lateinit var cardPurchase: CardView
    private lateinit var cardRole: CardView
    private lateinit var cardSeller: CardView
    private lateinit var cardShipment: CardView
    private lateinit var cardShop: CardView
    private lateinit var cardSpecify: CardView
    private lateinit var cardUbication: CardView
    private lateinit var cardUser: CardView
    private lateinit var cardReport: CardView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("module")

    private val moduleViews = mutableMapOf<String, ModuleDashboardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_dashboard)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadModules()
    }

    override fun onResume() {
        super.onResume()
        loadModules()
    }

    private fun initViews() {
        dataShop = findViewById(R.id.dataShop)
        dataUser = findViewById(R.id.dataUser)

        cardModule = findViewById(R.id.cardModule)
        cardAddress = findViewById(R.id.cardAddress)
        cardCategory = findViewById(R.id.cardCategory)
        cardGateway = findViewById(R.id.cardGateway)
        cardImage = findViewById(R.id.cardImage)
        cardOrder = findViewById(R.id.cardOrder)
        cardPayment = findViewById(R.id.cardPayment)
        cardEpayco = findViewById(R.id.cardEpayco)
        cardProduct = findViewById(R.id.cardProduct)
        cardPurchase = findViewById(R.id.cardPurchase)
        cardRole = findViewById(R.id.cardRole)
        cardSeller = findViewById(R.id.cardSeller)
        cardShipment = findViewById(R.id.cardShipment)
        cardShop = findViewById(R.id.cardShop)
        cardSpecify = findViewById(R.id.cardSpecify)
        cardUbication = findViewById(R.id.cardUbication)
        cardUser = findViewById(R.id.cardUser)
        cardReport = findViewById(R.id.cardReport)

        moduleViews["module"] = ModuleDashboardItem(cardModule, findViewById(R.id.titleModule), findViewById(
            R.id.detailModule), "Modulos")
        moduleViews["address"] = ModuleDashboardItem(cardAddress, findViewById(R.id.titleAddress), findViewById(
            R.id.detailAddress), "Direcciones")
        moduleViews["category"] = ModuleDashboardItem(cardCategory, findViewById(R.id.titleCategory), findViewById(
            R.id.detailCategory), "Categorias")
        moduleViews["gateway"] = ModuleDashboardItem(cardGateway, findViewById(R.id.titleGateway), findViewById(
            R.id.detailGateway), "Pasarelas")
        moduleViews["image"] = ModuleDashboardItem(cardImage, findViewById(R.id.titleImage), findViewById(
            R.id.detailImage), "Imágenes")
        moduleViews["order"] = ModuleDashboardItem(cardOrder, findViewById(R.id.titleOrder), findViewById(
            R.id.detailOrder), "Ordenes")
        moduleViews["payment"] = ModuleDashboardItem(cardPayment, findViewById(R.id.titlePayment), findViewById(
            R.id.detailPayment), "Pagos")
        moduleViews["epayco"] = ModuleDashboardItem(cardEpayco, findViewById(R.id.titleEpayco), findViewById(
            R.id.detailEpayco), "Epayco")
        moduleViews["product"] = ModuleDashboardItem(cardProduct, findViewById(R.id.titleProduct), findViewById(
            R.id.detailProduct), "Productos")
        moduleViews["purchase"] = ModuleDashboardItem(cardPurchase, findViewById(R.id.titlePurchase), findViewById(
            R.id.detailPurchase), "Compras")
        moduleViews["role"] = ModuleDashboardItem(cardRole, findViewById(R.id.titleRole), findViewById(
            R.id.detailRole), "Roles")
        moduleViews["seller"] = ModuleDashboardItem(cardSeller, findViewById(R.id.titleSeller), findViewById(
            R.id.detailSeller), "Vendedores")
        moduleViews["shipment"] = ModuleDashboardItem(cardShipment, findViewById(R.id.titleShipment), findViewById(
            R.id.detailShipment), "Envios")
        moduleViews["shop"] = ModuleDashboardItem(cardShop, findViewById(R.id.titleShop), findViewById(
            R.id.detailShop), "Tiendas")
        moduleViews["specify"] = ModuleDashboardItem(cardSpecify, findViewById(R.id.titleSpecify), findViewById(
            R.id.detailSpecify), "Especifaciones")
        moduleViews["ubication"] = ModuleDashboardItem(cardUbication, findViewById(R.id.titleUbication), findViewById(
            R.id.detailUbication), "Ubicaciones")
        moduleViews["user"] = ModuleDashboardItem(cardUser, findViewById(R.id.titleUser), findViewById(
            R.id.detailUser), "Usuarios")
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

        cardModule.setOnClickListener {
            val intent = Intent(this, ModuleSelectActivity::class.java)
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

        cardEpayco.setOnClickListener {
            val intent = Intent(this, EpaycoSelectActivity::class.java)
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

        cardShipment.setOnClickListener {
            val intent = Intent(this, ShipmentSelectActivity::class.java)
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

        cardReport.setOnClickListener {
            val intent = Intent(this, AdminReportDataActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadModules() {
        collection
            .get()
            .addOnSuccessListener { result ->
                val modules = result.documents.mapNotNull { document ->
                    document.toObject(ModuleModel::class.java)
                }

                moduleViews.forEach { (key, item) ->
                    val module = modules.firstOrNull { it.model == key }

                    item.card.visibility = if (module?.state == false) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                    val name = module?.name?.takeIf { it.isNotBlank() } ?: item.defaultName
                    item.title.text = "Gestionar $name"
                    item.detail.text = module?.detail?.takeIf { it.isNotBlank() } ?: ""
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private data class ModuleDashboardItem(
        val card: CardView,
        val title: TextView,
        val detail: TextView,
        val defaultName: String
    )
}
