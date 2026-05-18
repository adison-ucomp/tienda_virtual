package com.example.tienda.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PurchaseDto(
    val register: Long? = null,
    val date: String? = null,
    val hour: String? = null,
    val amount: Int? = null,
    val value: Double? = null,
    val total: Double? = null,

    @SerializedName("id_product")
    val idProduct: Long? = null,

    @SerializedName("id_method")
    val idMethod: Long? = null,

    @SerializedName("id_gangway")
    val idGangway: Long? = null,

    @SerializedName("id_user")
    val idUser: Long? = null
)