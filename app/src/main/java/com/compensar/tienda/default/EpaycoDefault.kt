package com.compensar.tienda.default

import com.compensar.tienda.model.EpaycoModel

object EpaycoDefault {

    fun getAll(): List<EpaycoModel> {
        return listOf(
            EpaycoModel(register = 1L, api = "JSON", state = "APROBADO", idOrder = 1L),
            EpaycoModel(register = 2L, api = "JSON", state = "APROBADO", idOrder = 2L),
            EpaycoModel(register = 3L, api = "JSON", state = "APROBADO", idOrder = 3L)
        )
    }
}
