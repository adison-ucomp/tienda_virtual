package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Payment.
 * @property register Identificador unico del registro.
 * @property name Nombre principal de la entidad.
 */
data class PaymentModel(
    val register: Long = 0,
    val name: String? = null
)

