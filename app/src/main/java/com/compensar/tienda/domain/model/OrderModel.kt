package com.compensar.tienda.domain.model

data class OrderModel(
    val register: Long = 0,
    val address: String? = null,
    val reference: String? = null,
    val total: Double? = null,
    val date: String? = null,
    val hour: String? = null,
    val idShipment: Long = 0,
    val idUser: Long = 0
)
