package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Image.
 * @property register Identificador unico del registro.
 * @property storefire URL del recurso almacenado en Firebase Storage.
 * @property idProduct Identificador de referencia a otra entidad.
 */
data class ImageModel(
    val register: Long = 0,
    val storefire: String? = null,
    val idProduct: Long = 0
)

