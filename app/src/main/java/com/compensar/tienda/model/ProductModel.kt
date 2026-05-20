package com.compensar.tienda.model

data class ProductModel(
    val register: Long = 0,
    val name: String? = null,
    val detail: String? = null,
    val stock: Int = 0,
    val price: Double = 0.0,
    val storefire: String? = null,
    val idCategory: Long = 0,
    val idShop: Long = 0
)
