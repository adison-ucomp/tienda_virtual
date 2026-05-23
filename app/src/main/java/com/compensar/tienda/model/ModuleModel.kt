package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Module.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 * @property model Nombre tecnico del modulo o entidad relacionada.
 * @property detail Descripcion o detalle complementario.
 * @property state Estado logico de la entidad.
 */
data class ModuleModel(
    val register: Long = 0,
    val name: String? = null,
    val model: String? = null,
    val detail: String? = null,
    val state: Boolean? = null
)

