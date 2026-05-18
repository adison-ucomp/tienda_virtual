package com.example.tienda.admin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tienda.R
import android.widget.EditText

class EditShopAdmin : BaseAdmin() {

    private lateinit var etNit: EditText
    private lateinit var etCompany: EditText
    private lateinit var etAdress: EditText
    private lateinit var etName: EditText
    private lateinit var etSurnames: EditText
    private lateinit var etEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_edit_shop)

        configurarBottomNavigation()
        configurarBotonBack()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNit = findViewById(R.id.etNit)
        etCompany = findViewById(R.id.etCompany)
        etAdress = findViewById(R.id.etAdress)
        etName = findViewById(R.id.etName)
        etSurnames = findViewById(R.id.etSurnames)
        etEmail = findViewById(R.id.etEmail)

        etNit.setText(intent.getStringExtra("nit"))
        etCompany.setText(intent.getStringExtra("company"))
        etAdress.setText(intent.getStringExtra("adress"))
        etName.setText(intent.getStringExtra("name"))
        etSurnames.setText(intent.getStringExtra("surnames"))
        etEmail.setText(intent.getStringExtra("email"))
    }
}