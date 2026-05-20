package com.compensar.tienda.default

import com.compensar.tienda.model.ProductModel

class ProductFire : BaseFire<ProductModel>(
    collectionName = "product",
    clazz = ProductModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
