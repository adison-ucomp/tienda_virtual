package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.model.PaymentModel

object PaymentDefault {

    fun getAll(): List<PaymentModel> {
        return listOf(
            PaymentModel(register = 1, name = "Debito"),
            PaymentModel(register = 2, name = "Credito")
        )
    }
}
