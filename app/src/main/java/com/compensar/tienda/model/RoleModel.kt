package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Role.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 */
data class RoleModel(
    val register: Long = 0,
    val name: String? = null
)

