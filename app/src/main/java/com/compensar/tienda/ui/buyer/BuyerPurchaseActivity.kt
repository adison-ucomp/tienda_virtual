package com.compensar.tienda.ui.buyer

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.AddressModel
import com.compensar.tienda.ui.common.CartManager
import com.compensar.tienda.ui.common.SessionManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

class BuyerPurchaseActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btnBack: TextView
    private lateinit var btnEditAddress: TextView
    private lateinit var btnConfirmPurchase: Button
    private lateinit var spnAddresses: Spinner
    private lateinit var txtAddress: TextView
    private lateinit var txtCity: TextView
    private lateinit var txtContact: TextView
    private lateinit var txtProductsLabel: TextView
    private lateinit var txtProductsValue: TextView
    private lateinit var txtShippingValue: TextView
    private lateinit var txtTaxesValue: TextView
    private lateinit var txtTotalValue: TextView

    private val addresses = mutableListOf<AddressModel>()
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_purchase)

        applyWindowInsets()
        initViews()
        initEvents()
        renderSummary()
        initMap()
        loadAddresses()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEditAddress = findViewById(R.id.btnEditAddress)
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase)
        spnAddresses = findViewById(R.id.spnAddresses)
        txtAddress = findViewById(R.id.txtAddress)
        txtCity = findViewById(R.id.txtCity)
        txtContact = findViewById(R.id.txtContact)
        txtProductsLabel = findViewById(R.id.txtProductsLabel)
        txtProductsValue = findViewById(R.id.txtProductsValue)
        txtShippingValue = findViewById(R.id.txtShippingValue)
        txtTaxesValue = findViewById(R.id.txtTaxesValue)
        txtTotalValue = findViewById(R.id.txtTotalValue)
    }

    private fun initEvents() {
        btnBack.setOnClickListener { finish() }

        btnEditAddress.setOnClickListener {
            startActivity(Intent(this, BuyerAddressActivity::class.java))
        }

        btnConfirmPurchase.setOnClickListener {
            Toast.makeText(this, "Compra lista para pasarela de pagos", Toast.LENGTH_SHORT).show()
        }

        spnAddresses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position in addresses.indices) {
                    selectAddress(addresses[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun initMap() {
        val fragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        fragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(4.7110, -74.0721), 12f))
        if (addresses.isNotEmpty()) {
            selectAddress(addresses.first())
        }
    }

    private fun loadAddresses() {
        val userRegister = SessionManager.getRegister(this)
        if (userRegister <= 0) {
            txtAddress.text = "Debes iniciar sesión para seleccionar una dirección"
            return
        }

        FirebaseFirestore.getInstance()
            .collection("address")
            .whereEqualTo("idUser", userRegister)
            .orderBy("register", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                addresses.clear()
                addresses.addAll(result.documents.mapNotNull { it.toObject(AddressModel::class.java) })

                if (addresses.isEmpty()) {
                    txtAddress.text = "No tienes direcciones registradas"
                    txtCity.text = "Agrega una dirección antes de continuar"
                    txtContact.text = ""
                    spnAddresses.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        listOf("Sin direcciones")
                    )
                    return@addOnSuccessListener
                }

                val labels = addresses.map { address ->
                    val label = address.label?.takeIf { it.isNotBlank() } ?: "Dirección"
                    "$label - ${address.address ?: ""}"
                }

                spnAddresses.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    labels
                )

                spnAddresses.setSelection(0)
                selectAddress(addresses.first())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando direcciones: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun selectAddress(address: AddressModel) {
        val label = address.label?.takeIf { it.isNotBlank() } ?: "Dirección"
        val value = address.address?.takeIf { it.isNotBlank() } ?: "Sin dirección"

        txtAddress.text = value
        txtCity.text = label
        txtContact.text = "Comprador · Dirección guardada"
        showAddressOnMap(value)
    }

    private fun showAddressOnMap(address: String) {
        if (address.isBlank()) return

        try {
            val geocoder = Geocoder(this, Locale("es", "CO"))
            @Suppress("DEPRECATION")
            val result = geocoder.getFromLocationName(address, 1)

            if (!result.isNullOrEmpty()) {
                val latLng = LatLng(result[0].latitude, result[0].longitude)
                googleMap?.clear()
                googleMap?.addMarker(MarkerOptions().position(latLng).title(address))
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        } catch (_: Exception) {
        }
    }

    private fun renderSummary() {
        val items = CartManager.getItems(this)
        val count = items.sumOf { it.quantity }
        val subtotal = CartManager.subtotal(this)
        val shipping = 0.0
        val taxes = 0.0
        val total = subtotal + shipping + taxes

        txtProductsLabel.text = "Productos ($count)"
        txtProductsValue.text = "$ ${String.format("%,.0f", subtotal)}"
        txtShippingValue.text = "$ ${String.format("%,.0f", shipping)}"
        txtTaxesValue.text = "$ ${String.format("%,.0f", taxes)}"
        txtTotalValue.text = "$ ${String.format("%,.0f", total)}"
    }
}
