package com.compensar.tienda.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.ProductModel
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class HomeFilteringActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnCart: TextView
    private lateinit var txtTitle: TextView
    private lateinit var productGrid: GridLayout
    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private var categoryRegister: Long = 0
    private var categoryName: String = "Categoría"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_filtering)
        categoryRegister = intent.getLongExtra("categoryRegister", 0)
        categoryName = intent.getStringExtra("categoryName") ?: "Categoría"
        applyWindowInsets()
        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        initEvents()
        SessionNavigation.bindProfile(this)
        loadProducts()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCart = findViewById(R.id.btnCart)
        txtTitle = findViewById(R.id.txtTitle)
        productGrid = findViewById(R.id.productGrid)
        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)
        txtTitle.text = categoryName
    }

    private fun initEvents() {
        btnBack.setOnClickListener { finish() }
        btnCart.setOnClickListener { SessionNavigation.openCartOrLogin(this) }
        actionHome.setOnClickListener { startActivity(Intent(this, HomeProductActivity::class.java)); finish() }
        actionCategory.setOnClickListener { startActivity(Intent(this, HomeCategoryActivity::class.java)); finish() }
        actionShopping.setOnClickListener { startActivity(Intent(this, BuyerShoppingActivity::class.java)) }
        actionAddress.setOnClickListener { startActivity(Intent(this, BuyerAddressActivity::class.java)) }
        actionAccount.setOnClickListener { SessionNavigation.openProfileOrLogin(this) }
    }

    private fun loadProducts() {
        if (categoryRegister <= 0) return
        FirebaseFirestore.getInstance()
            .collection("product")
            .whereEqualTo("idCategory", categoryRegister)
            .get()
            .addOnSuccessListener { result ->
                productGrid.removeAllViews()
                val products = result.documents.mapNotNull { it.toObject(ProductModel::class.java) }.sortedBy { it.register }
                if (products.isEmpty()) {
                    productGrid.addView(TextView(this).apply {
                        text = "No hay productos para esta categoría"
                        gravity = Gravity.CENTER
                        textSize = 15f
                        setPadding(0, dp(30), 0, dp(30))
                    })
                    return@addOnSuccessListener
                }
                products.forEach { productGrid.addView(productCard(it)) }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando productos: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun productCard(product: ProductModel): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = GridLayout.LayoutParams().apply {
                width = (resources.displayMetrics.widthPixels - dp(72)) / 2
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                setMargins(dp(6), 0, dp(6), dp(28))
            }
        }
        val img = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(170))
            setBackgroundColor(0xFFF7F7F7.toInt())
            setPadding(dp(18), dp(18), dp(18), dp(18))
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        if (!product.storefire.isNullOrBlank()) Glide.with(this).load(product.storefire).placeholder(R.mipmap.ic_launcher).into(img) else img.setImageResource(R.mipmap.ic_launcher)
        val name = TextView(this).apply {
            text = product.name ?: "Producto"
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
            setPadding(0, dp(14), 0, 0)
            maxLines = 2
        }
        val price = TextView(this).apply {
            text = "$ ${String.format("%,.0f", product.price)}"
            textSize = 15f
            setTextColor(0xFF777777.toInt())
            setPadding(0, dp(6), 0, 0)
        }
        val openDetail = { startActivity(Intent(this, HomeDetailActivity::class.java).putExtra("productRegister", product.register)) }
        img.setOnClickListener { openDetail() }
        name.setOnClickListener { openDetail() }
        card.addView(img)
        card.addView(name)
        card.addView(price)
        return card
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
