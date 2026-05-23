package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Specify.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 * @property detail Descripcion o detalle complementario.
 * @property idProduct Identificador de referencia a otra entidad.
 */
data class SpecifyModel(
    val register: Long = 0,
    val name: String? = null,
    val detail: String? = null,
    val idProduct: Long = 0
)

