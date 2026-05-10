package com.example.tiendavirtual.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Pasarelas de Pago
@Entity(tableName = "gateway")
data class GatewayEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null
)