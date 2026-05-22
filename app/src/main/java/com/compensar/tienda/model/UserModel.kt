package com.compensar.tienda.model

data class UserModel(
    val register: Long = 0,
    val names: String? = null,
    val srnms: String? = null,
    val email: String? = null,
    val password: String? = null,
    val storefire: String? = null,
    val idRole: Long = 0,
    val biometric: Boolean = false
)
