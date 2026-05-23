package com.compensar.tienda.ui.buyer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.model.AddressModel
import com.compensar.tienda.ui.common.SessionManager
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [BuyerAddressActivity].
 *
 * Responsable de la logica asociada al pantalla del flujo de comprador.
 */
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
    private fun initEvents(){ btnBack.setOnClickListener{ finish() }; btnNew.setOnClickListener{ startActivity(Intent(this, BuyerMapsActivity::class.java)) } }
    private fun loadUbicationsThenAddresses(){ db.collection("ubication").get().addOnSuccessListener{ result -> ubicationNames.clear(); result.documents.forEach{doc-> ubicationNames[doc.getLong("register")?:0L]=doc.getString("name")?:"" }; loadAddresses() }.addOnFailureListener{ loadAddresses() } }
    private fun loadAddresses(){ val user=SessionManager.getRegister(this); addressList.removeAllViews(); if(user<=0){ addEmpty("Debes iniciar sesión para ver tus direcciones"); return }; db.collection("address").whereEqualTo("idUser", user).get().addOnSuccessListener{ result -> addressList.removeAllViews(); val items=result.documents.mapNotNull{it.toObject(AddressModel::class.java)}.sortedBy{it.register}; if(items.isEmpty()) addEmpty("No tienes direcciones registradas"); items.forEach{ addressList.addView(card(it)) } }.addOnFailureListener{ e -> addEmpty("Error cargando direcciones: ${e.message}") } }
    private fun addEmpty(text:String){ val view=TextView(this).apply{this.text=text;textSize=15f;setTextColor(getColor(R.color.black));gravity=Gravity.CENTER;setPadding(0,dp(30),0,dp(30))}; addressList.addView(view) }
    private fun card(data: AddressModel): CardView {
        val card = CardView(this).apply {
            radius = dp(8).toFloat()
            cardElevation = dp(3).toFloat()
            setCardBackgroundColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(18))
            }
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        val ubicationName = ubicationNames[data.id_ubication]?.ifBlank { "Ubicación" } ?: "Ubicación"
        val labelText = data.label?.trim().orEmpty()

        val title = TextView(this).apply {
            text = if (labelText.isNotBlank()) "$ubicationName · $labelText" else ubicationName
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
        }

        val addr = TextView(this).apply {
            text = data.address ?: ""
            textSize = 15f
            setTextColor(0xFF4B5060.toInt())
            setPadding(0, dp(8), 0, 0)
        }

        val separator = View(this).apply {
            setBackgroundColor(0xFFD8DDE8.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
            ).apply {
                setMargins(0, dp(24), 0, dp(16))
            }
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val edit = actionButton(
            text = "Editar",
            iconRes = R.drawable.ic_edit
        ) {
            startActivity(
                Intent(this@BuyerAddressActivity, BuyerMapsActivity::class.java)
                    .putExtra("register", data.register)
            )
        }

        val del = actionButton(
            text = "Eliminar",
            iconRes = R.drawable.ic_delete
        ) {
            deleteAddress(data.register)
        }

        actions.addView(edit)
        actions.addView(del)

        box.addView(title)
        box.addView(addr)
        box.addView(separator)
        box.addView(actions)
        card.addView(box)
        return card
    }

    private fun actionButton(text: String, iconRes: Int, onClick: () -> Unit): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(this@BuyerAddressActivity, R.drawable.bg_button)
            isClickable = true
            isFocusable = true
            setPadding(dp(12), 0, dp(12), 0)
            layoutParams = LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                setMargins(dp(6), 0, dp(6), 0)
            }
            setOnClickListener { onClick() }

            val icon = ImageView(this@BuyerAddressActivity).apply {
                setImageResource(iconRes)
                layoutParams = LinearLayout.LayoutParams(dp(18), dp(18)).apply {
                    setMargins(0, 0, dp(6), 0)
                }
            }

            val label = TextView(this@BuyerAddressActivity).apply {
                this.text = text
                textSize = 15f
                setTextColor(0xFF222222.toInt())
                setTypeface(null, Typeface.BOLD)
            }

            addView(icon)
            addView(label)
        }
    }
    private fun deleteAddress(register:Long){ db.collection("address").document(register.toString()).delete().addOnSuccessListener{ Toast.makeText(this,"Dirección eliminada",Toast.LENGTH_SHORT).show(); loadAddresses() }.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}

