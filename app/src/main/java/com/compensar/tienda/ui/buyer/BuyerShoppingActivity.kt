package com.compensar.tienda.ui.buyer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.home.HomeCategoryActivity
import com.compensar.tienda.ui.home.HomeProductActivity
import com.google.firebase.firestore.FirebaseFirestore

class BuyerShoppingActivity : AppCompatActivity() {

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout
    private lateinit var shoppingContent: LinearLayout

    private val db = FirebaseFirestore.getInstance()

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
        actionAddress.setOnClickListener { startActivity(Intent(this, BuyerAddressActivity::class.java)); finish() }
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

        db.collection("order")
            .whereEqualTo("idUser", userRegister)
            .get()
            .addOnSuccessListener { orders ->
                val data = orders.documents.sortedByDescending { it.getLong("register") ?: 0L }
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
                    val orderRegister = order.getLong("register") ?: 0L
                    db.collection("purchase").whereEqualTo("idOrder", orderRegister).get().addOnSuccessListener { purchases ->
                        val first = purchases.documents.firstOrNull()
                        val productId = first?.getLong("idProduct") ?: 0L
                        if (productId > 0) {
                            db.collection("product").document(productId.toString()).get().addOnSuccessListener { product ->
                                shoppingContent.addView(orderCard(
                                    reference = order.getString("reference") ?: "ORD-$orderRegister",
                                    date = first?.getString("date") ?: "",
                                    name = product.getString("name") ?: "Compra",
                                    image = product.getString("storefire") ?: "",
                                    total = order.getDouble("total") ?: 0.0
                                ))
                            }
                        } else {
                            shoppingContent.addView(orderCard(order.getString("reference") ?: "ORD-$orderRegister", "", "Compra", "", order.getDouble("total") ?: 0.0))
                        }
                    }
                }
            }
            .addOnFailureListener { Toast.makeText(this, "Error cargando compras: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    private fun orderCard(reference: String, date: String, name: String, image: String, total: Double): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(0, dp(34), 0, 0) }
            val img = ImageView(this@BuyerShoppingActivity).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210))
                setBackgroundColor(0xFFE0F2F1.toInt())
                scaleType = ImageView.ScaleType.CENTER_CROP
                if (image.isNotBlank()) Glide.with(this@BuyerShoppingActivity).load(image).placeholder(R.mipmap.ic_launcher).into(this) else setImageResource(R.mipmap.ic_launcher)
            }
            val status = TextView(this@BuyerShoppingActivity).apply { text = "●  PROCESANDO        $date"; textSize = 10f; setTypeface(null, Typeface.BOLD); setTextColor(0xFF111111.toInt()); setPadding(0, dp(12), 0, 0) }
            val info = TextView(this@BuyerShoppingActivity).apply { text = "$name\n$ ${String.format("%,.0f", total)}"; textSize = 16f; setTextColor(0xFF111111.toInt()); setPadding(0, dp(8), 0, 0) }
            val code = TextView(this@BuyerShoppingActivity).apply { text = "#$reference"; textSize = 12f; setTextColor(0xFF747A8C.toInt()); setPadding(0, dp(8), 0, 0) }
            addView(img); addView(status); addView(info); addView(code)
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
