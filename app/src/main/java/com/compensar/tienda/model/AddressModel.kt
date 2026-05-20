package com.compensar.tienda.model

data class AddressModel(
    val register: Long = 0,
    val address: String? = null,
    val label: String? = null,
    val id_ubication: Long = 0,
    val idUser: Long = 0
)
