package com.compensar.tienda.domain.model

data class UserModel(
    val register: Long = 0,
    val names: String? = null,
    val srnms: String? = null,
    val email: String? = null,
    val password: String? = null,
    val idRole: Long = 0
)