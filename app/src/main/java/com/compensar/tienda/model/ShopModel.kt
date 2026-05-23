package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Shop.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 * @property storefire URL del recurso almacenado en Firebase Storage.
 * @property idSeller Identificador de referencia a otra entidad.
 */
data class ShopModel(
    val register: Long = 0,
    val name: String? = null,
    val storefire: String? = null,
    val idSeller: Long = 0
)

