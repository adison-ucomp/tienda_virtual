package com.compensar.tienda.default

import com.compensar.tienda.model.CategoryModel

object CategoryDefault {

    fun getAll(): List<CategoryModel> {
        return listOf(
            CategoryModel(
                register = 1,
                name = "Tecnologia",
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fcategory%2F1_1779418996180.jpg?alt=media&token=b788cecb-eb34-4435-8979-a2f3273cc5c3"
            ),
            CategoryModel(
                register = 2,
                name = "Deportes",
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fcategory%2F2_1779419016167.jpg?alt=media&token=3fca3dd4-8bc7-47a0-87d1-96a5abc8720f"
            )
        )
    }
}
