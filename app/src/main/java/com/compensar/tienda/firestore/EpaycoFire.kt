package com.compensar.tienda.firestore

import com.compensar.tienda.model.EpaycoModel

class EpaycoFire : BaseFire<EpaycoModel>(
    collectionName = "epayco",
    clazz = EpaycoModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
