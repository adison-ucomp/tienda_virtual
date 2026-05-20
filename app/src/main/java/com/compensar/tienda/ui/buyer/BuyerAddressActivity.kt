package com.compensar.tienda.ui.buyer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.AddressModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.home.HomeProductActivity
import com.google.firebase.firestore.FirebaseFirestore

class BuyerAddressActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView
    private lateinit var btnNew: Button
    private lateinit var addressList: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private val ubicationNames = mutableMapOf<Long, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge(); setContentView(R.layout.buyer_address); applyWindowInsets(); initViews(); initEvents()
    }
    override fun onResume(){ super.onResume(); loadUbicationsThenAddresses() }
    private fun applyWindowInsets(){ ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v,insets-> val b=insets.getInsets(WindowInsetsCompat.Type.systemBars()); v.setPadding(b.left,b.top,b.right,b.bottom); insets } }
    private fun initViews(){ btnBack=findViewById(R.id.btnBack); btnNew=findViewById(R.id.btnAddressNew); addressList=findViewById(R.id.addressList) }
    private fun initEvents(){ btnBack.setOnClickListener{ startActivity(Intent(this, HomeProductActivity::class.java)); finish() }; btnNew.setOnClickListener{ startActivity(Intent(this, BuyerMapsActivity::class.java)) } }
    private fun loadUbicationsThenAddresses(){ db.collection("ubication").get().addOnSuccessListener{ result -> ubicationNames.clear(); result.documents.forEach{doc-> ubicationNames[doc.getLong("register")?:0L]=doc.getString("name")?:"" }; loadAddresses() }.addOnFailureListener{ loadAddresses() } }
    private fun loadAddresses(){ val user=SessionManager.getRegister(this); addressList.removeAllViews(); if(user<=0){ addEmpty("Debes iniciar sesión para ver tus direcciones"); return }; db.collection("address").whereEqualTo("idUser", user).get().addOnSuccessListener{ result -> addressList.removeAllViews(); val items=result.documents.mapNotNull{it.toObject(AddressModel::class.java)}.sortedBy{it.register}; if(items.isEmpty()) addEmpty("No tienes direcciones registradas"); items.forEach{ addressList.addView(card(it)) } }.addOnFailureListener{ e -> addEmpty("Error cargando direcciones: ${e.message}") } }
    private fun addEmpty(text:String){ val view=TextView(this).apply{this.text=text;textSize=15f;setTextColor(getColor(R.color.black));gravity=Gravity.CENTER;setPadding(0,dp(30),0,dp(30))}; addressList.addView(view) }
    private fun card(data: AddressModel): CardView { val card=CardView(this).apply{radius=dp(18).toFloat();cardElevation=dp(4).toFloat();setCardBackgroundColor(0xFFFFFFFF.toInt());layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply{setMargins(0,0,0,dp(14))}}; val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(18),dp(18),dp(18))}; val title=TextView(this).apply{text="${ubicationNames[data.id_ubication] ?: "Ubicación"} · ${data.label ?: ""}";textSize=17f;setTypeface(null,Typeface.BOLD);setTextColor(0xFF111111.toInt())}; val addr=TextView(this).apply{text=data.address ?: "";textSize=14f;setTextColor(0xFF4B5060.toInt());setPadding(0,dp(8),0,0)}; val actions=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.END;setPadding(0,dp(14),0,0)}; val edit=TextView(this).apply{text="Editar";setTextColor(0xFF111111.toInt());setTypeface(null,Typeface.BOLD);setPadding(dp(16),dp(8),dp(16),dp(8));setOnClickListener{ startActivity(Intent(this@BuyerAddressActivity, BuyerMapsActivity::class.java).putExtra("register",data.register)) }}; val del=TextView(this).apply{text="Eliminar";setTextColor(0xFFB00020.toInt());setTypeface(null,Typeface.BOLD);setPadding(dp(16),dp(8),dp(16),dp(8));setOnClickListener{ deleteAddress(data.register) }}; actions.addView(edit); actions.addView(del); box.addView(title); box.addView(addr); box.addView(actions); card.addView(box); return card }
    private fun deleteAddress(register:Long){ db.collection("address").document(register.toString()).delete().addOnSuccessListener{ Toast.makeText(this,"Dirección eliminada",Toast.LENGTH_SHORT).show(); loadAddresses() }.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}
