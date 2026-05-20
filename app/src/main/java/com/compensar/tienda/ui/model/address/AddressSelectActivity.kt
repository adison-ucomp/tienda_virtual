package com.compensar.tienda.ui.model.address

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
import com.compensar.tienda.domain.model.AddressModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class AddressSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("address")

    private var userMap: Map<Long, String> = emptyMap()
    private var ubicationMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_address_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }
        actionNew.setOnClickListener { startActivity(Intent(this, AddressCreateActivity::class.java)) }
        loadDataBase()
    }

    override fun onResume() {
        super.onResume()
        loadDataBase()
    }

    private fun loadDataBase() {
        db.collection("user").get().addOnSuccessListener { users ->
            userMap = users.documents.mapNotNull { document ->
                val register = document.getLong("register") ?: return@mapNotNull null
                val description = listOfNotNull(document.getString("names"), document.getString("srnms"), document.getString("email"))
                    .joinToString(" ").trim().ifEmpty { "Sin Informacion" }
                register to description
            }.toMap()

            db.collection("ubication").get().addOnSuccessListener { ubications ->
                ubicationMap = ubications.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    register to (document.getString("name") ?: "Sin Informacion")
                }.toMap()
                loadItems()
            }.addOnFailureListener {
                ubicationMap = emptyMap()
                loadItems()
            }
        }.addOnFailureListener {
            userMap = emptyMap()
            loadItems()
        }
    }

    private fun loadItems() {
        collection.get().addOnSuccessListener { result ->
            dataList.removeAllViews()
            result.documents.mapNotNull { it.toObject(AddressModel::class.java) }
                .sortedBy { it.register }
                .forEach { dataList.addView(loadCard(it)) }
        }
    }

    private fun loadCard(data: AddressModel): CardView {
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
        addText(textContainer, "Ubicación: ${label(ubicationMap, data.id_ubication)}")
        addText(textContainer, "Dirección: ${data.address ?: ""}")
        addText(textContainer, "Etiqueta: ${data.label ?: ""}")
        addText(textContainer, "Usuario: ${label(userMap, data.idUser)}")

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val btnEdit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply { setMargins(0, 0, 0, dp(16)) }
            setOnClickListener { startActivity(Intent(this@AddressSelectActivity, AddressUpdateActivity::class.java).putExtra("register", data.register)) }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28))
            setOnClickListener { startActivity(Intent(this@AddressSelectActivity, AddressDeleteActivity::class.java).putExtra("register", data.register)) }
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

    private fun label(map: Map<Long, String>, id: Long): String = map[id] ?: "Sin Informacion"
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
