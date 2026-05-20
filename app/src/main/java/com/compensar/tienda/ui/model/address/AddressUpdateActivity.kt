package com.compensar.tienda.ui.model.address

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.AddressModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore

class AddressUpdateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout; private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button
    private lateinit var fieldAddress: EditText; private lateinit var fieldLabel: EditText; private lateinit var fieldIdUser: Spinner; private lateinit var fieldIdUbication: Spinner
    private val collection = FirebaseFirestore.getInstance().collection("address"); private var register: Long=0; private var currentData: AddressModel?=null
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_address_update); SessionNavigation.bindProfile(this); register=intent.getLongExtra("register",0); initViews(); initEvents(); loadRegister() }
    private fun initViews(){ actionHome=findViewById(R.id.actionHome); actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); fieldAddress=findViewById(R.id.fieldAddress); fieldLabel=findViewById(R.id.fieldLabel); fieldIdUser=findViewById(R.id.fieldIdUser); fieldIdUbication=findViewById(R.id.fieldIdUbication) }
    private fun initEvents(){ actionHome.setOnClickListener{startActivity(Intent(this,DashboardAdminActivity::class.java));finish()}; actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{actionOperate()} }
    private fun loadRegister(){ if(register<=0){finish();return}; collection.document(register.toString()).get().addOnSuccessListener{ if(it.exists()){ currentData=it.toObject(AddressModel::class.java); showRegister() } else finish() } }
    private fun showRegister(){ val c=currentData?:return; fieldAddress.setText(c.address?:""); fieldLabel.setText(c.label?:""); FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names","srnms","email"), c.idUser); FirestoreSelectHelper.load(this, fieldIdUbication, "ubication", listOf("name"), c.id_ubication) }
    private fun actionOperate(){ val address=fieldAddress.text.toString().trim(); val label=fieldLabel.text.toString().trim(); if(address.isEmpty()||label.isEmpty()){Toast.makeText(this,"Debes completar dirección y etiqueta",Toast.LENGTH_SHORT).show();return}; val idUser=FirestoreSelectHelper.getSelectedId(fieldIdUser); val idUbication=FirestoreSelectHelper.getSelectedId(fieldIdUbication); if(idUser==null||idUbication==null){Toast.makeText(this,"Debes seleccionar usuario y ubicación",Toast.LENGTH_SHORT).show();return}; val data=AddressModel(register,address,label,idUbication,idUser); collection.document(register.toString()).set(data).addOnSuccessListener{Toast.makeText(this,"Dirección actualizada",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
}
