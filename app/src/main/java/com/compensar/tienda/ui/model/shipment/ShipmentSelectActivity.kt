package com.compensar.tienda.ui.model.shipment

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
import com.compensar.tienda.model.ShipmentModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.SelectSearchHelper
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [ShipmentSelectActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class ShipmentSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionNew: LinearLayout
    private lateinit var dataList: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shipment")
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_shipment_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        actionNew = findViewById(R.id.actionNew)
        dataList = findViewById(R.id.dataList)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener { startActivity(Intent(this, ShipmentCreateActivity::class.java)) }

        SelectSearchHelper.bind(
            this,
            onSearch = { query ->
                searchQuery = query
                loadDataBase()
            },
            onClean = {
                searchQuery = ""
                loadDataBase()
            }
        )

        loadDataBase()
    }

    override fun onResume() {
        super.onResume()
        loadDataBase()
    }

    private fun loadDataBase() {
        collection.get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()
                val items = result.documents
                    .mapNotNull { document -> document.toObject(ShipmentModel::class.java) }
                    .filter { SelectSearchHelper.matches(it, searchQuery) }
                    .sortedBy { it.register }

                items.forEach { data ->
                    dataList.addView(loadCard(data))
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun loadCard(data: ShipmentModel): CardView {
        val card = CardView(this).apply {
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

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val txtRegister = TextView(this).apply {
            text = "Registro: ${data.register}"
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(getColor(R.color.black))
            setPadding(0, dp(6), 0, 0)
        }
        textContainer.addView(txtRegister)

        val txtName = TextView(this).apply {
            text = "Nombre: ${data.name ?: ""}"
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(getColor(R.color.black))
            setPadding(0, dp(6), 0, 0)
        }
        textContainer.addView(txtName)

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val edit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }
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
                setMargins(0, 0, 0, dp(16))
            }
            setOnClickListener {
                startActivity(
                    Intent(this@ShipmentSelectActivity, ShipmentDeleteActivity::class.java)
                        .putExtra("register", data.register)
                )
            }
        }

        buttonContainer.addView(edit)
        buttonContainer.addView(del)

        row.addView(textContainer)
        row.addView(buttonContainer)
        card.addView(row)
        return card
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}

