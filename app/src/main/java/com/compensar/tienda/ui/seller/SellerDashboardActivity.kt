package com.compensar.tienda.ui.seller

import android.content.Intent
import android.graphics.Color
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
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SellerDataHelper
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.report.ReportDataSaleActivity
import kotlin.math.abs
import kotlin.math.roundToInt

class SellerDashboardActivity : AppCompatActivity() {

    private lateinit var cardSellerReport: CardView
    private lateinit var textSalesAmount: TextView
    private lateinit var textSalesTrend: TextView
    private lateinit var chartContainer: LinearLayout
    private lateinit var textInventoryCount: TextView
    private lateinit var pendingOrdersContainer: LinearLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navOrders: LinearLayout
    private lateinit var navProducts: LinearLayout

    private var sellerData: SellerDataHelper.SellerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.seller_dashboard)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadDashboard()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun initViews() {
        cardSellerReport = findViewById(R.id.cardSellerReport)
        textSalesAmount = findViewById(R.id.textSalesAmount)
        textSalesTrend = findViewById(R.id.textSalesTrend)
        chartContainer = findViewById(R.id.chartContainer)
        textInventoryCount = findViewById(R.id.textInventoryCount)
        pendingOrdersContainer = findViewById(R.id.pendingOrdersContainer)
        navHome = findViewById(R.id.navHome)
        navOrders = findViewById(R.id.navOrders)
        navProducts = findViewById(R.id.navProducts)
    }

    private fun initEvents() {
        navHome.setOnClickListener {
            loadDashboard()
        }

        navOrders.setOnClickListener {
            startActivity(Intent(this, SellerOrderListActivity::class.java))
        }

        navProducts.setOnClickListener {
            val intent = Intent(this, SellerProductListActivity::class.java)
            startActivity(intent)
        }

        cardSellerReport.setOnClickListener {
            val intent = Intent(this, ReportDataSaleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDashboard() {
        SellerDataHelper.loadSellerData(
            context = this,
            onSuccess = { data ->
                sellerData = data
                renderSalesSummary(data)
                renderInventory(data)
                renderPendingOrders(data)
            },
            onFailure = {
                Toast.makeText(this, "No fue posible cargar el resumen", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun renderSalesSummary(data: SellerDataHelper.SellerData) {
        val totalSales = data.purchases.sumOf { it.total ?: 0.0 }
        val sellerOrders = SellerDataHelper.buildSellerOrders(data)
        val purchasesByOrder = data.purchases.groupBy { it.idOrder }
        val lastMonths = SellerDataHelper.getLastSixMonths()
        val totalsByMonth = lastMonths.associateWith { month ->
            sellerOrders
                .filter { SellerDataHelper.getOrderMonth(it.order) == month }
                .sumOf { order ->
                    purchasesByOrder[order.order.register].orEmpty().sumOf { it.total ?: 0.0 }
                }
        }

        textSalesAmount.text = SellerDataHelper.formatCurrency(totalSales)
        renderTrend(lastMonths, totalsByMonth)
        renderChart(lastMonths, totalsByMonth)
    }

    private fun renderTrend(
        lastMonths: List<String>,
        totalsByMonth: Map<String, Double>
    ) {
        val currentMonth = lastMonths.lastOrNull().orEmpty()
        val previousMonth = lastMonths.dropLast(1).lastOrNull().orEmpty()
        val currentTotal = totalsByMonth[currentMonth] ?: 0.0
        val previousTotal = totalsByMonth[previousMonth] ?: 0.0

        if (currentTotal == 0.0 && previousTotal == 0.0) {
            textSalesTrend.text = "0%"
            textSalesTrend.setTextColor(Color.parseColor("#A8AEC0"))
            textSalesTrend.setBackgroundColor(Color.parseColor("#F2F5FA"))
            return
        }

        if (previousTotal == 0.0) {
            textSalesTrend.text = if (currentTotal > 0.0) "↑ 100%" else "0%"
            textSalesTrend.setTextColor(Color.parseColor(if (currentTotal > 0.0) "#10B981" else "#A8AEC0"))
            textSalesTrend.setBackgroundColor(Color.parseColor(if (currentTotal > 0.0) "#DDFBEA" else "#F2F5FA"))
            return
        }

        val variation = ((currentTotal - previousTotal) / previousTotal) * 100
        val symbol = if (variation >= 0) "↑" else "↓"
        val color = if (variation >= 0) "#10B981" else "#EF4444"
        val background = if (variation >= 0) "#DDFBEA" else "#FFF1F2"

        textSalesTrend.text = "$symbol ${"%.2f".format(abs(variation))}%"
        textSalesTrend.setTextColor(Color.parseColor(color))
        textSalesTrend.setBackgroundColor(Color.parseColor(background))
    }

    private fun renderChart(
        lastMonths: List<String>,
        totalsByMonth: Map<String, Double>
    ) {
        chartContainer.removeAllViews()

        val maxTotal = totalsByMonth.values.maxOrNull() ?: 0.0

        lastMonths.forEach { month ->
            val total = totalsByMonth[month] ?: 0.0
            val height = if (maxTotal > 0.0 && total > 0.0) {
                (42 + ((total / maxTotal) * 96)).roundToInt()
            } else {
                42
            }

            val column = LinearLayout(this)
            column.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginEnd = 6
            }
            column.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            column.orientation = LinearLayout.VERTICAL

            val bar = View(this)
            bar.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            ).apply {
                bottomMargin = 8
            }
            bar.setBackgroundColor(
                Color.parseColor(
                    if (total > 0.0) {
                        "#111827"
                    } else {
                        "#EEF2F8"
                    }
                )
            )

            val label = TextView(this)
            label.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            label.gravity = Gravity.CENTER
            label.text = month
            label.setTextColor(Color.parseColor(if (total > 0.0) "#111827" else "#A8AEC0"))
            label.textSize = 8f

            column.addView(bar)
            column.addView(label)
            chartContainer.addView(column)
        }
    }

    private fun renderInventory(data: SellerDataHelper.SellerData) {
        val availableProducts = data.products.count { it.stock > 0 }
        textInventoryCount.text = availableProducts.toString()
    }

    private fun renderPendingOrders(data: SellerDataHelper.SellerData) {
        pendingOrdersContainer.removeAllViews()

        val pendingOrders = SellerDataHelper.buildSellerOrders(data)
            .filter { it.order.idShipment == 1L }
            .sortedWith(compareBy<SellerDataHelper.SellerOrder> { it.order.date.orEmpty() }.thenBy { it.order.hour.orEmpty() })
            .take(5)

        if (pendingOrders.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            emptyText.text = "No tienes pedidos pendientes"
            emptyText.setTextColor(Color.parseColor("#666666"))
            emptyText.textSize = 14f
            pendingOrdersContainer.addView(emptyText)
            return
        }

        pendingOrders.forEach { item ->
            pendingOrdersContainer.addView(createPendingOrderView(item))
        }
    }

    private fun createPendingOrderView(item: SellerDataHelper.SellerOrder): View {
        val row = LinearLayout(this)
        row.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            76.dp()
        ).apply {
            bottomMargin = 8.dp()
        }
        row.gravity = Gravity.CENTER_VERTICAL
        row.orientation = LinearLayout.HORIZONTAL

        val icon = TextView(this)
        icon.layoutParams = LinearLayout.LayoutParams(52.dp(), 52.dp())
        icon.gravity = Gravity.CENTER
        icon.text = "▣"
        icon.setTextColor(Color.WHITE)
        icon.textSize = 20f
        icon.setBackgroundColor(Color.parseColor("#111827"))

        val textContainer = LinearLayout(this)
        textContainer.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            marginStart = 16.dp()
        }
        textContainer.orientation = LinearLayout.VERTICAL

        val title = TextView(this)
        title.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        title.text = "#${item.order.reference.orEmpty().ifEmpty { item.order.register.toString() }}"
        title.setTextColor(Color.parseColor("#111827"))
        title.textSize = 12f
        title.setTypeface(null, android.graphics.Typeface.BOLD)

        val detail = TextView(this)
        detail.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        detail.text = "${item.order.date.orEmpty()} ${item.order.hour.orEmpty()}"
        detail.setTextColor(Color.parseColor("#A8AEC0"))
        detail.textSize = 10f

        textContainer.addView(title)
        textContainer.addView(detail)

        val button = TextView(this)
        button.layoutParams = LinearLayout.LayoutParams(92.dp(), 42.dp())
        button.gravity = Gravity.CENTER
        button.text = "Preparar"
        button.setTextColor(Color.WHITE)
        button.textSize = 12f
        button.setTypeface(null, android.graphics.Typeface.BOLD)
        button.setBackgroundResource(R.drawable.bg_button_dark)
        button.setOnClickListener {
            val intent = Intent(this, SellerOrderShowActivity::class.java)
            intent.putExtra("register", item.order.register)
            startActivity(intent)
        }

        row.addView(icon)
        row.addView(textContainer)
        row.addView(button)

        return row
    }


    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
