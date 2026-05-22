package com.compensar.tienda.model

data class OrderModel(
    val register: Long = 0,
    val address: String? = null,
    val reference: String? = null,
    val total: Double? = null,
    val date: String? = null,
    val hour: String? = null,
    val idShop: Long = 0,
    val idShipment: Long = 0,
    val idUser: Long = 0
)
