package com.compensar.tienda.ui.platform

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.compensar.tienda.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class BuyerMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var btnBack: TextView
    private lateinit var btnUseCurrentLocation: TextView
    private lateinit var btnSaveAddress: Button

    private lateinit var txtSearchAddress: EditText
    private lateinit var txtLocationName: EditText
    private lateinit var txtAddressDetail: EditText

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.buyer_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        applyWindowInsets()
        initViews()
        initEvents()
        initMap()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnUseCurrentLocation = findViewById(R.id.btnUseCurrentLocation)
        btnSaveAddress = findViewById(R.id.btnSaveAddress)

        txtSearchAddress = findViewById(R.id.txtSearchAddress)
        txtLocationName = findViewById(R.id.txtLocationName)
        txtAddressDetail = findViewById(R.id.txtAddressDetail)
    }

    private fun initEvents() {
        btnBack.setOnClickListener {
            finish()
        }

        btnUseCurrentLocation.setOnClickListener {
            checkLocationPermission()
        }

        btnSaveAddress.setOnClickListener {
            Toast.makeText(this, "Dirección guardada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val bogota = LatLng(4.7110, -74.0721)

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(bogota, 12f)
        )

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            enableMyLocationLayer()
            getCurrentLocation()
        } else {
            requestLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun enableMyLocationLayer() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            return
        }

        googleMap.isMyLocationEnabled = true
    }

    private fun getCurrentLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            Toast.makeText(this, "No hay permiso de ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val latitude = location.latitude
                val longitude = location.longitude
                val currentLatLng = LatLng(latitude, longitude)

                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions()
                        .position(currentLatLng)
                        .title("Mi ubicación actual")
                )

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f)
                )

                getAddressFromCoordinates(latitude, longitude)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error obteniendo ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale("es", "CO"))

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val fullAddress = addresses[0].getAddressLine(0) ?: ""

                        runOnUiThread {
                            fillAddressFields(fullAddress)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "No se encontró dirección", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val fullAddress = addresses[0].getAddressLine(0) ?: ""
                    fillAddressFields(fullAddress)
                } else {
                    Toast.makeText(this, "No se encontró dirección", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (exception: Exception) {
            Toast.makeText(this, "Error convirtiendo ubicación a dirección", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillAddressFields(fullAddress: String) {
        txtSearchAddress.setText(fullAddress)

        if (txtLocationName.text.isNullOrBlank()) {
            txtLocationName.setText("Mi ubicación")
        }

        if (txtAddressDetail.text.isNullOrBlank()) {
            txtAddressDetail.setText(fullAddress)
        }
    }
}