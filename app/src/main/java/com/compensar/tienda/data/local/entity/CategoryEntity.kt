package com.compensar.tienda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Categorias
@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null
)