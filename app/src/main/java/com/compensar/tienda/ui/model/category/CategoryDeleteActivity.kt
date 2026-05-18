package com.compensar.tienda.ui.model.category

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryDeleteActivity : AppCompatActivity() {
    private lateinit var categoryDeleteIdTxt: TextView
    private lateinit var categoryDeleteNameTxt: TextView
    private lateinit var categoryBackBtn: Button
    private lateinit var categoryDeleteBtn: Button

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("category")

    private var register: Long = 0
    private var category: CategoryModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_category_delete)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadCategory()
    }

    private fun initViews() {
        categoryDeleteIdTxt = findViewById(R.id.categoryDeleteIdTxt)
        categoryDeleteNameTxt = findViewById(R.id.categoryDeleteNameTxt)
        categoryBackBtn = findViewById(R.id.categoryBackBtn)
        categoryDeleteBtn = findViewById(R.id.categoryDeleteBtn)
    }

    private fun initEvents() {
        categoryBackBtn.setOnClickListener {
            finish()
        }

        categoryDeleteBtn.setOnClickListener {
            deleteCategory()
        }
    }

    private fun loadCategory() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    category = document.toObject(CategoryModel::class.java)
                    showCategory()
                } else {
                    Toast.makeText(this, "No se encontró la categoría", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showCategory() {
        val currentCategory = category ?: return

        categoryDeleteIdTxt.text = currentCategory.register.toString()
        categoryDeleteNameTxt.text = currentCategory.name ?: ""
    }

    private fun deleteCategory() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Categoría eliminada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}