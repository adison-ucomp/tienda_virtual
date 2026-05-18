package com.example.tienda.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SellerDto(
    val register: Long? = null,
    val company: String? = null,
    val nit: String? = null,
    val address: String? = null,

    @SerializedName("id_user")
    val idUser: Long? = null
)