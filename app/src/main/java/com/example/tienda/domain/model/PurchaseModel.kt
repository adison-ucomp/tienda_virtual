package com.example.tienda.domain.model

data class PurchaseModel(
    val register: Long = 0,
    val date: String = "",
    val hour: String? = null,
    val amount: Int? = null,
    val value: Double? = null,
    val total: Double? = null,
    val idProduct: Long = 0,
    val idMethod: Long = 0,
    val idGangway: Long = 0,
    val idUser: Long = 0
)