package com.compensar.tienda.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.model.CategoryModel
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class HomeCategoryActivity : AppCompatActivity() {

    private lateinit var btnCart: TextView
    private lateinit var categoryContainer: LinearLayout
    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_category)

        applyWindowInsets()
        initViews()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        SessionNavigation.bindProfile(this)
        initEvents()
        loadCategories()
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
        categoryContainer = findViewById(R.id.categoryContainer)
        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)
    }

    private fun initEvents() {
        btnCart.setOnClickListener { SessionNavigation.openCartOrLogin(this) }
        actionHome.setOnClickListener { startActivity(Intent(this, HomeProductActivity::class.java)); finish() }
        actionCategory.setOnClickListener { }
        actionShopping.setOnClickListener { startActivity(Intent(this, BuyerShoppingActivity::class.java)) }
        actionAddress.setOnClickListener { startActivity(Intent(this, BuyerAddressActivity::class.java)) }
        actionAccount.setOnClickListener { SessionNavigation.openProfileOrLogin(this) }
    }

    private fun loadCategories() {
        FirebaseFirestore.getInstance()
            .collection("category")
            .get()
            .addOnSuccessListener { result ->
                val categories = result.documents.mapNotNull { it.toObject(CategoryModel::class.java) }.sortedBy { it.register }
                renderCategories(categories)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando categorías: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun renderCategories(categories: List<CategoryModel>) {
        categoryContainer.removeAllViews()

        if (categories.isEmpty()) {
            categoryContainer.addView(TextView(this).apply {
                text = "No hay categorías registradas"
                textSize = 14f
                setTextColor(0xFF747A8C.toInt())
                gravity = Gravity.CENTER
                setPadding(0, dp(24), 0, dp(24))
            })
            return
        }

        categories.forEach { category ->
            categoryContainer.addView(categoryCard(category))
        }
    }

    private fun categoryCard(category: CategoryModel): LinearLayout {
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(96)
            ).apply { setMargins(0, 0, 0, dp(20)) }
            setBackgroundColor(0xFFF7F8FA.toInt())
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(24), 0, dp(24), 0)

            val icon = TextView(this@HomeCategoryActivity).apply {
                layoutParams = LinearLayout.LayoutParams(dp(58), dp(58))
                setBackgroundColor(0xFFFFFFFF.toInt())
                gravity = Gravity.CENTER
                text = "▣"
                setTextColor(0xFF151A1D.toInt())
                textSize = 24f
            }

            val texts = LinearLayout(this@HomeCategoryActivity).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(dp(20), 0, 0, 0)
                }
                orientation = LinearLayout.VERTICAL
            }

            val title = TextView(this@HomeCategoryActivity).apply {
                text = category.name ?: "Categoría"
                setTextColor(0xFF151A1D.toInt())
                textSize = 15f
                setTypeface(null, Typeface.BOLD)
            }

            val detail = TextView(this@HomeCategoryActivity).apply {
                text = "Ver productos relacionados"
                setTextColor(0xFF747A8C.toInt())
                textSize = 12f
                setPadding(0, dp(4), 0, 0)
            }

            texts.addView(title)
            texts.addView(detail)
            addView(icon)
            addView(texts)

            setOnClickListener {
                startActivity(
                    Intent(this@HomeCategoryActivity, HomeCategoryFilterActivity::class.java)
                        .putExtra("categoryRegister", category.register)
                        .putExtra("categoryName", category.name ?: "Categoría")
                )
            }
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
