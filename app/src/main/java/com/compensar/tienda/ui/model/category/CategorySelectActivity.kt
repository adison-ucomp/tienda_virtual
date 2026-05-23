package com.compensar.tienda.ui.model.category

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
import com.compensar.tienda.ui.model.common.SelectSearchHelper
import com.compensar.tienda.model.CategoryModel
import com.compensar.tienda.ui.admin.AdminDashboardActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [CategorySelectActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class CategorySelectActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("category")
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_category_select)
        SessionNavigation.bindProfile(this)

        actionHome = findViewById(R.id.actionHome)
        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)

        actionHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener {
            finish()
        }

        actionNew.setOnClickListener {
            val intent = Intent(this, CategoryCreateActivity::class.java)
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

    override fun onResume() {
        super.onResume()
        loadDataBase()
    }

    private fun loadDataBase() {
        collection
            .get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()

                val categories = result.documents.mapNotNull { document ->
                    document.toObject(CategoryModel::class.java)
                }.filter { SelectSearchHelper.matches(it, searchQuery) }.sortedBy { it.register }

                categories.forEach { category ->
                    dataList.addView(loadCard(category))
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun loadCard(category: CategoryModel): CardView {
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

        val txtId = TextView(this).apply {
            text = "Registro: ${category.register}"
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
        }

        val txtName = TextView(this).apply {
            text = category.name ?: ""
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }

        textContainer.addView(txtId)
        textContainer.addView(txtName)

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
                val intent = Intent(this@CategorySelectActivity, CategoryUpdateActivity::class.java)
                intent.putExtra("register", category.register)
                startActivity(intent)
            }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }

            setOnClickListener {
                val intent = Intent(this@CategorySelectActivity, CategoryDeleteActivity::class.java)
                intent.putExtra("register", category.register)
                startActivity(intent)
            }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)

        ImagePreviewHelper.addPreviewToRow(this, mainRow, category.storefire)
        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)

        cardView.addView(mainRow)

        return cardView
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

