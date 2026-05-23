package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Trade.
 * @property register Identificador unico del registro.
 * @property api Campo de datos de la entidad.
 * @property state Estado logico de la entidad.
 * @property reference Campo de datos de la entidad.
 * @property idGateway Identificador de referencia a otra entidad.
 * @property idOrder Identificador de referencia a otra entidad.
 */
data class TradeModel(
    val register: Long = 0,
    val api: String? = null,
    val state: String? = null,
    val reference: String? = null,
    val idGateway: Long = 0,
    val idOrder: Long = 0
)

