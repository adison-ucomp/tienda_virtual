package com.example.tienda.domain.model

data class SellerModel(
    val register: Long = 0,
    val company: String? = null,
    val nit: String? = null,
    val address: String? = null,
    val idUser: Long = 0
)