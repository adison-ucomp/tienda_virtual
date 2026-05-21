package com.compensar.tienda.ui.seller

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation

class SellerOrderListActivity : AppCompatActivity() {

    private lateinit var orderListContainer: LinearLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navOrders: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.seller_order_list)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadOrders()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun initViews() {
        orderListContainer = findViewById(R.id.orderListContainer)
        navHome = findViewById(R.id.navHome)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun initEvents() {
        navHome.setOnClickListener {
            val intent = Intent(this, SellerDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        navOrders.setOnClickListener {
            loadOrders()
        }

        navProfile.setOnClickListener {
            SessionNavigation.openProfileOrLogin(this)
        }
    }

    private fun loadOrders() {
        SellerDataHelper.loadSellerData(
            context = this,
            onSuccess = { data ->
                renderOrders(
                    SellerDataHelper.buildSellerOrders(data)
                        .sortedWith(compareByDescending<SellerDataHelper.SellerOrder> { it.order.date.orEmpty() }.thenByDescending { it.order.hour.orEmpty() })
                )
            },
            onFailure = {
                Toast.makeText(this, "No fue posible cargar los pedidos", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun renderOrders(orders: List<SellerDataHelper.SellerOrder>) {
        orderListContainer.removeAllViews()

        if (orders.isEmpty()) {
            val text = TextView(this)
            text.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text.gravity = Gravity.CENTER
            text.text = "No tienes pedidos registrados"
            text.setTextColor(Color.parseColor("#666666"))
            text.textSize = 15f
            orderListContainer.addView(text)
            return
        }

        orders.forEach { item ->
            orderListContainer.addView(createOrderCard(item))
        }
    }

    private fun createOrderCard(item: SellerDataHelper.SellerOrder): View {
        val card = CardView(this)
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 18.dp()
        }
        card.radius = 20.dp().toFloat()
        card.cardElevation = 0f
        card.setCardBackgroundColor(Color.WHITE)

        val container = LinearLayout(this)
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(18.dp(), 18.dp(), 18.dp(), 18.dp())

        val reference = TextView(this)
        reference.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        reference.text = "#${item.order.reference.orEmpty().ifEmpty { item.order.register.toString() }}"
        reference.setTextColor(Color.parseColor("#111111"))
        reference.textSize = 17f
        reference.setTypeface(null, Typeface.BOLD)

        val date = TextView(this)
        date.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 8.dp()
        }
        date.text = "${item.order.date.orEmpty()} ${item.order.hour.orEmpty()}".trim()
        date.setTextColor(Color.parseColor("#666666"))
        date.textSize = 13f

        val client = TextView(this)
        client.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 10.dp()
        }
        client.text = SellerDataHelper.getUserFullName(item.user)
        client.setTextColor(Color.parseColor("#111111"))
        client.textSize = 14f

        val totalRow = LinearLayout(this)
        totalRow.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 18.dp()
        }
        totalRow.gravity = Gravity.CENTER_VERTICAL
        totalRow.orientation = LinearLayout.HORIZONTAL

        val totalContainer = LinearLayout(this)
        totalContainer.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        totalContainer.orientation = LinearLayout.VERTICAL

        val totalLabel = TextView(this)
        totalLabel.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        totalLabel.text = "TOTAL"
        totalLabel.setTextColor(Color.parseColor("#666666"))
        totalLabel.textSize = 11f
        totalLabel.setTypeface(null, Typeface.BOLD)

        val totalValue = TextView(this)
        totalValue.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 4.dp()
        }
        totalValue.text = SellerDataHelper.formatCurrency(item.order.total ?: item.purchases.sumOf { it.total ?: 0.0 })
        totalValue.setTextColor(Color.parseColor("#111111"))
        totalValue.textSize = 20f
        totalValue.setTypeface(null, Typeface.BOLD)

        totalContainer.addView(totalLabel)
        totalContainer.addView(totalValue)

        val status = TextView(this)
        status.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            36.dp()
        )
        status.gravity = Gravity.CENTER
        status.setPadding(14.dp(), 0, 14.dp(), 0)
        status.text = item.shipment?.name ?: "Sin estado"
        status.setTextColor(getStatusColor(item.order.idShipment))
        status.textSize = 12f
        status.setTypeface(null, Typeface.BOLD)
        status.setBackgroundResource(R.drawable.bg_button)

        totalRow.addView(totalContainer)
        totalRow.addView(status)

        val action = TextView(this)
        action.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            48.dp()
        ).apply {
            topMargin = 18.dp()
        }
        action.gravity = Gravity.CENTER
        action.text = "Ver detalles"
        action.setTextColor(Color.WHITE)
        action.textSize = 14f
        action.setTypeface(null, Typeface.BOLD)
        action.setBackgroundColor(Color.BLACK)
        action.setOnClickListener {
            val intent = Intent(this, SellerOrderShowActivity::class.java)
            intent.putExtra("register", item.order.register)
            startActivity(intent)
        }

        container.addView(reference)
        container.addView(date)
        container.addView(client)
        container.addView(totalRow)
        container.addView(action)
        card.addView(container)

        return card
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
