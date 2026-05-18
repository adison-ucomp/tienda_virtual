package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.GatewayModel

class GatewayFire : BaseFire<GatewayModel>(
    collectionName = "gateway",
    clazz = GatewayModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)