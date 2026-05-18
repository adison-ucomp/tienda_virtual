package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.SellerModel

class SellerFire : BaseFire<SellerModel>(
    collectionName = "seller",
    clazz = SellerModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)