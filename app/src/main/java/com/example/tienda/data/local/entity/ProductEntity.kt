package com.example.tienda.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Productos
@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null,

    val detail: String? = null,

    @ColumnInfo(name = "url_image")
    val urlImage: String? = null,

    @ColumnInfo(name = "id_category")
    val idCategory: Long,

    @ColumnInfo(name = "id_shop")
    val idShop: Long
)