package com.compensar.tienda.ui.buyer

import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLEncoder

class BuyerOrderActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var txtReference: TextView
    private lateinit var txtInfo: TextView
    private lateinit var txtAddressDetail: TextView
    private lateinit var webAddressMap: WebView
    private lateinit var productsContainer: LinearLayout
    private lateinit var txtGrandTotal: TextView

    private val db = FirebaseFirestore.getInstance()
    private var orderRegister: Long = 0
    private var orderTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_order)
        SessionNavigation.bindProfile(this)
        SessionNavigation.applyBuyerInferiorVisibility(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        orderRegister = intent.getLongExtra("orderRegister", 0)

        btnBack = findViewById(R.id.btnBack)
        txtReference = findViewById(R.id.txtReference)
        txtInfo = findViewById(R.id.txtInfo)
        txtAddressDetail = findViewById(R.id.txtAddressDetail)
        webAddressMap = findViewById(R.id.webAddressMap)
        productsContainer = findViewById(R.id.productsContainer)
        txtGrandTotal = findViewById(R.id.txtGrandTotal)

        btnBack.setOnClickListener { finish() }

        loadOrder()
    }

    private fun loadOrder() {
        if (orderRegister <= 0) {
            Toast.makeText(this, "Orden no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("order").document(orderRegister.toString()).get()
            .addOnSuccessListener { doc ->
                val order = doc.toObject(OrderModel::class.java)
                if (order == null) {
                    Toast.makeText(this, "No se encontró la orden", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                orderTotal = order.total ?: 0.0
                txtReference.text = "Orden #${order.reference ?: order.register}"
                txtInfo.text = "Fecha: ${order.date ?: ""}  Hora: ${order.hour ?: ""}\nDirección: ${order.address ?: ""}"
                txtGrandTotal.text = "Total Compra: $ ${String.format("%,.0f", orderTotal)}"
                loadPurchases()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando orden: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadAddressMap(address: String) {
        if (address.isBlank() || address == "Sin dirección") return
        val encoded = URLEncoder.encode(address, "UTF-8")
        webAddressMap.settings.javaScriptEnabled = true
        webAddressMap.settings.domStorageEnabled = true
        webAddressMap.loadUrl("https://www.google.com/maps/search/?api=1&query=$encoded")
    }

    private fun loadPurchases() {
        productsContainer.removeAllViews()

        db.collection("purchase")
            .whereEqualTo("idOrder", orderRegister)
            .get()
            .addOnSuccessListener { result ->
                val purchases = result.documents.mapNotNull { it.toObject(PurchaseModel::class.java) }
                    .sortedBy { it.register }

                if (purchases.isEmpty()) {
                    productsContainer.addView(TextView(this).apply {
                        text = "No hay productos asociados a esta orden"
                        gravity = Gravity.CENTER
                        setPadding(0, dp(30), 0, dp(30))
                    })
                    return@addOnSuccessListener
                }

                purchases.forEach { purchase ->
                    loadProductCard(purchase)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando productos: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadProductCard(purchase: PurchaseModel) {
        db.collection("product").document(purchase.idProduct.toString()).get()
            .addOnSuccessListener { product ->
                val name = product.getString("name") ?: "Producto"
                val image = product.getString("storefire") ?: ""
                val amount = purchase.amount ?: 0
                val value = purchase.value ?: 0.0
                val total = purchase.total ?: 0.0

                productsContainer.addView(productCard(name, image, amount, value, total))
            }
    }

    private fun productCard(name: String, image: String, amount: Int, value: Double, total: Double): CardView {
        val card = CardView(this).apply {
            radius = dp(16).toFloat()
            cardElevation = dp(4).toFloat()
            setCardBackgroundColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, dp(14)) }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        val img = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(82), dp(82))
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(0xFFF7F8FA.toInt())
        }

        if (image.isNotBlank()) {
            Glide.with(this).load(image).placeholder(R.drawable.ic_shops).into(img)
        } else {
            img.setImageResource(R.drawable.ic_shops)
        }

        val info = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(12), 0, 0, 0)
            }
        }

        info.addView(TextView(this).apply {
            text = name
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
        })

        info.addView(TextView(this).apply {
            text = "Cantidad: $amount"
            textSize = 13f
            setTextColor(0xFF4B5060.toInt())
            setPadding(0, dp(5), 0, 0)
        })

        info.addView(TextView(this).apply {
            text = "Unitario: $ ${String.format("%,.0f", value)}"
            textSize = 13f
            setTextColor(0xFF4B5060.toInt())
            setPadding(0, dp(5), 0, 0)
        })

        info.addView(TextView(this).apply {
            text = "Total: $ ${String.format("%,.0f", total)}"
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
            setPadding(0, dp(5), 0, 0)
        })

        row.addView(img)
        row.addView(info)
        card.addView(row)
        return card
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
