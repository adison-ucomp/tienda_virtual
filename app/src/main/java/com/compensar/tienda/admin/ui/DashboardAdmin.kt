package com.compensar.tienda.admin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.compensar.tienda.R
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class DashboardAdmin : BaseAdmin() {
    private lateinit var cardUsers: CardView
    private lateinit var cardShops: CardView
    private lateinit var cardReports: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        configurarBottomNavigation()
        setupDrawer()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cardUsers = findViewById(R.id.cardUsers)
        cardUsers.setOnClickListener {
            val intent = Intent(this, UserManagementAdmin::class.java)
            startActivity(intent)
        }

        cardShops = findViewById(R.id.cardShops)
        cardShops.setOnClickListener {
            val intent = Intent(this, ShopManagementAdmin::class.java)
            startActivity(intent)
        }

        cardReports = findViewById(R.id.cardReports)
        cardReports.setOnClickListener {
            val intent = Intent(this, ReportAdmin::class.java)
            startActivity(intent)
        }

    }
}