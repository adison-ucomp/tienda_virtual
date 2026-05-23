package com.compensar.tienda.ui.seller

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
import com.compensar.tienda.ui.model.common.ImagePreviewHelper
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.model.common.SelectSearchHelper
import com.compensar.tienda.model.ShopModel
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [SellerShopListActivity].
 *
 * Responsable de la logica asociada al pantalla del flujo de vendedor.
 */
class SellerShopListActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shop")
    private var searchQuery: String = ""

    private var sellerMap: Map<Long, String> = emptyMap()
    private var currentSellerRegister: Long = 0L
    private var currentSellerRegisters: Set<Long> = emptySet()


    /**
     * Se ejecuta al crear la pantalla.
     * Inicializa vista, estado y eventos principales.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seller_shop_list)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionReturn.setOnClickListener { finish() }

        actionNew.setOnClickListener {
            val intent = Intent(this, SellerShopStoreActivity::class.java)
            startActivity(intent)
        }

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

    /**
     * Se ejecuta cuando la pantalla vuelve al primer plano.
     * Recarga datos o refresca el estado visual.
     */
    override fun onResume() {
        super.onResume()
        loadReferenceData { loadItems() }
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
    private fun loadDataBase() {
        loadReferenceData { loadItems() }
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
    private fun loadReferenceData(onComplete: () -> Unit) {
        loadSellers(onComplete)
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
    private fun loadSellers(onComplete: () -> Unit) {
        db.collection("seller")
            .get()
            .addOnSuccessListener { result ->
                val userRegister = SessionManager.getRegister(this)
                val registers = mutableSetOf<Long>()
                currentSellerRegister = 0L
                sellerMap = result.documents.mapNotNull { document ->
                    val register = document.getLong("register") ?: return@mapNotNull null
                    val idUser = document.getLong("idUser") ?: 0L
                    val description = document.getString("company") ?: document.getString("nit") ?: "Sin Informacion"

                    if (idUser == userRegister || register == userRegister) {
                        registers.add(register)
                    }

                    register to description
                }.toMap()

                if (registers.isEmpty() && userRegister > 0L) {
                    registers.add(userRegister)
                }

                currentSellerRegisters = registers
                currentSellerRegister = registers.firstOrNull() ?: 0L
                onComplete()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                sellerMap = emptyMap()
                onComplete()
            }
    }


    /**
     * Carga informacion desde origen local o remoto.
     */
    private fun loadItems() {
        collection
            .get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()

                val items = result.documents.mapNotNull { document ->
                    document.toObject(ShopModel::class.java)
                }.filter { shop ->
                    shop.idSeller in currentSellerRegisters
                }.filter { SelectSearchHelper.matches(it, searchQuery) }
                    .sortedBy { it.register }

                items.forEach { data ->
                    dataList.addView(loadCard(data))
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    /**
     * Carga informacion desde origen local o remoto.
     */
    private fun loadCard(data: ShopModel): CardView {
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
        addText(textContainer, "Empresa: ${label(sellerMap, data.idSeller)}")

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
                val intent = Intent(this@SellerShopListActivity, SellerShopEditActivity::class.java)
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
                val intent = Intent(this@SellerShopListActivity, SellerShopQuitActivity::class.java)
                intent.putExtra("register", data.register)
                startActivity(intent)
            }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)

        ImagePreviewHelper.addPreviewToRow(this, mainRow, data.storefire)
        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)

        cardView.addView(mainRow)

        return cardView
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
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

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun label(map: Map<Long, String>, id: Long): String {
        return map[id] ?: "Sin Informacion"
    }


    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

