package com.example.tiendavirtual.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Especificaciones
@Entity(tableName = "specify")
data class SpecifyEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null,

    val detail: String? = null,

    @ColumnInfo(name = "id_product")
    val idProduct: Long
)