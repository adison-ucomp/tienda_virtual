package com.compensar.tienda.firestore

import com.compensar.tienda.model.GatewayModel

class GatewayFire : BaseFire<GatewayModel>(
    collectionName = "gateway",
    clazz = GatewayModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
