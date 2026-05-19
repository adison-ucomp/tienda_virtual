package com.compensar.tienda.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Imagenes
@Entity(tableName = "image")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val storefire: String? = null,

    @ColumnInfo(name = "id_product")
    val idProduct: Long
)