package com.compensar.tienda.ui.model.trade

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
import com.compensar.tienda.model.TradeModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.FirestoreRelationLabelHelper
import com.compensar.tienda.ui.model.common.SelectSearchHelper
import com.google.firebase.firestore.FirebaseFirestore

class TradeSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val collection = FirebaseFirestore.getInstance().collection("trade")
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_trade_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener {
            startActivity(Intent(this, TradeCreateActivity::class.java))
        }

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
                val items = result.documents
                    .mapNotNull { it.toObject(TradeModel::class.java) }
                    .filter { SelectSearchHelper.matches(it, searchQuery) }
                    .sortedBy { it.register }

                if (items.isEmpty()) {
                    dataList.addView(TextView(this).apply {
                        text = "Sin transacciones registradas"
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

    private fun loadCard(data: TradeModel): CardView {
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
        addText(textContainer, "Servicio: ${data.api ?: ""}")
        addText(textContainer, "Estado: ${data.state ?: ""}")
        addText(textContainer, "Referencia ePayco: ${data.reference ?: ""}")
        val orderLabel = addText(textContainer, "Orden: Cargando...")
        val gatewayLabel = addText(textContainer, "Pasarela: Cargando...")
        FirestoreRelationLabelHelper.load(gatewayLabel, "gateway", data.idGateway, listOf("name"))
        FirestoreRelationLabelHelper.load(orderLabel, "order", data.idOrder, listOf("reference"))

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
                startActivity(
                    Intent(this@TradeSelectActivity, TradeUpdateActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
            setOnClickListener {
                startActivity(
                    Intent(this@TradeSelectActivity, TradeDeleteActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)
        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)
        cardView.addView(mainRow)
        return cardView
    }

    private fun addText(container: LinearLayout, value: String): TextView {
        val text = TextView(this).apply {
            this.text = value
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }
        container.addView(text)
        return text
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
