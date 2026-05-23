package com.compensar.tienda.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.SpecifyModel
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [HomeSpecifyActivity].
 *
 * Responsable de la logica asociada al pantalla o helper del flujo de inicio/autenticacion/compra.
 */
class HomeSpecifyActivity : AppCompatActivity(){
 private lateinit var btnBack:TextView; private lateinit var specList:LinearLayout; private var productRegister:Long=0
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContentView(R.layout.home_specify);productRegister=intent.getLongExtra("productRegister",0);btnBack=findViewById(R.id.btnBack);specList=findViewById(R.id.specList);btnBack.setOnClickListener{finish()};load()}
 private fun load(){ FirebaseFirestore.getInstance().collection("specify").whereEqualTo("idProduct",productRegister).get().addOnSuccessListener{ result -> specList.removeAllViews(); val items=result.documents.mapNotNull{it.toObject(SpecifyModel::class.java)}.sortedBy{it.register}; if(items.isEmpty()){specList.addView(TextView(this).apply{text="No hay especificaciones";textSize=15f})}; items.forEach{specList.addView(card(it))} } }
 private fun card(data:SpecifyModel):CardView{ val card=CardView(this).apply{radius=dp(16).toFloat();cardElevation=dp(4).toFloat();setCardBackgroundColor(0xFFFFFFFF.toInt());layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply{setMargins(0,0,0,dp(12))}}; val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(18),dp(18),dp(18))}; box.addView(TextView(this).apply{text=data.name?:"Especificación";textSize=16f;setTypeface(null,Typeface.BOLD);setTextColor(0xFF111111.toInt())}); box.addView(TextView(this).apply{text=data.detail?:"";textSize=14f;setTextColor(0xFF4B5060.toInt());setPadding(0,dp(8),0,0)}); card.addView(box); return card }
 private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}

