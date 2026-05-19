package com.compensar.tienda.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageDto(
    val register: Long? = null,
    val storefire: String? = null,

    @SerializedName("id_product")
    val idProduct: Long? = null
)