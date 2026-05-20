package com.compensar.tienda.firestore

import com.compensar.tienda.model.ShopModel

class ShopFire : BaseFire<ShopModel>(
    collectionName = "shop",
    clazz = ShopModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
