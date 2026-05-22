package com.compensar.tienda.ui.seller

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.CategoryModel
import com.compensar.tienda.model.ProductModel
import com.compensar.tienda.ui.common.SellerDataHelper
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ImagePreviewHelper
import com.google.firebase.firestore.FirebaseFirestore

class SellerProductListActivity : AppCompatActivity() {

    private lateinit var actionReturn: TextView
    private lateinit var fieldSearch: EditText
    private lateinit var actionSearch: ImageView
    private lateinit var actionClean: ImageView
    private lateinit var categoryList: LinearLayout
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private var allProducts: List<ProductModel> = emptyList()
    private var allCategories: List<CategoryModel> = emptyList()
    private var categoryMap: Map<Long, String> = emptyMap()
    private var selectedCategory: Long = 0L
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seller_product_list)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        fieldSearch = findViewById(R.id.fieldSearch)
        actionSearch = findViewById(R.id.actionSearch)
        actionClean = findViewById(R.id.actionClean)
        categoryList = findViewById(R.id.categoryList)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }

        actionNew.setOnClickListener {
            startActivity(Intent(this, SellerProductStoreActivity::class.java))
        }

        actionSearch.setOnClickListener { executeSearch() }
        actionClean.setOnClickListener { cleanSearch() }

        fieldSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executeSearch()
                true
            } else {
                false
            }
        }
    }

    private fun loadData() {
        SellerDataHelper.loadSellerData(
            context = this,
            onSuccess = { data ->
                allProducts = data.products.sortedBy { it.name.orEmpty() }
                loadCategories()
            },
            onFailure = { exception ->
                exception.printStackTrace()
                Toast.makeText(this, "Error al cargar productos: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun loadCategories() {
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                allCategories = result.documents
                    .mapNotNull { it.toObject(CategoryModel::class.java) }
                    .sortedBy { it.name.orEmpty() }

                categoryMap = allCategories.associate { it.register to it.name.orEmpty() }
                renderCategories()
                renderProducts()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                allCategories = emptyList()
                categoryMap = emptyMap()
                renderCategories()
                renderProducts()
            }
    }

    private fun renderCategories() {
        categoryList.removeAllViews()
        categoryList.addView(createCategoryButton("Todos", 0L))

        allCategories.forEach { category ->
            categoryList.addView(createCategoryButton(category.name.orEmpty(), category.register))
        }
    }

    private fun createCategoryButton(label: String, register: Long): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(20), 0, dp(20), 0)
            background = getDrawable(R.drawable.bg_button_light)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(40)
            ).apply {
                setMargins(if (categoryList.childCount == 0) 0 else dp(10), 0, 0, 0)
            }
        }

        val textView = TextView(this).apply {
            text = label.ifBlank { "Sin nombre" }
            textSize = 14f
            setTextColor(getColor(R.color.black))
            if (selectedCategory == register) {
                setTypeface(null, Typeface.BOLD)
            }
        }

        container.addView(textView)
        container.setOnClickListener {
            selectedCategory = register
            renderCategories()
            renderProducts()
        }

        return container
    }

    private fun executeSearch() {
        val query = fieldSearch.text.toString().trim()

        if (query.isBlank()) {
            Toast.makeText(this, "Debes escribir algo para buscar", Toast.LENGTH_SHORT).show()
            return
        }

        searchQuery = query
        actionClean.visibility = android.view.View.VISIBLE
        renderProducts()
    }

    private fun cleanSearch() {
        searchQuery = ""
        fieldSearch.setText("")
        actionClean.visibility = android.view.View.GONE
        renderProducts()
    }

    private fun renderProducts() {
        dataList.removeAllViews()

        val filtered = allProducts.filter { product ->
            val matchCategory = selectedCategory == 0L || product.idCategory == selectedCategory
            val matchSearch = searchQuery.isBlank() || product.name.orEmpty().contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }

        if (filtered.isEmpty()) {
            addEmptyText()
            return
        }

        filtered.forEach { product ->
            dataList.addView(createProductCard(product))
        }
    }

    private fun createProductCard(product: ProductModel): CardView {
        val cardView = CardView(this).apply {
            radius = dp(18).toFloat()
            cardElevation = 0f
            setCardBackgroundColor(getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(14))
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        ImagePreviewHelper.addPreviewToRow(this, row, product.storefire)

        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(12), 0, dp(8), 0)
            }
        }

        addText(textContainer, product.name.orEmpty(), 16f, true, R.color.black)
        addText(textContainer, categoryMap[product.idCategory] ?: "Sin categoría", 14f, false, R.color.subtitle)
        addText(textContainer, SellerDataHelper.formatCurrency(product.price), 14f, true, R.color.black)
        addText(textContainer, "Stock: ${product.stock}", 14f, false, android.R.color.holo_green_dark)

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val edit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(18))
            }
            setOnClickListener {
                val intent = Intent(this@SellerProductListActivity, SellerProductEditActivity::class.java)
                intent.putExtra("register", product.register)
                startActivity(intent)
            }
        }

        val delete = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
            setOnClickListener {
                val intent = Intent(this@SellerProductListActivity, SellerProductQuitActivity::class.java)
                intent.putExtra("register", product.register)
                startActivity(intent)
            }
        }

        actions.addView(edit)
        actions.addView(delete)
        row.addView(textContainer)
        row.addView(actions)
        cardView.addView(row)

        return cardView
    }

    private fun addText(container: LinearLayout, value: String, size: Float, bold: Boolean, color: Int) {
        val textView = TextView(this).apply {
            text = value
            textSize = size
            setTextColor(getColor(color))
            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
            setPadding(0, dp(3), 0, 0)
        }
        container.addView(textView)
    }

    private fun addEmptyText() {
        val textView = TextView(this).apply {
            text = "No hay productos para mostrar"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(getColor(R.color.subtitle))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(20), 0, dp(20))
            }
        }
        dataList.addView(textView)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
