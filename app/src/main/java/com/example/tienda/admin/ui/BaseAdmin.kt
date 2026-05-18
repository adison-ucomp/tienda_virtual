package com.example.tienda.admin.ui

import android.content.Intent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tienda.R
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout


open class BaseAdmin : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var btnMenu: ImageView

    fun setupDrawer() {

        drawerLayout = findViewById(R.id.drawerLayout)

        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {

            drawerLayout.openDrawer(GravityCompat.START)

        }

    }

    protected fun configurarBotonBack() {

        val btnBack = findViewById<ImageView>(R.id.btnMenu)

        btnBack?.setOnClickListener {

            finish()

        }

    }

    fun configurarBottomNavigation() {

        // Home
        val btnHome = findViewById<LinearLayout>(R.id.btnHome)

        btnHome?.setOnClickListener {

            val intent = Intent(this, DashboardAdmin::class.java)
            startActivity(intent)

        }

        // Profile
        val btnProfile = findViewById<LinearLayout>(R.id.btnProfile)

        btnProfile?.setOnClickListener {

            val intent = Intent(this, ProfileAdmin::class.java)
            startActivity(intent)

        }


    }

}