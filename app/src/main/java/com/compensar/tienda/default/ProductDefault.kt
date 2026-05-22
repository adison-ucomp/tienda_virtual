package com.compensar.tienda.default

import com.compensar.tienda.model.ProductModel

object ProductDefault {

    fun getAll(): List<ProductModel> {
        return listOf(
            ProductModel(
                register = 1,
                name = "A",
                detail = "Prueba",
                stock = 10,
                reserved = 0,
                price = 5000.0,
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fproduct%2F1_1779213640610.jpg?alt=media&token=70a8296a-8d27-4eb9-a139-19644d411509",
                idCategory = 1,
                idShop = 1
            ),
            ProductModel(
                register = 2,
                name = "B",
                detail = "Prueba",
                stock = 20,
                reserved = 0,
                price = 10000.0,
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fproduct%2F2_1779213669660.jpg?alt=media&token=71618d69-5337-4d6d-ba51-1dda042aba56",
                idCategory = 2,
                idShop = 2
            ),
            ProductModel(
                register = 3,
                name = "C",
                detail = "Prueba",
                stock = 30,
                reserved = 0,
                price = 15000.0,
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fproduct%2F3_1779241702322.jpg?alt=media&token=f27c954e-d9e0-43ac-8cdd-8b5cc373c122",
                idCategory = 1,
                idShop = 3
            )
        )
    }
}
