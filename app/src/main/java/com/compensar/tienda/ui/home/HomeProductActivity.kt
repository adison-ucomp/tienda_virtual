package com.compensar.tienda.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.model.CategoryModel
import com.compensar.tienda.model.ProductModel
import com.compensar.tienda.ui.buyer.BuyerAddressActivity
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [HomeProductActivity].
 *
 * Responsable de la logica asociada al pantalla o helper del flujo de inicio/autenticacion/compra.
 */
class HomeProductActivity : AppCompatActivity() {

    private lateinit var btnCart: TextView
    private lateinit var txtViewAll: TextView
    private lateinit var productGrid: GridLayout
    private lateinit var btnPreviousProducts: TextView
    private lateinit var btnNextProducts: TextView
    private lateinit var fieldSearch: EditText
    private lateinit var actionSearch: ImageView
    private lateinit var actionClean: ImageView
    private lateinit var homeCategoryContainer: LinearLayout

    private lateinit var actionHome: LinearLayout
    private lateinit var actionCategory: LinearLayout
    private lateinit var actionShopping: LinearLayout
    private lateinit var actionAddress: LinearLayout
    private lateinit var actionAccount: LinearLayout

    private val products = mutableListOf<ProductModel>()
    private val filteredProducts = mutableListOf<ProductModel>()
    private val categories = mutableListOf<CategoryModel>()
    private var page = 0
    private val pageSize = 10

    /**
     * Se ejecuta al crear la pantalla.
     * Inicializa vista, estado y eventos principales.
     */
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

    /**
     * Se ejecuta cuando la pantalla vuelve al primer plano.
     * Recarga datos o refresca el estado visual.
     */
    override fun onResume() {
        super.onResume()
        SessionNavigation.applyBuyerInferiorVisibility(this)
        SessionNavigation.applyBuyerSuperiorVisibility(this)
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initViews() {
        btnCart = findViewById(R.id.btnCart)
        txtViewAll = findViewById(R.id.txtViewAll)
        productGrid = findViewById(R.id.productGrid)
        btnPreviousProducts = findViewById(R.id.btnPreviousProducts)
        btnNextProducts = findViewById(R.id.btnNextProducts)
        fieldSearch = findViewById(R.id.fieldSearch)
        actionSearch = findViewById(R.id.actionSearch)
        actionClean = findViewById(R.id.actionClean)
        homeCategoryContainer = findViewById(R.id.homeCategoryContainer)

        actionSearch.alpha = 0.45f

        actionHome = findViewById(R.id.actionHome)
        actionCategory = findViewById(R.id.actionCategory)
        actionShopping = findViewById(R.id.actionShopping)
        actionAddress = findViewById(R.id.actionAddress)
        actionAccount = findViewById(R.id.actionAccount)
    }

    /**
     * Inicializa componentes internos de la clase.
     */
    private fun initEvents() {
        btnCart.setOnClickListener {
            SessionNavigation.openCartOrLogin(this)
        }

        txtViewAll.setOnClickListener {
            startActivity(Intent(this, HomeCategoryActivity::class.java))
        }

        actionSearch.setOnClickListener {
            searchProducts()
        }

        actionClean.setOnClickListener {
            clearSearch()
        }

        fieldSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProducts()
                true
            } else {
                false
            }
        }

        fieldSearch.addTextChangedListener(object : TextWatcher {
            /**
             * Ejecuta una parte del flujo funcional de esta clase.
             */
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            /**
             * Ejecuta una parte del flujo funcional de esta clase.
             */
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !text.isNullOrBlank()
                actionSearch.alpha = if (hasText) 1f else 0.45f
                actionClean.visibility = if (hasText) View.VISIBLE else View.GONE

                if (!hasText && filteredProducts.size != products.size) {
                    filteredProducts.clear()
                    filteredProducts.addAll(products)
                    page = 0
                    renderProducts()
                }
            }

            /**
             * Ejecuta una parte del flujo funcional de esta clase.
             */
            override fun afterTextChanged(editable: Editable?) {}
        })

        btnPreviousProducts.setOnClickListener {
            if (page > 0) {
                page--
                renderProducts()
            }
        }

        btnNextProducts.setOnClickListener {
            if ((page + 1) * pageSize < filteredProducts.size) {
                page++
                renderProducts()
            }
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
            SessionNavigation.openProfileOrLogin(this)
        }
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
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

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun renderHomeCategories() {
        homeCategoryContainer.removeAllViews()

        if (categories.isEmpty()) {
            centerCategoryContainer(true)
            return
        }

        val totalWidth = (categories.size * dp(110)) + ((categories.size - 1).coerceAtLeast(0) * dp(20))
        val availableWidth = resources.displayMetrics.widthPixels - dp(48)
        val shouldCenter = totalWidth <= availableWidth

        centerCategoryContainer(shouldCenter)

        categories.forEachIndexed { index, category ->
            homeCategoryContainer.addView(categoryCard(category, index))
        }
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun centerCategoryContainer(shouldCenter: Boolean) {
        homeCategoryContainer.layoutParams = homeCategoryContainer.layoutParams.apply {
            width = if (shouldCenter) {
                ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        homeCategoryContainer.gravity = if (shouldCenter) {
            Gravity.CENTER
        } else {
            Gravity.CENTER_VERTICAL
        }
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun categoryCard(category: CategoryModel, index: Int): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(110), ViewGroup.LayoutParams.MATCH_PARENT).apply {
                if (index > 0) {
                    marginStart = dp(20)
                }
            }
            setOnClickListener {
                openCategory(category)
            }
        }

        val image = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(58), dp(58))
            setBackgroundColor(0xFFFFFFFF.toInt())
            contentDescription = "Imagen de categoría"
            setPadding(dp(8), dp(8), dp(8), dp(8))
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        if (!category.storefire.isNullOrBlank()) {
            Glide.with(this)
                .load(category.storefire)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(image)
        } else {
            image.setImageResource(R.mipmap.ic_launcher)
        }

        val name = TextView(this).apply {
            text = (category.name ?: "Categoría").uppercase()
            setTextColor(0xFF111111.toInt())
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            maxLines = 1
            setPadding(0, dp(10), 0, 0)
        }

        card.addView(image)
        card.addView(name)
        return card
    }

    /**
     * Abre recurso o pantalla asociada al flujo actual.
     */
    private fun openCategory(category: CategoryModel) {
        startActivity(
            Intent(this, HomeFilteringActivity::class.java)
                .putExtra("categoryRegister", category.register)
                .putExtra("categoryName", category.name ?: "Categoría")
        )
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
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
                filteredProducts.clear()
                filteredProducts.addAll(products)
                page = 0
                renderProducts()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando productos: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun searchProducts() {
        val query = fieldSearch.text.toString().trim()

        if (query.isBlank()) {
            Toast.makeText(this, "Debe escribir el nombre del producto para buscar", Toast.LENGTH_SHORT).show()
            return
        }

        filteredProducts.clear()
        filteredProducts.addAll(
            products.filter { product ->
                product.name.orEmpty().contains(query, ignoreCase = true)
            }
        )

        page = 0
        actionClean.visibility = View.VISIBLE
        renderProducts()
    }

    /**
     * Limpia estado temporal o datos persistidos.
     */
    private fun clearSearch() {
        fieldSearch.setText("")
        filteredProducts.clear()
        filteredProducts.addAll(products)
        page = 0
        actionClean.visibility = View.GONE
        actionSearch.alpha = 0.45f
        renderProducts()
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun renderProducts() {
        productGrid.removeAllViews()

        if (filteredProducts.isEmpty()) {
            productGrid.rowCount = 1
            productGrid.addView(
                TextView(this).apply {
                    text = if (fieldSearch.text.toString().trim().isBlank()) {
                        "No hay productos disponibles"
                    } else {
                        "No se encontraron productos"
                    }
                    textSize = 15f
                    gravity = Gravity.CENTER
                    setTextColor(0xFF747A8C.toInt())
                    setPadding(0, dp(24), 0, dp(24))
                }
            )
            updatePaginationButtons()
            return
        }

        val start = page * pageSize
        val end = minOf(start + pageSize, filteredProducts.size)
        val visibleProducts = filteredProducts.subList(start, end)

        productGrid.rowCount = ((visibleProducts.size + 1) / 2).coerceAtLeast(1)
        visibleProducts.forEach { product ->
            productGrid.addView(productCard(product))
        }

        updatePaginationButtons()
    }

    /**
     * Actualiza informacion existente segun el flujo actual.
     */
    private fun updatePaginationButtons() {
        val hasMoreThanOnePage = filteredProducts.size > pageSize
        val hasPrevious = page > 0
        val hasNext = (page + 1) * pageSize < filteredProducts.size

        btnPreviousProducts.visibility = if (hasMoreThanOnePage) View.VISIBLE else View.GONE
        btnNextProducts.visibility = if (hasMoreThanOnePage) View.VISIBLE else View.GONE

        btnPreviousProducts.isEnabled = hasPrevious
        btnNextProducts.isEnabled = hasNext
        btnPreviousProducts.alpha = if (hasPrevious) 1f else 0.35f
        btnNextProducts.alpha = if (hasNext) 1f else 0.35f

        txtViewAll.text = "VER TODO"
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
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
            setPadding(dp(6), dp(6), dp(6), dp(6))
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        if (!product.storefire.isNullOrBlank()) {
            Glide.with(this)
                .load(product.storefire)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(image)
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

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}

