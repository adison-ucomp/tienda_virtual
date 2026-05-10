package com.example.tiendavirtual.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    val register: Long? = null,
    val name: String? = null,
    val detail: String? = null,

    @SerializedName("url_image")
    val urlImage: String? = null,

    @SerializedName("id_category")
    val idCategory: Long? = null,

    @SerializedName("id_shop")
    val idShop: Long? = null
)