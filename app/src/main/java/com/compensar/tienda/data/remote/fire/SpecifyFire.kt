package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.SpecifyModel

class SpecifyFire : BaseFire<SpecifyModel>(
    collectionName = "specify",
    clazz = SpecifyModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)