package com.compensar.tienda.ui.model.shipment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.ShipmentModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class ShipmentSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionNew: LinearLayout
    private lateinit var dataList: LinearLayout

    private val collection = FirebaseFirestore.getInstance().collection("shipment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_shipment_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        actionNew = findViewById(R.id.actionNew)
        dataList = findViewById(R.id.dataList)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener { startActivity(Intent(this, ShipmentCreateActivity::class.java)) }

        ensureDefaults()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun ensureDefaults() {
        val defaults = listOf("Pendiente", "Enviado", "Entregado")
        defaults.forEachIndexed { index, name ->
            val register = (index + 1).toLong()
            collection.document(register.toString()).get()
                .addOnSuccessListener {
                    if (!it.exists()) {
                        collection.document(register.toString()).set(ShipmentModel(register, name))
                    }
                }
        }
        load()
    }

    private fun load() {
        collection.get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()
                result.documents
                    .mapNotNull { it.toObject(ShipmentModel::class.java) }
                    .sortedBy { it.register }
                    .forEach { dataList.addView(card(it)) }
            }
            .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    private fun card(data: ShipmentModel): CardView {
        val card = CardView(this).apply {
            radius = dp(16).toFloat()
            cardElevation = dp(4).toFloat()
            setCardBackgroundColor(getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(12))
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val txt = TextView(this).apply {
            text = "${data.register}. ${data.name ?: ""}"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(getColor(R.color.black))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val edit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
            setOnClickListener {
                startActivity(
                    Intent(this@ShipmentSelectActivity, ShipmentUpdateActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        }

        val del = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(dp(16), 0, 0, 0)
            }
            setOnClickListener {
                startActivity(
                    Intent(this@ShipmentSelectActivity, ShipmentDeleteActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        }

        row.addView(txt)
        row.addView(edit)
        row.addView(del)
        card.addView(row)
        return card
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
