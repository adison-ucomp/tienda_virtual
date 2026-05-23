package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Address.
 * @property register Identificador unico del registro.
 * @property address Direccion o ubicacion textual.
 * @property label Etiqueta de referencia para el registro.
 * @property id_ubication Identificador de referencia a otra entidad.
 * @property idUser Identificador de referencia a otra entidad.
 */
data class AddressModel(
    val register: Long = 0,
    val address: String? = null,
    val label: String? = null,
    val id_ubication: Long = 0,
    val idUser: Long = 0
)

