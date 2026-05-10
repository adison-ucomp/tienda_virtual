package com.example.tiendavirtual.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Usuarios
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val register: Long = 0,

    val names: String? = null,

    val srnms: String? = null,

    val email: String? = null,

    val password: String? = null,

    @ColumnInfo(name = "id_role")
    val idRole: Long
)