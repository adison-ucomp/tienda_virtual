package com.compensar.tienda.ui.model.address

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.AddressModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.model.common.ModuleView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [AddressDeleteActivity].
 *
 * Responsable de la logica asociada al pantalla de mantenimiento (CRUD) de modelos.
 */
class AddressDeleteActivity : AppCompatActivity() {
    private lateinit var titleHeader: TextView
    private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button; private lateinit var fieldRegister: TextView; private lateinit var fieldAddress: TextView; private lateinit var fieldLabel: TextView
    private val collection = FirebaseFirestore.getInstance().collection("address"); private var register: Long=0
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_address_delete); SessionNavigation.bindProfile(this); register=intent.getLongExtra("register",0); initViews(); initEvents(); load() }
    private fun initViews(){ titleHeader=findViewById(R.id.titleHeader); actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); ModuleView.bindTitle(titleHeader, "Eliminar", "address", "Direcciones"); fieldRegister=findViewById(R.id.fieldRegister); fieldAddress=findViewById(R.id.fieldAddress); fieldLabel=findViewById(R.id.fieldLabel) }
    private fun initEvents(){ actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{ collection.document(register.toString()).delete().addOnSuccessListener{Toast.makeText(this,"Dirección eliminada",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} } }
    private fun load(){ fieldRegister.text=register.toString(); collection.document(register.toString()).get().addOnSuccessListener{ val data=it.toObject(AddressModel::class.java); fieldAddress.text=data?.address?:""; fieldLabel.text=data?.label?:"" } }
}

