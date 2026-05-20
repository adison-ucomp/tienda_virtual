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
import com.google.firebase.firestore.Query

class AddressCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout; private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button
    private lateinit var fieldAddress: EditText; private lateinit var fieldLabel: EditText; private lateinit var fieldIdUser: Spinner; private lateinit var fieldIdUbication: Spinner
    private val collection = FirebaseFirestore.getInstance().collection("address"); private var generatedRegister: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_address_create); SessionNavigation.bindProfile(this); initViews(); initEvents(); loadSelectors(); loadNextRegister() }
    private fun initViews(){ actionHome=findViewById(R.id.actionHome); actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); fieldAddress=findViewById(R.id.fieldAddress); fieldLabel=findViewById(R.id.fieldLabel); fieldIdUser=findViewById(R.id.fieldIdUser); fieldIdUbication=findViewById(R.id.fieldIdUbication) }
    private fun initEvents(){ actionHome.setOnClickListener{startActivity(Intent(this,DashboardAdminActivity::class.java));finish()}; actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{actionOperate()} }
    private fun loadSelectors(){ FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names","srnms","email"), 0); FirestoreSelectHelper.load(this, fieldIdUbication, "ubication", listOf("name"), 0) }
    private fun actionOperate(){ val register=generatedRegister; if(register==null||register<=0){Toast.makeText(this,"No fue posible generar ID",Toast.LENGTH_SHORT).show();loadNextRegister();return}; val address=fieldAddress.text.toString().trim(); val label=fieldLabel.text.toString().trim(); if(address.isEmpty()){Toast.makeText(this,"Debes ingresar la dirección",Toast.LENGTH_SHORT).show();return}; if(label.isEmpty()){Toast.makeText(this,"Debes ingresar la etiqueta",Toast.LENGTH_SHORT).show();return}; val idUser=FirestoreSelectHelper.getSelectedId(fieldIdUser); val idUbication=FirestoreSelectHelper.getSelectedId(fieldIdUbication); if(idUser==null||idUbication==null){Toast.makeText(this,"Debes seleccionar usuario y ubicación",Toast.LENGTH_SHORT).show();return}; val data=AddressModel(register,address,label,idUbication,idUser); collection.document(register.toString()).set(data).addOnSuccessListener{Toast.makeText(this,"Dirección creada",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
    private fun loadNextRegister(){ actionExecute.isEnabled=false; collection.orderBy("register", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener{generatedRegister=(it.documents.firstOrNull()?.getLong("register")?:0L)+1L;actionExecute.isEnabled=true}.addOnFailureListener{generatedRegister=null;actionExecute.isEnabled=true} }
}
