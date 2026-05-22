package com.compensar.tienda.model

data class TradeModel(
    val register: Long = 0,
    val api: String? = null,
    val state: String? = null,
    val idGateway: Long = 0,
    val idOrder: Long = 0
)
