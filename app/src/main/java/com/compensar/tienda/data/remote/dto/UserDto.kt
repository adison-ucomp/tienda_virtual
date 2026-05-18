package com.compensar.tienda.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val register: Long? = null,
    val names: String? = null,
    val srnms: String? = null,
    val email: String? = null,
    val password: String? = null,

    @SerializedName("id_role")
    val idRole: Long? = null
)