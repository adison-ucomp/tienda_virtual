package com.compensar.tienda.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.report.ReportDataSaleActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase [AdminReportDataActivity].
 *
 * Responsable de la logica asociada al pantalla del panel administrativo.
 */
class AdminReportDataActivity : AppCompatActivity() {

    private lateinit var cardReportSale: CardView
    private lateinit var cardUserCount: CardView
    private lateinit var cardShopCount: CardView
    private lateinit var textUserCount: TextView
    private lateinit var textShopCount: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_report_data)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadCounters()
    }

    override fun onResume() {
        super.onResume()
        loadCounters()
    }

    private fun initViews() {
        cardReportSale = findViewById(R.id.cardReportSale)
        cardUserCount = findViewById(R.id.cardUserCount)
        cardShopCount = findViewById(R.id.cardShopCount)
        textUserCount = findViewById(R.id.textUserCount)
        textShopCount = findViewById(R.id.textShopCount)
    }

    private fun initEvents() {
        cardReportSale.setOnClickListener {
            val intent = Intent(this, ReportDataSaleActivity::class.java)
            startActivity(intent)
        }

        cardUserCount.setOnClickListener {
            val intent = Intent(this, AdminUserListActivity::class.java)
            startActivity(intent)
        }

        cardShopCount.setOnClickListener {
            val intent = Intent(this, AdminShopListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadCounters() {
        db.collection("user")
            .get()
            .addOnSuccessListener { result ->
                textUserCount.text = result.size().toString()
            }
            .addOnFailureListener {
                textUserCount.text = "0"
            }

        db.collection("shop")
            .get()
            .addOnSuccessListener { result ->
                textShopCount.text = result.size().toString()
            }
            .addOnFailureListener {
                textShopCount.text = "0"
            }
    }
}

