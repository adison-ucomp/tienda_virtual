package com.example.tiendavirtual.admin.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendavirtual.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendavirtual.admin.adapter.ShopAdapter
import com.example.tiendavirtual.admin.model.Shop

class ShopManagementAdmin : BaseAdmin() {

    private lateinit var recyclerShops: RecyclerView
    private lateinit var listShops: ArrayList<Shop>
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var btnAddShop: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_shops)

        configurarBottomNavigation()
        configurarBotonBack()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAddShop = findViewById(R.id.btnAddShop)
        btnAddShop.setOnClickListener {

            val intent = Intent(this, AddShopAdmin::class.java)
            startActivity(intent)

        }

        recyclerShops = findViewById(R.id.recyclerShop)

        recyclerShops.layoutManager = LinearLayoutManager(this)


        listShops = ArrayList()

        listShops.add(
            Shop(
                "123456789",
                "EMPTIO STORE",
                "Cra 10 #20-30",

                "001",
                "Juan",
                "Perez",
                "juan@gmail.com"
            )
        )

        listShops.add(
            Shop(
                "987654321",
                "TECH SHOP",
                "Calle 50 #10-20",

                "002",
                "Daniela",
                "Fajardo",
                "daniela@gmail.com"
            )
        )

        shopAdapter = ShopAdapter(listShops)

        recyclerShops.adapter = shopAdapter

    }
}