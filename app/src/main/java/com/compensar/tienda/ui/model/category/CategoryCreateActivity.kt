package com.compensar.tienda.ui.model.category

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryCreateActivity : AppCompatActivity() {

    private lateinit var categoryCreateIdTxt: EditText
    private lateinit var categoryCreateNameTxt: EditText
    private lateinit var categoryCreateBackBtn: Button
    private lateinit var categoryCreateSaveBtn: Button

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("category")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_category_create)

        initViews()
        initEvents()
    }

    private fun initViews() {
        categoryCreateIdTxt = findViewById(R.id.categoryCreateIdTxt)
        categoryCreateNameTxt = findViewById(R.id.categoryCreateNameTxt)
        categoryCreateBackBtn = findViewById(R.id.categoryCreateBackBtn)
        categoryCreateSaveBtn = findViewById(R.id.categoryCreateSaveBtn)
    }

    private fun initEvents() {
        categoryCreateBackBtn.setOnClickListener {
            finish()
        }

        categoryCreateSaveBtn.setOnClickListener {
            createCategory()
        }
    }

    private fun createCategory() {
        val registerText = categoryCreateIdTxt.text.toString().trim()
        val name = categoryCreateNameTxt.text.toString().trim()

        if (registerText.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el ID", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val register = registerText.toLongOrNull()

        if (register == null || register <= 0) {
            Toast.makeText(this, "El ID no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        val category = CategoryModel(
            register = register,
            name = name
        )

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "Ya existe una categoría con ese ID", Toast.LENGTH_SHORT).show()
                } else {
                    saveCategory(category)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveCategory(category: CategoryModel) {
        collection.document(category.register.toString())
            .set(category)
            .addOnSuccessListener {
                Toast.makeText(this, "Categoría creada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}