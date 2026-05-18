package com.example.tienda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Roles
@Entity(tableName = "role")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val name: String? = null
)