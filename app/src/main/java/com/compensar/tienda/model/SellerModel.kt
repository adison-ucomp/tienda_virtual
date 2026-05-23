package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Seller.
 * @property register Identificador unico del registro.
 * @property company Nombre de la empresa o razon social.
 * @property nit Identificador tributario de la empresa.
 * @property address Direccion o ubicacion textual.
 * @property idUser Identificador de referencia a otra entidad.
 */
data class SellerModel(
    val register: Long = 0,
    val company: String? = null,
    val nit: String? = null,
    val address: String? = null,
    val idUser: Long = 0
)

