package com.example.tiendavirtual.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Medios de Pago
@Entity(tableName = "payment")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null
)