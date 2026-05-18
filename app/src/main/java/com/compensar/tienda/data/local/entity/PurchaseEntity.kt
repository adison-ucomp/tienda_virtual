package com.compensar.tienda.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Compras
@Entity(tableName = "purchase")
data class PurchaseEntity(

    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val date: String,

    val hour: String? = null,

    val amount: Int? = null,

    val value: Double? = null,

    val total: Double? = null,

    @ColumnInfo(name = "id_product")
    val idProduct: Long,

    @ColumnInfo(name = "id_method")
    val idMethod: Long,

    @ColumnInfo(name = "id_gangway")
    val idGangway: Long,

    @ColumnInfo(name = "id_user")
    val idUser: Long
)