package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Ubication.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 */
data class UbicationModel(
    val register: Long = 0,
    val name: String? = null
)

