package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Category.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 * @property storefire URL del recurso almacenado en Firebase Storage.
 */
data class CategoryModel(
    val register: Long = 0,
    val name: String? = null,
    val storefire: String? = null
)

