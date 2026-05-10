package com.example.tiendavirtual.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Direcciones de Envio
@Entity(tableName = "address_send")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val address: String? = null,

    @ColumnInfo(name = "id_user")
    val idUser: Long
)