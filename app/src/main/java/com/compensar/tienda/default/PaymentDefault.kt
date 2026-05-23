package com.compensar.tienda.default

import com.compensar.tienda.model.PaymentModel

/**
 * Metodos de pago iniciales.
 */
object PaymentDefault {

    /**
     * Retorna el listado de metodos de pago soportados por defecto.
     */
    fun getAll(): List<PaymentModel> {
        return listOf(
            PaymentModel(register = 1, name = "Debito"),
            PaymentModel(register = 2, name = "Credito"),
            PaymentModel(register = 3, name = "PSE")
        )
    }
}
