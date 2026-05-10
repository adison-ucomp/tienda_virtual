package com.example.tiendavirtual.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Vendedores
@Entity(tableName = "seller")
data class SellerEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val company: String? = null,

    val nit: String? = null,

    val address: String? = null,

    @ColumnInfo(name = "id_user")
    val idUser: Long
)