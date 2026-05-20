package com.compensar.tienda.ui.model.shipment

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.ShipmentModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ShipmentCreateActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var actionCancel: Button
    private lateinit var actionExecute: Button
    private lateinit var fieldName: EditText
    private val collection = FirebaseFirestore.getInstance().collection("shipment")
    private var generatedRegister: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_shipment_create); SessionNavigation.bindProfile(this); initViews(); initEvents(); loadNextRegister() }
    private fun initViews(){ actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); fieldName=findViewById(R.id.fieldName) }
    private fun initEvents(){ actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{ save() } }
    private fun loadNextRegister(){ actionExecute.isEnabled=false; collection.orderBy("register", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener{ generatedRegister=(it.documents.firstOrNull()?.getLong("register")?:0L)+1L; actionExecute.isEnabled=true }.addOnFailureListener{ actionExecute.isEnabled=true; Toast.makeText(this,"No fue posible generar ID",Toast.LENGTH_SHORT).show() } }
    private fun save(){ val name=fieldName.text.toString().trim(); if(name.isEmpty()){ Toast.makeText(this,"Debes ingresar el nombre",Toast.LENGTH_SHORT).show(); return}; val data=ShipmentModel(generatedRegister,name); collection.document(generatedRegister.toString()).set(data).addOnSuccessListener{ Toast.makeText(this,"Estado creado",Toast.LENGTH_SHORT).show(); finish() }.addOnFailureListener{ Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show() } }
}
