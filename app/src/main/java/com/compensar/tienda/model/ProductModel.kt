package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Product.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 * @property detail Descripcion o detalle complementario.
 * @property stock Cantidad disponible en inventario.
 * @property reserved Cantidad reservada del inventario.
 * @property price Precio unitario.
 * @property storefire URL del recurso almacenado en Firebase Storage.
 * @property idCategory Identificador de referencia a otra entidad.
 * @property idShop Identificador de referencia a otra entidad.
 */
data class ProductModel(
    val register: Long = 0,
    val name: String? = null,
    val detail: String? = null,
    val stock: Int = 0,
    val reserved: Int = 0,
    val price: Double = 0.0,
    val storefire: String? = null,
    val idCategory: Long = 0,
    val idShop: Long = 0
)

