package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.model.ProductModel

object ProductDefault {

    fun getAll(): List<ProductModel> {
        return listOf(
            ProductModel(
                register = 1,
                name = "A",
                detail = "Prueba",
                stock = 10,
                price = 100.0,
                storefire = "Url",
                idCategory = 1,
                idShop = 1
            ),
            ProductModel(
                register = 2,
                name = "B",
                detail = "Prueba",
                stock = 20,
                price = 200.0,
                storefire = "Url",
                idCategory = 2,
                idShop = 2
            ),
            ProductModel(
                register = 3,
                name = "C",
                detail = "Prueba",
                stock = 30,
                price = 300.0,
                storefire = "Url",
                idCategory = 1,
                idShop = 3
            )
        )
    }
}
