package com.compensar.tienda.default

import com.compensar.tienda.model.SellerModel

object SellerDefault {

    fun getAll(): List<SellerModel> {
        return listOf(
            SellerModel(
                register = 1,
                company = "UCompensar",
                nit = "123456789",
                address = "Calle 1 Carrera 1 # 1",
                idUser = 2
            )
        )
    }
}
