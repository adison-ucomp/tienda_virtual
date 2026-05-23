package com.compensar.tienda.ui.buyer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.model.AddressModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.model.common.FirestoreSelectHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

/**
 * Clase [BuyerMapsActivity].
 *
 * Responsable de la logica asociada al pantalla del flujo de comprador.
 */
class BuyerMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap; private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnBack: TextView; private lateinit var btnUseCurrentLocation: TextView; private lateinit var btnSaveAddress: Button; private lateinit var btnSearchAddress: TextView
    private lateinit var txtSearchAddress: EditText; private lateinit var txtAddressDetail: EditText; private lateinit var txtLabel: EditText; private lateinit var spnUbication: Spinner
    private val db=FirebaseFirestore.getInstance(); private val collection=db.collection("address"); private var register:Long=0; private var generatedRegister:Long=0; private var selectedLatLng: LatLng?=null
    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { p -> if (p[Manifest.permission.ACCESS_FINE_LOCATION] == true || p[Manifest.permission.ACCESS_COARSE_LOCATION] == true) { enableMyLocationLayer(); useCurrentLocation() } else Toast.makeText(this,"Permiso de ubicación denegado",Toast.LENGTH_SHORT).show() }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); enableEdgeToEdge(); setContentView(R.layout.buyer_maps); fusedLocationClient=LocationServices.getFusedLocationProviderClient(this); register=intent.getLongExtra("register",0); applyWindowInsets(); initViews(); initEvents(); loadUbications(); loadNextRegister(); initMap() }
    private fun applyWindowInsets(){ ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v,insets-> val b=insets.getInsets(WindowInsetsCompat.Type.systemBars()); v.setPadding(b.left,b.top,b.right,b.bottom); insets } }
    private fun initViews(){ btnBack=findViewById(R.id.btnBack); btnUseCurrentLocation=findViewById(R.id.btnUseCurrentLocation); btnSaveAddress=findViewById(R.id.btnSaveAddress); btnSearchAddress=findViewById(R.id.btnSearchAddress); txtSearchAddress=findViewById(R.id.txtSearchAddress); txtAddressDetail=findViewById(R.id.txtAddressDetail); txtLabel=findViewById(R.id.txtLabel); spnUbication=findViewById(R.id.spnUbication) }
    private fun initEvents(){ btnBack.setOnClickListener{finish()}; btnUseCurrentLocation.setOnClickListener{checkLocationPermission(true)}; btnSaveAddress.setOnClickListener{saveAddress()}; btnSearchAddress.setOnClickListener{searchAddressOnMap()}; txtSearchAddress.setOnEditorActionListener{_, actionId, _ -> if(actionId==EditorInfo.IME_ACTION_SEARCH){searchAddressOnMap(); true}else false} }
    private fun loadUbications(selected:Long=0){ FirestoreSelectHelper.load(this, spnUbication, "ubication", listOf("name"), selected) }
    private fun loadNextRegister(){ collection.orderBy("register",Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener{ generatedRegister=(it.documents.firstOrNull()?.getLong("register")?:0L)+1L; if(register>0) loadAddressForEdit() } }
    private fun initMap(){ val mapFragment=supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment; mapFragment.getMapAsync(this) }
    override fun onMapReady(map: GoogleMap){ googleMap=map; googleMap.uiSettings.isZoomControlsEnabled=true; googleMap.uiSettings.isMyLocationButtonEnabled=false; val bogota=LatLng(4.7110,-74.0721); googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota,12f)); googleMap.setOnCameraIdleListener{ val target=googleMap.cameraPosition.target; selectedLatLng=target; getAddressFromCoordinates(target.latitude,target.longitude,false) }; checkLocationPermission(false) }
    private fun checkLocationPermission(use:Boolean){ val fine=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED; val coarse=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED; if(fine||coarse){ enableMyLocationLayer(); if(use || register<=0) useCurrentLocation() } else requestLocationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)) }
    private fun enableMyLocationLayer(){ if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) googleMap.isMyLocationEnabled=true }
    private fun useCurrentLocation(){ if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) return; fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener{ location -> if(location==null){Toast.makeText(this,"No se pudo obtener ubicación actual",Toast.LENGTH_SHORT).show();return@addOnSuccessListener}; moveMap(LatLng(location.latitude,location.longitude),"Mi ubicación actual",true) }.addOnFailureListener{Toast.makeText(this,"Error obteniendo ubicación",Toast.LENGTH_SHORT).show()} }
    private fun moveMap(latLng:LatLng,title:String,fill:Boolean){ selectedLatLng=latLng; googleMap.clear(); googleMap.addMarker(MarkerOptions().position(latLng).title(title)); googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f)); getAddressFromCoordinates(latLng.latitude,latLng.longitude,fill) }
    private fun getAddressFromCoordinates(latitude:Double, longitude:Double, forceFill:Boolean){ val geocoder=Geocoder(this, Locale("es","CO")); try{ if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){ geocoder.getFromLocation(latitude,longitude,1){ addresses -> if(addresses.isNotEmpty()) runOnUiThread{ fillAddress(addresses[0].getAddressLine(0)?:"", forceFill) } } } else { @Suppress("DEPRECATION") val addresses=geocoder.getFromLocation(latitude,longitude,1); if(!addresses.isNullOrEmpty()) fillAddress(addresses[0].getAddressLine(0)?:"", forceFill) } }catch(_:Exception){} }
    private fun fillAddress(full:String, force:Boolean){ txtSearchAddress.setText(full); if(force || txtAddressDetail.text.isNullOrBlank()) txtAddressDetail.setText(full) }
    private fun searchAddressOnMap(){ val query=txtSearchAddress.text.toString().trim(); if(query.isEmpty()){Toast.makeText(this,"Escribe una dirección",Toast.LENGTH_SHORT).show();return}; try{ val geocoder=Geocoder(this, Locale("es","CO")); @Suppress("DEPRECATION") val list=geocoder.getFromLocationName(query,1); if(list.isNullOrEmpty()){Toast.makeText(this,"No se encontró la dirección",Toast.LENGTH_SHORT).show();return}; val a=list[0]; moveMap(LatLng(a.latitude,a.longitude),query,true) }catch(e:Exception){Toast.makeText(this,"Error buscando dirección",Toast.LENGTH_SHORT).show()} }
    private fun loadAddressForEdit(){ collection.document(register.toString()).get().addOnSuccessListener{ doc -> val data=doc.toObject(AddressModel::class.java)?:return@addOnSuccessListener; txtAddressDetail.setText(data.address?:""); txtSearchAddress.setText(data.address?:""); txtLabel.setText(data.label?:""); loadUbications(data.id_ubication); searchAddressOnMap() } }
    private fun saveAddress(){ val user=SessionManager.getRegister(this); if(user<=0){Toast.makeText(this,"Debes iniciar sesión",Toast.LENGTH_SHORT).show();return}; val address=txtAddressDetail.text.toString().trim(); val label=txtLabel.text.toString().trim(); val ubication=FirestoreSelectHelper.getSelectedId(spnUbication); if(address.isEmpty()||label.isEmpty()||ubication==null){Toast.makeText(this,"Completa ubicación, dirección y etiqueta",Toast.LENGTH_SHORT).show();return}; val finalRegister=if(register>0) register else generatedRegister; val data=AddressModel(finalRegister,address,label,ubication,user); collection.document(finalRegister.toString()).set(data).addOnSuccessListener{Toast.makeText(this,"Dirección guardada",Toast.LENGTH_SHORT).show();finish()}.addOnFailureListener{Toast.makeText(this,"Error: ${it.message}",Toast.LENGTH_LONG).show()} }
}

