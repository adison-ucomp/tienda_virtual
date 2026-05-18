package com.compensar.tienda.ui.model.category

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryUpdateActivity : AppCompatActivity() {

    private lateinit var categoryUpdateIdTxt: EditText
    private lateinit var categoryUpdateNameTxt: EditText
    private lateinit var categoryUpdateBackBtn: Button
    private lateinit var categoryUpdateSaveBtn: Button

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("category")

    private var register: Long = 0
    private var category: CategoryModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_category_update)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadCategory()
    }

    private fun initViews() {
        categoryUpdateIdTxt = findViewById(R.id.categoryUpdateIdTxt)
        categoryUpdateNameTxt = findViewById(R.id.categoryUpdateNameTxt)
        categoryUpdateBackBtn = findViewById(R.id.categoryUpdateBackBtn)
        categoryUpdateSaveBtn = findViewById(R.id.categoryUpdateSaveBtn)
    }

    private fun initEvents() {
        categoryUpdateBackBtn.setOnClickListener {
            finish()
        }

        categoryUpdateSaveBtn.setOnClickListener {
            updateCategory()
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

        categoryUpdateIdTxt.setText(currentCategory.register.toString())
        categoryUpdateNameTxt.setText(currentCategory.name ?: "")
    }

    private fun updateCategory() {
        val name = categoryUpdateNameTxt.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedCategory = CategoryModel(
            register = register,
            name = name
        )

        collection.document(register.toString())
            .set(updatedCategory)
            .addOnSuccessListener {
                Toast.makeText(this, "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}