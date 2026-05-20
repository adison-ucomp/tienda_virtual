package com.compensar.tienda.default

import com.compensar.tienda.model.CategoryModel

object CategoryDefault {

    fun getAll(): List<CategoryModel> {
        return listOf(
            CategoryModel(register = 1, name = "Tecnologia"),
            CategoryModel(register = 2, name = "Deportes")
        )
    }
}
