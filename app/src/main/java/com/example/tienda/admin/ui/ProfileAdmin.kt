package com.example.tienda.admin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tienda.R
import android.content.Intent
import android.widget.LinearLayout

class ProfileAdmin : BaseAdmin() {

    private lateinit var btnEditProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_admin)

        configurarBottomNavigation()
        setupDrawer()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {

            val intent = Intent(this, EditProfileAdmin::class.java)
            startActivity(intent)

        }
    }
}