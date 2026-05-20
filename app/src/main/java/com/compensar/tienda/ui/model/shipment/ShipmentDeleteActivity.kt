package com.compensar.tienda.ui.model.shipment

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class ShipmentDeleteActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button; private lateinit var fieldRegister: TextView; private lateinit var fieldName: TextView
    private val collection = FirebaseFirestore.getInstance().collection("shipment"); private var register: Long=0
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_shipment_delete); SessionNavigation.bindProfile(this); register=intent.getLongExtra("register",0); initViews(); initEvents(); load() }
    private fun initViews(){ actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); fieldRegister=findViewById(R.id.fieldRegister); fieldName=findViewById(R.id.fieldName) }
    private fun initEvents(){ actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{ collection.document(register.toString()).delete().addOnSuccessListener{Toast.makeText(this,"Estado eliminado",Toast.LENGTH_SHORT).show();finish()} } }
    private fun load(){ fieldRegister.text=register.toString(); collection.document(register.toString()).get().addOnSuccessListener{ fieldName.text=it.getString("name")?:"" } }
}
