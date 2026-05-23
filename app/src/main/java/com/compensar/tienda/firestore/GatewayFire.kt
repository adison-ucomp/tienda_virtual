package com.compensar.tienda.firestore

import com.compensar.tienda.model.GatewayModel

/**
 * Acceso Firestore para la entidad Gateway.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class GatewayFire : BaseFire<GatewayModel>(
    collectionName = "gateway",
    clazz = GatewayModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

