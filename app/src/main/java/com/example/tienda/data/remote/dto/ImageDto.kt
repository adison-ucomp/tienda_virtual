package com.example.tienda.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageDto(
    val register: Long? = null,

    @SerializedName("url_image")
    val urlImage: String? = null,

    @SerializedName("id_product")
    val idProduct: Long? = null
)