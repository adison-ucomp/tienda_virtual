package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.ProductModel

class ProductFire : BaseFire<ProductModel>(
    collectionName = "product",
    clazz = ProductModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)