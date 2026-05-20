package com.compensar.tienda.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.CategoryModel
import com.compensar.tienda.domain.model.ProductModel
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerCartShopActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class HomeProductActivity : AppCompatActivity() {

    private lateinit var btnCart: TextView
    private lateinit var txtViewAll: TextView
    private lateinit var productGrid: GridLayout

    private lateinit var categoryOne: LinearLayout
    private lateinit var categoryTwo: LinearLayout
    private lateinit var txtCategoryOne: TextView
    private lateinit var txtCategoryTwo: TextView

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private val products = mutableListOf<ProductModel>()
    private val categories = mutableListOf<CategoryModel>()
    private var page = 0
    private val pageSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_product)

        applyWindowInsets()
        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        SessionNavigation.bindProfile(this)
        initEvents()
        loadCategories()
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
        btnCart = findViewById(R.id.btnCart)
        txtViewAll = findViewById(R.id.txtViewAll)
        productGrid = findViewById(R.id.productGrid)

        categoryOne = findViewById(R.id.categoryOne)
        categoryTwo = findViewById(R.id.categoryTwo)
        txtCategoryOne = findViewById(R.id.txtCategoryOne)
        txtCategoryTwo = findViewById(R.id.txtCategoryTwo)

        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)
    }

    private fun initEvents() {
        btnCart.setOnClickListener {
            startActivity(Intent(this, BuyerCartShopActivity::class.java))
        }

        txtViewAll.setOnClickListener {
            startActivity(Intent(this, HomeCategoryActivity::class.java))
        }

        actionHome.setOnClickListener { }
        actionCategory.setOnClickListener {
            startActivity(Intent(this, HomeCategoryActivity::class.java))
        }
        actionShopping.setOnClickListener {
            startActivity(Intent(this, BuyerShoppingActivity::class.java))
        }
        actionAddress.setOnClickListener {
            startActivity(Intent(this, BuyerAddressActivity::class.java))
        }
        actionAccount.setOnClickListener {
            startActivity(Intent(this, HomeLoginActivity::class.java))
        }
    }

    private fun loadCategories() {
        FirebaseFirestore.getInstance()
            .collection("category")
            .get()
            .addOnSuccessListener { result ->
                categories.clear()
                categories.addAll(
                    result.documents
                        .mapNotNull { it.toObject(CategoryModel::class.java) }
                        .sortedBy { it.register }
                )
                renderHomeCategories()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando categorías: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun renderHomeCategories() {
        categoryOne.visibility = View.GONE
        categoryTwo.visibility = View.GONE

        categories.take(2).forEachIndexed { index, category ->
            val view = if (index == 0) categoryOne else categoryTwo
            val text = if (index == 0) txtCategoryOne else txtCategoryTwo

            text.text = (category.name ?: "Categoría").uppercase()
            view.visibility = View.VISIBLE
            view.setOnClickListener {
                openCategory(category)
            }
        }
    }

    private fun openCategory(category: CategoryModel) {
        startActivity(
            Intent(this, HomeCategoryFilterActivity::class.java)
                .putExtra("categoryRegister", category.register)
                .putExtra("categoryName", category.name ?: "Categoría")
        )
    }

    private fun loadProducts() {
        FirebaseFirestore.getInstance()
            .collection("product")
            .get()
            .addOnSuccessListener { result ->
                products.clear()
                products.addAll(
                    result.documents
                        .mapNotNull { it.toObject(ProductModel::class.java) }
                        .sortedBy { it.register }
                )
                page = 0
                renderProducts()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando productos: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun renderProducts() {
        productGrid.removeAllViews()

        if (products.isEmpty()) {
            productGrid.addView(
                TextView(this).apply {
                    text = "No hay productos disponibles"
                    textSize = 15f
                    gravity = Gravity.CENTER
                    setTextColor(0xFF747A8C.toInt())
                    setPadding(0, dp(24), 0, dp(24))
                }
            )
            txtViewAll.text = "VER TODO"
            return
        }

        val start = page * pageSize
        val end = minOf(start + pageSize, products.size)
        products.subList(start, end).forEach { product ->
            productGrid.addView(productCard(product))
        }

        txtViewAll.text = if (end < products.size) "VER MÁS" else "VER TODO"
        txtViewAll.setOnClickListener {
            if ((page + 1) * pageSize < products.size) {
                page++
                renderProducts()
            } else {
                startActivity(Intent(this, HomeCategoryActivity::class.java))
            }
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

        val image = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(170))
            setBackgroundColor(0xFFF7F7F7.toInt())
            contentDescription = "Imagen del producto"
            setPadding(dp(18), dp(18), dp(18), dp(18))
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        if (!product.storefire.isNullOrBlank()) {
            Glide.with(this).load(product.storefire).placeholder(R.mipmap.ic_launcher).into(image)
        } else {
            image.setImageResource(R.mipmap.ic_launcher)
        }

        val name = TextView(this).apply {
            text = product.name ?: "Producto"
            setTextColor(0xFF111111.toInt())
            textSize = 15f
            setTypeface(null, Typeface.BOLD)
            maxLines = 2
            setPadding(0, dp(14), 0, 0)
        }

        val price = TextView(this).apply {
            text = "$ ${String.format("%,.0f", product.price)}"
            setTextColor(0xFF777777.toInt())
            textSize = 15f
            setPadding(0, dp(6), 0, 0)
        }

        val openDetail = {
            startActivity(
                Intent(this, HomeDetailActivity::class.java)
                    .putExtra("productRegister", product.register)
            )
        }

        image.setOnClickListener { openDetail() }
        name.setOnClickListener { openDetail() }

        card.addView(image)
        card.addView(name)
        card.addView(price)
        return card
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
