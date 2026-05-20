package com.compensar.tienda.ui.model.order

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.OrderModel
import com.google.firebase.firestore.FirebaseFirestore

class OrderDeleteActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button; private lateinit var txtInfo: TextView
    private val collection = FirebaseFirestore.getInstance().collection("order"); private var register: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_order_delete); register=intent.getLongExtra("register",0); actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); txtInfo=findViewById(R.id.txtInfo); actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{delete()}; load() }
    private fun load(){ collection.document(register.toString()).get().addOnSuccessListener{ val data=it.toObject(OrderModel::class.java); txtInfo.text="Registro: ${data?.register}\nReferencia: ${data?.reference}\nDirección: ${data?.address}\nTotal: ${data?.total}\nUsuario: ${data?.idUser}" } }
    private fun delete(){ collection.document(register.toString()).delete().addOnSuccessListener{Toast.makeText(this,"Orden eliminada",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
}
