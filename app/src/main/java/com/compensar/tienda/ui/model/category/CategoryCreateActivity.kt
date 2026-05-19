package com.compensar.tienda.ui.model.category

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.CategoryModel
import com.compensar.tienda.ui.platform.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class CategoryCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    
    private lateinit var fieldRegister: EditText
    private lateinit var fieldName: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("category")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_category_create)

        initViews()
        initEvents()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, DashboardAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener {
            finish()
        }
        
        actionCancel.setOnClickListener {
            finish()
        }

        actionExecute.setOnClickListener {
            actionOperate()
        }
    }

    private fun actionOperate() {
        val registerText = fieldRegister.text.toString().trim()
        val name = fieldName.text.toString().trim()

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