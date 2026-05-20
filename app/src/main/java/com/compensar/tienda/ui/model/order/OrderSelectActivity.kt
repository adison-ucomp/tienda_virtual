package com.compensar.tienda.ui.model.order

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class OrderSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCreate: Button
    private lateinit var listContainer: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private var shipmentMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_order_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        actionCreate = findViewById(R.id.actionCreate)
        listContainer = findViewById(R.id.listContainer)

        actionReturn.setOnClickListener { finish() }
        actionCreate.setOnClickListener { startActivity(Intent(this, OrderCreateActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        loadShipments { loadData() }
    }

    private fun loadShipments(onComplete: () -> Unit) {
        db.collection("shipment").get()
            .addOnSuccessListener { result ->
                shipmentMap = result.documents.mapNotNull { doc ->
                    val register = doc.getLong("register") ?: return@mapNotNull null
                    register to (doc.getString("name") ?: "Estado $register")
                }.toMap()
                onComplete()
            }
            .addOnFailureListener {
                shipmentMap = emptyMap()
                onComplete()
            }
    }

    private fun loadData() {
        db.collection("order").get()
            .addOnSuccessListener { result ->
                listContainer.removeAllViews()
                val orders = result.documents
                    .mapNotNull { it.toObject(OrderModel::class.java) }
                    .sortedBy { it.register }

                if (orders.isEmpty()) {
                    listContainer.addView(TextView(this).apply {
                        text = "Sin ordenes registradas"
                        gravity = Gravity.CENTER
                        setPadding(0, 40, 0, 40)
                    })
                }

                orders.forEach { listContainer.addView(card(it)) }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun card(data: OrderModel): CardView {
        val card = CardView(this).apply {
            radius = dp(18).toFloat()
            cardElevation = dp(5).toFloat()
            setCardBackgroundColor(getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(14))
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val text = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            text = "Registro: ${data.register}\n" +
                "Referencia: ${data.reference ?: ""}\n" +
                "Direccion: ${data.address ?: ""}\n" +
                "Total: ${data.total ?: 0.0}\n" +
                "Fecha: ${data.date ?: ""}\n" +
                "Hora: ${data.hour ?: ""}\n" +
                "Estado: ${shipmentMap[data.idShipment] ?: data.idShipment}\n" +
                "Usuario: ${data.idUser}"
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(getColor(R.color.black))
        }

        val buttons = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        buttons.addView(ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { setMargins(0, 0, 0, dp(16)) }
            setOnClickListener {
                startActivity(
                    Intent(this@OrderSelectActivity, OrderUpdateActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        })

        buttons.addView(ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
            setOnClickListener {
                startActivity(
                    Intent(this@OrderSelectActivity, OrderDeleteActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        })

        row.addView(text)
        row.addView(buttons)
        card.addView(row)
        return card
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

