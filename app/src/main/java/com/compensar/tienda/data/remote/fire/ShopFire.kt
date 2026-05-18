package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.ShopModel

class ShopFire : BaseFire<ShopModel>(
    collectionName = "shop",
    clazz = ShopModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)