package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Order.
 * @property register Identificador unico del registro.
 * @property address Direccion o ubicacion textual.
 * @property reference Campo de datos de la entidad.
 * @property total Valor total calculado.
 * @property date Campo de datos de la entidad.
 * @property hour Campo de datos de la entidad.
 * @property idTrade Identificador de referencia a otra entidad.
 * @property idShop Identificador de referencia a otra entidad.
 * @property idPayment Identificador de referencia a otra entidad.
 * @property idShipment Identificador de referencia a otra entidad.
 * @property idUser Identificador de referencia a otra entidad.
 */
data class OrderModel(
    val register: Long = 0,
    val address: String? = null,
    val reference: String? = null,
    val total: Double? = null,
    val date: String? = null,
    val hour: String? = null,
    val idTrade: Long = 0,
    val idShop: Long = 0,
    val idPayment: Long = 0,
    val idShipment: Long = 0,
    val idUser: Long = 0
)

