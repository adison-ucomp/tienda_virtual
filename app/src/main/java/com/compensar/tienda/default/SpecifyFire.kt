package com.compensar.tienda.default

import com.compensar.tienda.model.SpecifyModel

class SpecifyFire : BaseFire<SpecifyModel>(
    collectionName = "specify",
    clazz = SpecifyModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
