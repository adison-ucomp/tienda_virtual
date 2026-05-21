package com.compensar.tienda.ui.model.module

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.ModuleModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore

class ModuleDeleteActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: TextView
    private lateinit var fieldName: TextView
    private lateinit var fieldModel: TextView
    private lateinit var fieldDetail: TextView
    private lateinit var fieldState: TextView

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("module")

    private var register: Long = 0
    private var data: ModuleModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_module_delete)
        SessionNavigation.bindProfile(this)

        register = intent.getLongExtra("register", 0)

        initViews()
        initEvents()
        loadRegister()
    }

    private fun initViews() {
        titleHeader = findViewById(R.id.titleHeader)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)
        fieldRegister = findViewById(R.id.fieldRegister)
        fieldName = findViewById(R.id.fieldName)
        fieldModel = findViewById(R.id.fieldModel)
        fieldDetail = findViewById(R.id.fieldDetail)
        fieldState = findViewById(R.id.fieldState)

        ModuleView.bindTitle(titleHeader, "Eliminar", "module", "Modulos")
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadRegister() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    data = document.toObject(ModuleModel::class.java)
                    showRegister()
                } else {
                    Toast.makeText(this, "No se encontró el registro", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun showRegister() {
        val current = data ?: return
        fieldRegister.text = current.register.toString()
        fieldName.text = current.name.toString()
        fieldModel.text = current.model.toString()
        fieldDetail.text = current.detail.toString()
        fieldState.text = if (current.state == false) "Apagado" else "Activo"
    }

    private fun actionOperate() {
        if (register <= 0) {
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_SHORT).show()
            return
        }

        collection.document(register.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}
