package com.compensar.tienda.ui.model.order

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.OrderModel
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID

class OrderCreateActivity : AppCompatActivity() {
    private lateinit var actionHome: LinearLayout; private lateinit var actionReturn: TextView; private lateinit var actionCancel: Button; private lateinit var actionExecute: Button
    private lateinit var fieldAddress: EditText; private lateinit var fieldReference: EditText; private lateinit var fieldTotal: EditText; private lateinit var fieldIdUser: Spinner
    private val db = FirebaseFirestore.getInstance(); private val collection = db.collection("order"); private var generatedRegister: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.model_order_create); initViews(); initEvents(); loadNextRegister(); FirestoreSelectHelper.load(this, fieldIdUser, "user", listOf("names","srnms","email"), 0) }
    private fun initViews(){ actionHome=findViewById(R.id.actionHome); actionReturn=findViewById(R.id.actionReturn); actionCancel=findViewById(R.id.actionCancel); actionExecute=findViewById(R.id.actionExecute); fieldAddress=findViewById(R.id.fieldAddress); fieldReference=findViewById(R.id.fieldReference); fieldTotal=findViewById(R.id.fieldTotal); fieldIdUser=findViewById(R.id.fieldIdUser); fieldReference.setText(UUID.randomUUID().toString().replace("-","").take(12).uppercase()) }
    private fun initEvents(){ actionHome.setOnClickListener{startActivity(Intent(this,DashboardAdminActivity::class.java));finish()}; actionReturn.setOnClickListener{finish()}; actionCancel.setOnClickListener{finish()}; actionExecute.setOnClickListener{save()} }
    private fun loadNextRegister(){ actionExecute.isEnabled=false; collection.orderBy("register",Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener{ generatedRegister=(it.documents.firstOrNull()?.getLong("register")?:0L)+1L; actionExecute.isEnabled=true }.addOnFailureListener{ generatedRegister=null; actionExecute.isEnabled=true } }
    private fun save(){ val register=generatedRegister ?: return; val idUser=FirestoreSelectHelper.getSelectedId(fieldIdUser); if(idUser==null){Toast.makeText(this,"Debe seleccionar usuario",Toast.LENGTH_SHORT).show();return}; val data=OrderModel(register,fieldAddress.text.toString().trim(),fieldReference.text.toString().trim(),fieldTotal.text.toString().toDoubleOrNull(),idUser); collection.document(register.toString()).set(data).addOnSuccessListener{Toast.makeText(this,"Orden creada",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
}
