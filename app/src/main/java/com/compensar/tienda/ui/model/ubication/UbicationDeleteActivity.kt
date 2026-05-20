package com.compensar.tienda.ui.model.ubication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class UbicationDeleteActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldRegister: TextView
    private lateinit var fieldName: TextView

    private val collection = FirebaseFirestore.getInstance().collection("ubication")
    private var register: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_ubication_delete)
        SessionNavigation.bindProfile(this)
        register = intent.getLongExtra("register", 0)
        initViews()
        initEvents()
        load()
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { delete() }
    }

    private fun load() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no valido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fieldRegister.text = register.toString()
        collection.document(register.toString()).get()
            .addOnSuccessListener { fieldName.text = it.getString("name") ?: "" }
            .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    private fun delete() {
        collection.document(register.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
    }
}
