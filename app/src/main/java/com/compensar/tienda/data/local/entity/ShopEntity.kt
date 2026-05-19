package com.compensar.tienda.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Tiendas
@Entity(tableName = "shop")
data class ShopEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null,
    
    val storefire: String? = null,

    @ColumnInfo(name = "id_seller")
    val idSeller: Long
)