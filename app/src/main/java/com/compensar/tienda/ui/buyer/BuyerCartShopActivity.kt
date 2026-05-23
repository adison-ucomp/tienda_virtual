package com.compensar.tienda.ui.buyer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
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
import com.compensar.tienda.ui.common.CartItem
import com.compensar.tienda.ui.common.CartManager
import com.compensar.tienda.ui.common.ReserveManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * Clase [BuyerCartShopActivity].
 *
 * Responsable de la logica asociada al pantalla del flujo de comprador.
 */
class BuyerCartShopActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView
    private lateinit var cartList: LinearLayout
    private lateinit var txtSubtotal: TextView
    private lateinit var txtShipping: TextView
    private lateinit var txtTaxes: TextView
    private lateinit var txtTotal: TextView
    private lateinit var btnContinue: Button
    private val listeners = mutableListOf<ListenerRegistration>()

    /**
     * Se ejecuta al crear la pantalla.
     * Inicializa vista, estado y eventos principales.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_cart_shop)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, i ->
            val b = i.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(b.left, b.top, b.right, b.bottom)
            i
        }
        initViews()
        initEvents()
        render()
    }

    /**
     * Se ejecuta al destruir la pantalla y liberar recursos.
     */
    override fun onDestroy() {
        listeners.forEach { it.remove() }
        listeners.clear()
        super.onDestroy()
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        cartList = findViewById(R.id.cartList)
        txtSubtotal = findViewById(R.id.txtSubtotal)
        txtShipping = findViewById(R.id.txtShipping)
        txtTaxes = findViewById(R.id.txtTaxes)
        txtTotal = findViewById(R.id.txtTotal)
        btnContinue = findViewById(R.id.btnContinue)
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initEvents() {
        btnBack.setOnClickListener { finish() }
        btnContinue.setOnClickListener {
            if (CartManager.getItems(this).isEmpty()) {
                Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, BuyerPurchaseActivity::class.java))
            }
        }
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun render() {
        listeners.forEach { it.remove() }
        listeners.clear()
        cartList.removeAllViews()
        val items = CartManager.getItems(this)
        if (items.isEmpty()) {
            cartList.addView(TextView(this).apply {
                text = "Tu carrito está vacío"
                gravity = Gravity.CENTER
                textSize = 15f
                setPadding(0, dp(30), 0, dp(30))
            })
        }
        items.forEach { cartList.addView(card(it)) }
        renderTotals()
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun renderTotals() {
        val subtotal = CartManager.subtotal(this)
        txtSubtotal.text = "Subtotal: $ ${String.format("%,.0f", subtotal)}"
        txtShipping.text = "Envío: $ 0"
        txtTaxes.text = "Impuestos: $ 0"
        txtTotal.text = "Total: $ ${String.format("%,.0f", subtotal)}"
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun card(item: CartItem): CardView {
        val card = CardView(this).apply {
            radius = dp(16).toFloat()
            cardElevation = dp(4).toFloat()
            setCardBackgroundColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, dp(14))
            }
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(12), dp(12), dp(12), dp(12)); gravity = Gravity.CENTER_VERTICAL }
        val img = ImageView(this).apply { layoutParams = LinearLayout.LayoutParams(dp(86), dp(86)); scaleType = ImageView.ScaleType.CENTER_CROP; setBackgroundColor(0xFFF7F8FA.toInt()) }
        if (item.storefire.isNotBlank()) Glide.with(this).load(item.storefire).placeholder(R.drawable.ic_shops).into(img) else img.setImageResource(R.drawable.ic_shops)
        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(dp(12), 0, 0, 0) } }
        info.addView(TextView(this).apply { text = item.name; textSize = 15f; setTypeface(null, Typeface.BOLD); setTextColor(0xFF111111.toInt()) })
        info.addView(TextView(this).apply { text = item.detail; textSize = 12f; setTextColor(0xFF4B5060.toInt()); maxLines = 2 })
        info.addView(TextView(this).apply { text = "Unitario: $ ${String.format("%,.0f", item.price)}"; textSize = 13f; setTextColor(0xFF111111.toInt()) })
        val stockLabel = TextView(this).apply { text = "Consultando stock..."; textSize = 12f; setTextColor(0xFF1E66F5.toInt()) }
        info.addView(stockLabel)
        listenStock(item, stockLabel)

        val qty = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(8), 0, 0) }
        val minus = Button(this).apply {
            text = "-"; layoutParams = LinearLayout.LayoutParams(dp(42), dp(40))
            setOnClickListener { updateQuantity(item, item.quantity - 1) }
        }
        val number = TextView(this).apply { text = item.quantity.toString(); gravity = Gravity.CENTER; textSize = 16f; layoutParams = LinearLayout.LayoutParams(dp(44), dp(40)) }
        val plus = Button(this).apply {
            text = "+"; layoutParams = LinearLayout.LayoutParams(dp(42), dp(40))
            setOnClickListener { updateQuantity(item, item.quantity + 1) }
        }
        qty.addView(minus); qty.addView(number); qty.addView(plus); info.addView(qty)
        row.addView(img); row.addView(info); card.addView(row); return card
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun listenStock(item: CartItem, target: TextView) {
        val listener = FirebaseFirestore.getInstance().collection("product").document(item.register.toString())
            .addSnapshotListener { snapshot, _ ->
                val stock = (snapshot?.getLong("stock") ?: 0L).toInt()
                val reserved = (snapshot?.getLong("reserved") ?: 0L).toInt()
                val available = (stock - reserved).coerceAtLeast(0)
                target.text = "Quedan disponibles: $available"
            }
        listeners.add(listener)
    }

    /**
     * Actualiza informacion existente segun el flujo actual.
     */
    private fun updateQuantity(item: CartItem, quantity: Int) {
        ReserveManager.reserveQuantity(this, item, quantity, onSuccess = {
            render()
        }, onError = { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}

