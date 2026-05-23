package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad Purchase.
 * @property register Identificador unico del registro.
 * @property amount Campo de datos de la entidad.
 * @property value Campo de datos de la entidad.
 * @property total Valor total calculado.
 * @property idOrder Identificador de referencia a otra entidad.
 * @property idProduct Identificador de referencia a otra entidad.
 * @property idUser Identificador de referencia a otra entidad.
 */
data class PurchaseModel(
    val register: Long = 0,
    val amount: Int? = null,
    val value: Double? = null,
    val total: Double? = null,
    val idOrder: Long = 0,
    val idProduct: Long = 0,
    val idUser: Long = 0
)

