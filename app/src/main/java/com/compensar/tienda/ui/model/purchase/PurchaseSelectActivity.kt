package com.compensar.tienda.ui.model.purchase

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
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class PurchaseSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val collection = FirebaseFirestore.getInstance().collection("purchase")
    private val db = FirebaseFirestore.getInstance()

    private var productMap: Map<Long, String> = emptyMap()
    private var paymentMap: Map<Long, String> = emptyMap()
    private var gatewayMap: Map<Long, String> = emptyMap()
    private var userMap: Map<Long, String> = emptyMap()
    private var orderMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_purchase_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener { startActivity(Intent(this, PurchaseCreateActivity::class.java)) }

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
        loadProducts {
            loadPayments {
                loadGateways {
                    loadUsers {
                        loadOrders(onComplete)
                    }
                }
            }
        }
    }

    private fun loadProducts(onComplete: () -> Unit) {
        db.collection("product").get()
            .addOnSuccessListener { result ->
                productMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    register to (document.getString("name") ?: "Sin Informacion")
                }.toMap()
                onComplete()
            }
            .addOnFailureListener {
                productMap = emptyMap()
                onComplete()
            }
    }

    private fun loadPayments(onComplete: () -> Unit) {
        db.collection("payment").get()
            .addOnSuccessListener { result ->
                paymentMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    register to (document.getString("name") ?: "Sin Informacion")
                }.toMap()
                onComplete()
            }
            .addOnFailureListener {
                paymentMap = emptyMap()
                onComplete()
            }
    }

    private fun loadGateways(onComplete: () -> Unit) {
        db.collection("gateway").get()
            .addOnSuccessListener { result ->
                gatewayMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    register to (document.getString("name") ?: "Sin Informacion")
                }.toMap()
                onComplete()
            }
            .addOnFailureListener {
                gatewayMap = emptyMap()
                onComplete()
            }
    }

    private fun loadUsers(onComplete: () -> Unit) {
        db.collection("user").get()
            .addOnSuccessListener { result ->
                userMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    register to (document.getString("email") ?: "Sin Informacion")
                }.toMap()
                onComplete()
            }
            .addOnFailureListener {
                userMap = emptyMap()
                onComplete()
            }
    }

    private fun loadOrders(onComplete: () -> Unit) {
        db.collection("order").get()
            .addOnSuccessListener { result ->
                orderMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    register to (document.getString("reference") ?: "Orden $register")
                }.toMap()
                onComplete()
            }
            .addOnFailureListener {
                orderMap = emptyMap()
                onComplete()
            }
    }

    private fun loadItems() {
        collection.get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()

                val items = result.documents.mapNotNull { it.toObject(PurchaseModel::class.java) }
                    .sortedBy { it.register }

                items.forEach { dataList.addView(loadCard(it)) }
            }
    }

    private fun loadCard(data: PurchaseModel): CardView {
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
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        addText(textContainer, "Registro: ${data.register}")
        addText(textContainer, "Cantidad: ${data.amount ?: 0}")
        addText(textContainer, "Valor: ${data.value ?: 0.0}")
        addText(textContainer, "Total: ${data.total ?: 0.0}")
        addText(textContainer, "Producto: ${label(productMap, data.idProduct)}")
        addText(textContainer, "Metodo Pago: ${label(paymentMap, data.idMethod)}")
        addText(textContainer, "Pasarela: ${label(gatewayMap, data.idGangway)}")
        addText(textContainer, "Usuario: ${label(userMap, data.idUser)}")
        addText(textContainer, "Orden: ${label(orderMap, data.idOrder)}")

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
                startActivity(Intent(this@PurchaseSelectActivity, PurchaseUpdateActivity::class.java).putExtra("register", data.register))
            }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }
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
