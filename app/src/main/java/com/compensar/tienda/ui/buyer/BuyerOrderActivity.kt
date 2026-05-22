package com.compensar.tienda.ui.buyer

import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class BuyerOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btnBack: TextView
    private lateinit var txtReference: TextView
    private lateinit var txtInfo: TextView
    private lateinit var txtAddressDetail: TextView
    private lateinit var productsContainer: LinearLayout
    private lateinit var txtGrandTotal: TextView

    private val db = FirebaseFirestore.getInstance()
    private var orderRegister: Long = 0
    private var orderTotal: Double = 0.0
    private var googleMap: GoogleMap? = null
    private var pendingAddress: String = ""

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
        productsContainer = findViewById(R.id.productsContainer)
        txtGrandTotal = findViewById(R.id.txtGrandTotal)

        btnBack.setOnClickListener { finish() }

        initMap()
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
                val addressText = order.address.orEmpty().ifBlank { "Sin dirección" }

                txtReference.text = "Orden #${order.reference ?: order.register}"
                txtInfo.text = "Fecha: ${order.date ?: ""}  Hora: ${order.hour ?: ""}\nDirección: $addressText"
                txtAddressDetail.text = addressText
                txtGrandTotal.text = "Total Compra: $ ${String.format("%,.0f", orderTotal)}"
                loadAddressMap(addressText)
                loadPurchases()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando orden: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun initMap() {
        val fragment = supportFragmentManager.findFragmentById(R.id.orderMapFragment) as? SupportMapFragment
        fragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isScrollGesturesEnabled = false
        map.uiSettings.isZoomGesturesEnabled = false
        map.uiSettings.isTiltGesturesEnabled = false
        map.uiSettings.isRotateGesturesEnabled = false
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(4.7110, -74.0721), 12f))
        if (pendingAddress.isNotBlank()) {
            loadAddressMap(pendingAddress)
        }
    }

    private fun loadAddressMap(address: String) {
        if (address.isBlank() || address == "Sin dirección") return
        pendingAddress = address

        try {
            val geocoder = android.location.Geocoder(this, Locale("es", "CO"))
            @Suppress("DEPRECATION")
            val result = geocoder.getFromLocationName(address, 1)

            if (!result.isNullOrEmpty()) {
                val latLng = LatLng(result[0].latitude, result[0].longitude)
                googleMap?.clear()
                googleMap?.addMarker(MarkerOptions().position(latLng).title(address))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        } catch (_: Exception) {
        }
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
