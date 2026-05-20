package com.compensar.tienda.model

data class PurchaseModel(
    val register: Long = 0,
    val amount: Int? = null,
    val value: Double? = null,
    val total: Double? = null,
    val idGangway: Long = 0,
    val idOrder: Long = 0,
    val idProduct: Long = 0,
    val idUser: Long = 0
)
