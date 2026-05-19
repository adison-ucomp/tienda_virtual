package com.compensar.tienda.ui.model.seller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.domain.model.SellerModel
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class SellerCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button

    private lateinit var fieldRegister: EditText
    private lateinit var fieldCompany: EditText
    private lateinit var fieldNit: EditText
    private lateinit var fieldAddress: EditText
    private lateinit var fieldIdUser: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("seller")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_seller_create)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadSelectors()
    }

    private fun initViews() {
        actionHome = findViewById(R.id.actionHome)
        actionReturn = findViewById(R.id.actionReturn)
        actionCancel = findViewById(R.id.actionCancel)
        actionExecute = findViewById(R.id.actionExecute)

        fieldRegister = findViewById(R.id.fieldRegister)
        fieldCompany = findViewById(R.id.fieldCompany)
        fieldNit = findViewById(R.id.fieldNit)
        fieldAddress = findViewById(R.id.fieldAddress)
        fieldIdUser = findViewById(R.id.fieldIdUser)
    }

    private fun initEvents() {
        actionHome.setOnClickListener {
            val intent = Intent(this, DashboardAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        actionReturn.setOnClickListener { finish() }
        actionCancel.setOnClickListener { finish() }
        actionExecute.setOnClickListener { actionOperate() }
    }

    private fun loadSelectors() {
        FirestoreSelectHelper.load(
            context = this,
            spinner = fieldIdUser,
            collectionName = "user",
            labelFields = listOf("names", "srnms", "email"),
            selectedId = 0,
            filterField = "idRole",
            filterValue = 2L
        )
    }

    private fun actionOperate() {
        val registerText = fieldRegister.text.toString().trim()

        if (registerText.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el ID", Toast.LENGTH_SHORT).show()
            return
        }

        val register = registerText.toLongOrNull()

        if (register == null || register <= 0) {
            Toast.makeText(this, "El ID no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        val company = fieldCompany.text.toString().trim()
        val nit = fieldNit.text.toString().trim()
        val address = fieldAddress.text.toString().trim()

        if (company.isEmpty()) {
            Toast.makeText(this, "Debes ingresar company", Toast.LENGTH_SHORT).show()
            return
        }
        if (nit.isEmpty()) {
            Toast.makeText(this, "Debes ingresar nit", Toast.LENGTH_SHORT).show()
            return
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "Debes ingresar address", Toast.LENGTH_SHORT).show()
            return
        }

        val idUser = FirestoreSelectHelper.getSelectedId(fieldIdUser)

        if (idUser == null) {
            Toast.makeText(this, "Debe seleccionar una opción válida", Toast.LENGTH_SHORT).show()
            return
        }

        val data = SellerModel(
            register = register,
            company = company,
            nit = nit,
            address = address,
            idUser = idUser
        )

        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "Ya existe un registro con ese ID", Toast.LENGTH_SHORT).show()
                } else {
                    saveRegister(data)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun saveRegister(data: SellerModel) {
        collection.document(data.register.toString())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro creado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }
}
