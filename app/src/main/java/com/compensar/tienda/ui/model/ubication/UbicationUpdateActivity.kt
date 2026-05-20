package com.compensar.tienda.ui.model.ubication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.UbicationModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class UbicationUpdateActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button; private lateinit var fieldName: EditText
    private val collection = FirebaseFirestore.getInstance().collection("ubication"); private var register: Long=0
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_ubication_update); SessionNavigation.bindProfile(this); register=intent.getLongExtra("register",0); initViews(); initEvents(); load() }
    private fun initViews(){ actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); fieldName=findViewById(R.id.fieldName) }
    private fun initEvents(){ actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{ update() } }
    private fun load(){ if(register<=0){finish();return}; collection.document(register.toString()).get().addOnSuccessListener{ fieldName.setText(it.getString("name")?:"") } }
    private fun update(){ val name=fieldName.text.toString().trim(); if(name.isEmpty()){Toast.makeText(this,"Debes ingresar el nombre",Toast.LENGTH_SHORT).show();return}; collection.document(register.toString()).set(UbicationModel(register,name)).addOnSuccessListener{Toast.makeText(this,"Registro actualizado",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
}
