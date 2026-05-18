package com.example.tienda.admin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tienda.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tienda.admin.adapter.UserAdapter
import com.example.tienda.admin.model.User
import android.content.Intent
import android.widget.LinearLayout

class UserManagementAdmin : BaseAdmin() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var listUsers: ArrayList<User>
    private lateinit var btnAddUser: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_users)

        configurarBottomNavigation()
        configurarBotonBack()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAddUser = findViewById(R.id.btnAddUser)
        btnAddUser.setOnClickListener {

            val intent = Intent(this, AddUserAdmin::class.java)
            startActivity(intent)

        }

        recyclerUsers = findViewById(R.id.recyclerUsers)

        recyclerUsers.layoutManager = LinearLayoutManager(this)


        listUsers = ArrayList()

        listUsers.add(
            User(
                "000001",
                "Administrador",
                "Daniela",
                "Fajardo",
                "dfajardop@ucom"
            )
        )

        listUsers.add(
            User(
                "000002",
                "Vendedor",
                "Adison",
                "Jimenez",
                "dfajardop@ucom"
            )
        )

        listUsers.add(
            User(
                "000003",
                "Cliente",
                "Carlos",
                "Fajardo",
                "dfajardop@ucom"
            )
        )

        userAdapter = UserAdapter(listUsers)

        recyclerUsers.adapter = userAdapter

    }
}