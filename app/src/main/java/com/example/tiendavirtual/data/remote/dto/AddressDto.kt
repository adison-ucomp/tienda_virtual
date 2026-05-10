package com.example.tiendavirtual.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressDto(
    val register: Long? = null,
    val address: String? = null,

    @SerializedName("id_user")
    val idUser: Long? = null
)