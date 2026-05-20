package com.compensar.tienda.ui.model.purchase

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
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class PurchaseSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val collection = FirebaseFirestore.getInstance().collection("purchase")
    private val db = FirebaseFirestore.getInstance()

    private var gatewayMap: Map<Long, String> = emptyMap()
    private var orderMap: Map<Long, String> = emptyMap()
    private var productMap: Map<Long, String> = emptyMap()
    private var userMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_purchase_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener { startActivity(Intent(this, PurchaseCreateActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        loadReferenceData { loadItems() }
    }

    private fun loadReferenceData(onComplete: () -> Unit) {
        loadCollection("gateway", listOf("name")) { gateways ->
            gatewayMap = gateways
            loadCollection("order", listOf("reference")) { orders ->
                orderMap = orders
                loadCollection("product", listOf("name")) { products ->
                    productMap = products
                    loadCollection("user", listOf("names", "srnms", "email")) { users ->
                        userMap = users
                        onComplete()
                    }
                }
            }
        }
    }

    private fun loadCollection(collectionName: String, fields: List<String>, onComplete: (Map<Long, String>) -> Unit) {
        db.collection(collectionName).get()
            .addOnSuccessListener { result ->
                val data = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    val label = fields.mapNotNull { document.get(it)?.toString()?.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(" ")
                    register to label.ifEmpty { "Registro: $register" }
                }.toMap()
                onComplete(data)
            }
            .addOnFailureListener { onComplete(emptyMap()) }
    }

    private fun loadItems() {
        collection.get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()
                val items = result.documents.mapNotNull { it.toObject(PurchaseModel::class.java) }.sortedBy { it.register }
                if (items.isEmpty()) {
                    dataList.addView(TextView(this).apply {
                        text = "Sin compras registradas"
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

    private fun loadCard(data: PurchaseModel): CardView {
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
        addText(textContainer, "Cantidad: ${data.amount ?: 0}")
        addText(textContainer, "Valor: ${data.value ?: 0.0}")
        addText(textContainer, "Total: ${data.total ?: 0.0}")
        addText(textContainer, "Pasarela: ${label(gatewayMap, data.idGangway)}")
        addText(textContainer, "Referencia: ${label(orderMap, data.idOrder)}")
        addText(textContainer, "Producto: ${label(productMap, data.idProduct)}")
        addText(textContainer, "Usuario: ${label(userMap, data.idUser)}")

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val btnEdit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { setMargins(0, 0, 0, dp(16)) }
            setOnClickListener {
                startActivity(Intent(this@PurchaseSelectActivity, PurchaseUpdateActivity::class.java).putExtra("register", data.register))
            }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { setMargins(0, 0, 0, dp(16)) }
            setOnClickListener {
                startActivity(Intent(this@PurchaseSelectActivity, PurchaseDeleteActivity::class.java).putExtra("register", data.register))
            }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)
        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)
        cardView.addView(mainRow)
        return cardView
    }

    private fun addText(container: LinearLayout, value: String) {
        container.addView(TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        })
    }

    private fun label(map: Map<Long, String>, id: Long): String = map[id] ?: "Sin Información"

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
