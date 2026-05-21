package com.compensar.tienda.ui.seller

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.ProductModel
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class SellerOrderShowActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var textOrderReference: TextView
    private lateinit var textOrderClient: TextView
    private lateinit var textOrderDate: TextView
    private lateinit var textOrderStatus: TextView
    private lateinit var productListContainer: LinearLayout
    private lateinit var textOrderAddress: TextView
    private lateinit var textOrderTotal: TextView
    private lateinit var orderActionsContainer: LinearLayout
    private lateinit var btnReject: LinearLayout
    private lateinit var btnAccept: LinearLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navOrders: LinearLayout
    private lateinit var navProfile: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private var register: Long = 0
    private var currentOrder: SellerDataHelper.SellerOrder? = null
    private var currentData: SellerDataHelper.SellerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.seller_order_show)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0L)

        initViews()
        initEvents()
        loadOrder()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        textOrderReference = findViewById(R.id.textOrderReference)
        textOrderClient = findViewById(R.id.textOrderClient)
        textOrderDate = findViewById(R.id.textOrderDate)
        textOrderStatus = findViewById(R.id.textOrderStatus)
        productListContainer = findViewById(R.id.productListContainer)
        textOrderAddress = findViewById(R.id.textOrderAddress)
        textOrderTotal = findViewById(R.id.textOrderTotal)
        orderActionsContainer = findViewById(R.id.orderActionsContainer)
        btnReject = findViewById(R.id.btnReject)
        btnAccept = findViewById(R.id.btnAccept)
        navHome = findViewById(R.id.navHome)
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

        navOrders.setOnClickListener {
            val intent = Intent(this, SellerOrderListActivity::class.java)
            startActivity(intent)
            finish()
        }

        navProfile.setOnClickListener {
            SessionNavigation.openProfileOrLogin(this)
        }

        btnAccept.setOnClickListener {
            updateShipment(3L)
        }

        btnReject.setOnClickListener {
            updateShipment(4L)
        }
    }

    private fun loadOrder() {
        if (register <= 0L) {
            Toast.makeText(this, "Pedido no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        SellerDataHelper.loadSellerData(
            context = this,
            onSuccess = { data ->
                currentData = data

                val order = SellerDataHelper.buildSellerOrders(data)
                    .firstOrNull { it.order.register == register }

                if (order == null) {
                    Toast.makeText(this, "No se encontró el pedido", Toast.LENGTH_SHORT).show()
                    finish()
                    return@loadSellerData
                }

                currentOrder = order
                renderOrder(data, order)
            },
            onFailure = {
                Toast.makeText(this, "No fue posible cargar el pedido", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun renderOrder(
        data: SellerDataHelper.SellerData,
        item: SellerDataHelper.SellerOrder
    ) {
        textOrderReference.text = "Pedido #${item.order.reference.orEmpty().ifEmpty { item.order.register.toString() }}"
        textOrderClient.text = "Cliente: ${SellerDataHelper.getUserFullName(item.user)}"
        textOrderDate.text = "Fecha: ${item.order.date.orEmpty()} ${item.order.hour.orEmpty()}".trim()
        textOrderStatus.text = item.shipment?.name ?: "Sin estado"
        textOrderStatus.setTextColor(getStatusColor(item.order.idShipment))
        textOrderAddress.text = item.order.address.orEmpty().ifEmpty { "Sin dirección" }
        textOrderTotal.text = SellerDataHelper.formatCurrency(item.order.total ?: item.purchases.sumOf { it.total ?: 0.0 })

        orderActionsContainer.visibility = if (item.order.idShipment == 1L) {
            View.VISIBLE
        } else {
            View.GONE
        }

        renderProducts(data, item.purchases)
    }

    private fun renderProducts(
        data: SellerDataHelper.SellerData,
        purchases: List<PurchaseModel>
    ) {
        productListContainer.removeAllViews()

        purchases.forEach { purchase ->
            val product = data.products.firstOrNull { it.register == purchase.idProduct }
            productListContainer.addView(createProductCard(product, purchase))
        }
    }

    private fun createProductCard(
        product: ProductModel?,
        purchase: PurchaseModel
    ): View {
        val card = CardView(this)
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12.dp()
        }
        card.radius = 18.dp().toFloat()
        card.cardElevation = 0f
        card.setCardBackgroundColor(Color.WHITE)

        val row = LinearLayout(this)
        row.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.gravity = Gravity.CENTER_VERTICAL
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())

        val image = ImageView(this)
        image.layoutParams = LinearLayout.LayoutParams(
            72.dp(),
            72.dp()
        ).apply {
            marginEnd = 14.dp()
        }
        image.scaleType = ImageView.ScaleType.CENTER_CROP

        if (!product?.storefire.isNullOrBlank()) {
            Glide.with(this)
                .load(product?.storefire)
                .placeholder(R.drawable.ic_shop)
                .error(R.drawable.ic_shop)
                .into(image)
        } else {
            image.setImageResource(R.drawable.ic_shop)
        }

        val textContainer = LinearLayout(this)
        textContainer.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        textContainer.orientation = LinearLayout.VERTICAL

        val name = TextView(this)
        name.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        name.text = product?.name ?: "Producto"
        name.setTextColor(Color.BLACK)
        name.textSize = 15f
        name.setTypeface(null, Typeface.BOLD)

        val amount = TextView(this)
        amount.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 8.dp()
        }
        amount.text = "Cantidad: ${purchase.amount ?: 0}"
        amount.setTextColor(Color.parseColor("#666666"))
        amount.textSize = 13f

        textContainer.addView(name)
        textContainer.addView(amount)

        val total = TextView(this)
        total.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        total.text = SellerDataHelper.formatCurrency(purchase.total ?: 0.0)
        total.setTextColor(Color.BLACK)
        total.textSize = 16f
        total.setTypeface(null, Typeface.BOLD)

        row.addView(image)
        row.addView(textContainer)
        row.addView(total)
        card.addView(row)

        return card
    }

    private fun updateShipment(idShipment: Long) {
        val order = currentOrder?.order ?: return

        db.collection("order")
            .document(order.register.toString())
            .update("idShipment", idShipment)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                loadOrder()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No fue posible actualizar el pedido", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getStatusColor(status: Long): Int {
        return when (status) {
            1L -> Color.parseColor("#1B5E20")
            3L -> Color.parseColor("#2962FF")
            4L -> Color.parseColor("#D52D09")
            else -> Color.parseColor("#666666")
        }
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
