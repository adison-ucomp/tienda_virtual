package com.compensar.tienda.ui.model.product

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import com.compensar.tienda.domain.model.ProductModel

class ProductSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("product")

    private var categoryMap: Map<Long, String> = emptyMap()
    private var shopMap: Map<Long, String> = emptyMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_product_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }

        actionNew.setOnClickListener {
            val intent = Intent(this, ProductCreateActivity::class.java)
            startActivity(intent)
        }

        loadDataBase()
    }

    override fun onResume() {
        super.onResume()
        loadReferenceData { loadItems() }
    }

    private fun loadDataBase() {
        loadReferenceData { loadItems() }
    }

    private fun loadReferenceData(onComplete: () -> Unit) {
        loadCategories {
            loadShops(onComplete)
        }
    }

    private fun loadCategories(onComplete: () -> Unit) {
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                categoryMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    val description = document.getString("name") ?: "Sin Informacion"
                    register to description
                }.toMap()

                onComplete()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                categoryMap = emptyMap()
                onComplete()
            }
    }

    private fun loadShops(onComplete: () -> Unit) {
        db.collection("shop")
            .get()
            .addOnSuccessListener { result ->
                shopMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    val description = document.getString("name") ?: "Sin Informacion"
                    register to description
                }.toMap()

                onComplete()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                shopMap = emptyMap()
                onComplete()
            }
    }


    private fun loadItems() {
        collection
            .get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()

                val items = result.documents.mapNotNull { document ->
                    document.toObject(ProductModel::class.java)
                }.sortedBy { it.register }

                items.forEach { data ->
                    dataList.addView(loadCard(data))
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun loadCard(data: ProductModel): CardView {
        val cardView = CardView(this).apply {
            radius = dp(18).toFloat()
            cardElevation = dp(6).toFloat()
            setCardBackgroundColor(getColor(R.color.white))

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp(4), 0, dp(4), dp(16))
            }
        }

        val mainRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        addText(textContainer, "Registro: ${data.register}")
        addText(textContainer, "Nombre: ${data.name ?: ""}")
        addText(textContainer, "Detalle: ${data.detail ?: ""}")
        addText(textContainer, "Stock: ${data.stock}")
        addText(textContainer, "Precio: ${data.price}")
        addText(textContainer, "URL Imagen: ${data.storefire ?: ""}")
        addText(textContainer, "Categoría: ${label(categoryMap, data.idCategory)}")
        addText(textContainer, "Tienda: ${label(shopMap, data.idShop)}")

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val btnEdit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }

            setOnClickListener {
                val intent = Intent(this@ProductSelectActivity, ProductUpdateActivity::class.java)
                intent.putExtra("register", data.register)
                startActivity(intent)
            }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }

            setOnClickListener {
                val intent = Intent(this@ProductSelectActivity, ProductDeleteActivity::class.java)
                intent.putExtra("register", data.register)
                startActivity(intent)
            }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)

        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)

        cardView.addView(mainRow)

        return cardView
    }

    private fun addText(container: LinearLayout, value: String, color: Int = R.color.black) {
        val textView = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(getColor(color))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }

        container.addView(textView)
    }

    private fun label(map: Map<Long, String>, id: Long): String {
        return map[id] ?: "Sin Informacion"
    }


    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
