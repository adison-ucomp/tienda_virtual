package com.compensar.tienda.domain.model

data class ProductModel(
    val register: Long = 0,
    val name: String? = null,
    val detail: String? = null,
    val storefire: String? = null,
    val idCategory: Long = 0,
    val idShop: Long = 0
)