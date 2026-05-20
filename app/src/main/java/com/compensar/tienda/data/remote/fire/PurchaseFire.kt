package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.model.PurchaseModel

class PurchaseFire : BaseFire<PurchaseModel>(
    collectionName = "purchase",
    clazz = PurchaseModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
