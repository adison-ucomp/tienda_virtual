package com.compensar.tienda.default

import com.compensar.tienda.model.ShopModel

/**
 * Tiendas iniciales asociadas a vendedores.
 */
object ShopDefault {

    /**
     * Retorna el conjunto de tiendas por defecto.
     */
    fun getAll(): List<ShopModel> {
        return listOf(
            ShopModel(
                register = 1,
                name = "Universidad",
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fshop%2F1_1779413510854.jpg?alt=media&token=b478c991-aed5-407e-8290-0583582e8fa7",
                idSeller = 1
            )
        )
    }
}
