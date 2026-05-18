package com.example.tienda.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpecifyDto(
    val register: Long? = null,
    val name: String? = null,
    val detail: String? = null,

    @SerializedName("id_product")
    val idProduct: Long? = null
)