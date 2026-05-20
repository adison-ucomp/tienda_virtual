package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.model.CategoryModel

class CategoryFire : BaseFire<CategoryModel>(
    collectionName = "category",
    clazz = CategoryModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
