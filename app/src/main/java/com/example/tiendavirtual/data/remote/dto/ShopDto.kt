package com.example.tiendavirtual.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShopDto(
    val register: Long? = null,
    val name: String? = null,

    @SerializedName("id_seller")
    val idSeller: Long? = null
)