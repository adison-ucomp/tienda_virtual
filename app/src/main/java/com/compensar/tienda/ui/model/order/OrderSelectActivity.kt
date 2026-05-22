package com.compensar.tienda.ui.model.order

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreRelationLabelHelper
import com.compensar.tienda.ui.model.common.SelectSearchHelper
import com.google.firebase.firestore.FirebaseFirestore

class OrderSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val collection = FirebaseFirestore.getInstance().collection("order")
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener { startActivity(Intent(this, OrderCreateActivity::class.java)) }

        SelectSearchHelper.bind(
            this,
            onSearch = { query ->
                searchQuery = query
                loadItems()
            },
            onClean = {
                searchQuery = ""
                loadItems()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    private fun loadItems() {
        collection.get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()
                val items = result.documents.mapNotNull { it.toObject(OrderModel::class.java) }.filter { SelectSearchHelper.matches(it, searchQuery) }.sortedBy { it.register }
                if (items.isEmpty()) {
                    dataList.addView(TextView(this).apply {
                        text = "Sin órdenes registradas"
                        gravity = Gravity.CENTER
                        setPadding(0, dp(40), 0, dp(40))
                    })
                }
                items.forEach { dataList.addView(loadCard(it)) }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun loadCard(data: OrderModel): CardView {
        val cardView = CardView(this).apply {
            radius = dp(18).toFloat()
            cardElevation = dp(6).toFloat()
            setCardBackgroundColor(getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
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
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        addText(textContainer, "Registro: ${data.register}")
        addText(textContainer, "Referencia: ${data.reference ?: ""}")
        addText(textContainer, "Dirección: ${data.address ?: ""}")
        addText(textContainer, "Total: ${data.total ?: 0.0}")
        addText(textContainer, "Fecha: ${data.date ?: ""}")
        addText(textContainer, "Hora: ${data.hour ?: ""}")
        val shopLabel = addText(textContainer, "Tienda: Cargando...")
        FirestoreRelationLabelHelper.load(shopLabel, "shop", data.idShop, listOf("name"))

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val btnEdit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { setMargins(0, 0, 0, dp(16)) }
            setOnClickListener { startActivity(Intent(this@OrderSelectActivity, OrderUpdateActivity::class.java).putExtra("register", data.register)) }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { setMargins(0, 0, 0, dp(16)) }
            setOnClickListener { startActivity(Intent(this@OrderSelectActivity, OrderDeleteActivity::class.java).putExtra("register", data.register)) }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)
        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)
        cardView.addView(mainRow)
        return cardView
    }

    private fun addText(container: LinearLayout, value: String): TextView {
        val textView = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }
        container.addView(textView)
        return textView
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
