package com.compensar.tienda.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.ImageModel
import com.compensar.tienda.domain.model.ProductModel
import com.compensar.tienda.ui.buyer.BuyerCartShopActivity
import com.compensar.tienda.ui.common.CartItem
import com.compensar.tienda.ui.common.CartManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore

class HomeDetailActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView; private lateinit var btnCart: TextView; private lateinit var btnAddCart: Button; private lateinit var btnBuyNow: Button; private lateinit var btnAllSpecifications: Button
    private lateinit var imgProduct: ImageView; private lateinit var txtName: TextView; private lateinit var txtPrice: TextView; private lateinit var txtDetail: TextView; private lateinit var relatedImages: LinearLayout
    private var productRegister: Long = 0; private var product: ProductModel? = null
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); enableEdgeToEdge(); setContentView(R.layout.home_detail); productRegister=intent.getLongExtra("productRegister",0); applyWindowInsets(); initViews(); initEvents(); SessionNavigation.bindProfile(this); loadProduct() }
    private fun applyWindowInsets(){ ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){view,insets-> val b=insets.getInsets(WindowInsetsCompat.Type.systemBars()); view.setPadding(b.left,b.top,b.right,b.bottom); insets } }
    private fun initViews(){ btnBack=findViewById(R.id.btnBack); btnCart=findViewById(R.id.btnCart); btnAddCart=findViewById(R.id.btnAddCart); btnBuyNow=findViewById(R.id.btnBuyNow); btnAllSpecifications=findViewById(R.id.btnAllSpecifications); imgProduct=findViewById(R.id.imgProduct); txtName=findViewById(R.id.txtName); txtPrice=findViewById(R.id.txtPrice); txtDetail=findViewById(R.id.txtDetail); relatedImages=findViewById(R.id.relatedImages) }
    private fun initEvents(){ btnBack.setOnClickListener{finish()}; btnCart.setOnClickListener{SessionNavigation.openCartOrLogin(this)}; btnAddCart.setOnClickListener{ addToCart(false) }; btnBuyNow.setOnClickListener{ addToCart(true) }; btnAllSpecifications.setOnClickListener{startActivity(Intent(this,HomeSpecifyActivity::class.java).putExtra("productRegister",productRegister))} }
    private fun loadProduct(){ if(productRegister<=0)return; FirebaseFirestore.getInstance().collection("product").document(productRegister.toString()).get().addOnSuccessListener{ doc-> product=doc.toObject(ProductModel::class.java); showProduct(); loadImages() } }
    private fun showProduct(){ val p=product?:return; txtName.text=p.name?:"Producto"; txtPrice.text="$ ${String.format("%,.0f", p.price)}"; txtDetail.text=p.detail?:"Sin descripción"; if(!p.storefire.isNullOrBlank()) Glide.with(this).load(p.storefire).placeholder(R.drawable.ic_shops).into(imgProduct) else imgProduct.setImageResource(R.drawable.ic_shops) }
    private fun loadImages(){ FirebaseFirestore.getInstance().collection("image").whereEqualTo("idProduct",productRegister).get().addOnSuccessListener{ result -> relatedImages.removeAllViews(); result.documents.mapNotNull{it.toObject(ImageModel::class.java)}.forEach{ img -> val view=ImageView(this).apply{layoutParams=LinearLayout.LayoutParams(dp(78),dp(78)).apply{setMargins(0,0,dp(10),0)};scaleType=ImageView.ScaleType.CENTER_CROP;setBackgroundColor(0xFFF7F8FA.toInt())}; if(!img.storefire.isNullOrBlank()) Glide.with(this).load(img.storefire).into(view); relatedImages.addView(view) } } }
    private fun addToCart(openCart:Boolean){ val p=product?:return; CartManager.add(this, CartItem(p.register,p.name?:"Producto",p.detail?:"",p.price,p.storefire?:"",1)); Toast.makeText(this,"Producto agregado al carrito",Toast.LENGTH_SHORT).show(); if(openCart) startActivity(Intent(this,BuyerCartShopActivity::class.java)) }
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}
