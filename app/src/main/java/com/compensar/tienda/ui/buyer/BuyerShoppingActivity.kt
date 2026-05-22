package com.compensar.tienda.ui.buyer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.model.TradeModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.home.HomeCategoryActivity
import com.compensar.tienda.ui.home.HomeProductActivity
import com.compensar.tienda.ui.util.StatusStyleHelper
import com.google.firebase.firestore.FirebaseFirestore

class BuyerShoppingActivity : AppCompatActivity() {

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout
    private lateinit var shoppingContent: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private var shipmentMap: Map<Long, String> = emptyMap()
    private var tradeMap: Map<Long, TradeModel> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_shopping)

        applyWindowInsets()
        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        initEvents()
        SessionNavigation.bindProfile(this)
        loadShopping()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)
        shoppingContent = findViewById(R.id.shoppingContent)
    }

    private fun initEvents() {
        actionHome.setOnClickListener { startActivity(Intent(this, HomeProductActivity::class.java)); finish() }
        actionCategory.setOnClickListener { startActivity(Intent(this, HomeCategoryActivity::class.java)); finish() }
        actionShopping.setOnClickListener { loadShopping() }
        actionAddress.setOnClickListener { startActivity(Intent(this, BuyerAddressActivity::class.java)) }
        actionAccount.setOnClickListener { SessionNavigation.openProfileOrLogin(this) }
    }

    private fun loadShopping() {
        val userRegister = SessionManager.getRegister(this)
        if (userRegister <= 0) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus compras", Toast.LENGTH_SHORT).show()
            return
        }

        val keepTitle = mutableListOf<android.view.View>()
        for (i in 0 until minOf(2, shoppingContent.childCount)) keepTitle.add(shoppingContent.getChildAt(i))
        shoppingContent.removeAllViews()
        keepTitle.forEach { shoppingContent.addView(it) }

        loadStates {
            db.collection("order")
                .whereEqualTo("idUser", userRegister)
                .get()
                .addOnSuccessListener { orders ->
                    val data = orders.documents.mapNotNull { it.toObject(OrderModel::class.java) }
                        .sortedByDescending { it.register }

                    if (data.isEmpty()) {
                        shoppingContent.addView(TextView(this).apply {
                            text = "No tienes compras registradas"
                            textSize = 15f
                            setTextColor(0xFF747A8C.toInt())
                            setPadding(0, dp(34), 0, dp(34))
                        })
                        return@addOnSuccessListener
                    }

                    data.forEach { order ->
                        shoppingContent.addView(orderCard(order))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error cargando compras: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loadStates(onComplete: () -> Unit) {
        db.collection("shipment").get()
            .addOnSuccessListener { shipmentResult ->
                shipmentMap = shipmentResult.documents.mapNotNull { doc ->
                    val register = doc.getLong("register") ?: return@mapNotNull null
                    register to (doc.getString("name") ?: "Estado $register")
                }.toMap()
                db.collection("trade").get()
                    .addOnSuccessListener { tradeResult ->
                        tradeMap = tradeResult.documents.mapNotNull { doc ->
                            val data = doc.toObject(TradeModel::class.java) ?: return@mapNotNull null
                            data.register to data
                        }.toMap()
                        onComplete()
                    }
                    .addOnFailureListener {
                        tradeMap = emptyMap()
                        onComplete()
                    }
            }
            .addOnFailureListener {
                shipmentMap = emptyMap()
                tradeMap = emptyMap()
                onComplete()
            }
    }

    private fun orderCard(order: OrderModel): CardView {
        val card = CardView(this).apply {
            radius = dp(16).toFloat()
            cardElevation = dp(4).toFloat()
            setCardBackgroundColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(18), 0, 0)
            }
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val statusName = shipmentMap[order.idShipment] ?: "Pendiente"

        box.addView(TextView(this).apply {
            text = "#${order.reference ?: "ORD-${order.register}"}"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
        })

        val tradeStatus = tradeMap[order.idTrade]?.state ?: "Pendiente"

        box.addView(statusRow("Envio:", statusName, true))
        box.addView(statusRow("Transaccion:", tradeStatus, false))

        box.addView(TextView(this).apply {
            text = "Total: $ ${String.format("%,.0f", order.total ?: 0.0)}"
            textSize = 15f
            setTextColor(0xFF111111.toInt())
            setPadding(0, dp(8), 0, 0)
        })

        box.addView(TextView(this).apply {
            text = "Fecha: ${order.date ?: ""}   Hora: ${order.hour ?: ""}"
            textSize = 13f
            setTextColor(0xFF4B5060.toInt())
            setPadding(0, dp(6), 0, 0)
        })

        box.addView(TextView(this).apply {
            text = "Dirección: ${order.address ?: ""}"
            textSize = 13f
            setTextColor(0xFF4B5060.toInt())
            setPadding(0, dp(6), 0, dp(12))
        })

        val btn = Button(this).apply {
            text = "Consultar"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
            )
            setOnClickListener {
                startActivity(
                    Intent(this@BuyerShoppingActivity, BuyerOrderActivity::class.java)
                        .putExtra("orderRegister", order.register)
                )
            }
        }

        box.addView(btn)
        card.addView(box)
        return card
    }

    private fun statusRow(label: String, value: String, shipment: Boolean): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, 0)
        }

        row.addView(TextView(this).apply {
            text = label
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
        })

        row.addView(TextView(this).apply {
            textSize = 12f
            setPadding(dp(12), 0, dp(12), 0)
            minHeight = dp(30)
            gravity = android.view.Gravity.CENTER
            if (shipment) {
                StatusStyleHelper.applyShipment(this, value)
            } else {
                StatusStyleHelper.applyTrade(this, value)
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dp(30)
            ).apply {
                setMargins(dp(8), 0, 0, 0)
            }
        })

        return row
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
