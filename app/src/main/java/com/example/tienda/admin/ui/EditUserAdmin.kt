package com.example.tienda.admin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tienda.R
import android.widget.EditText

class EditUserAdmin : BaseAdmin() {

    private lateinit var etName: EditText
    private lateinit var etSurnames: EditText
    private lateinit var etEmail: EditText

    private lateinit var etRol: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_edit_user)

        configurarBottomNavigation()
        configurarBotonBack()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etRol = findViewById(R.id.etRol)
        etName = findViewById(R.id.etName)
        etSurnames = findViewById(R.id.etSurnames)
        etEmail = findViewById(R.id.etEmail)

        val name = intent.getStringExtra("nombre")
        val surnames = intent.getStringExtra("apellido")
        val email = intent.getStringExtra("correo")
        val rol = intent.getStringExtra("rol")

        etName.setText(name)
        etSurnames.setText(surnames)
        etEmail.setText(email)
        etRol.setText(rol)

    }
}