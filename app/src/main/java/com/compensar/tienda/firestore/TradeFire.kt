package com.compensar.tienda.firestore

import com.compensar.tienda.model.TradeModel

/**
 * Acceso Firestore para la entidad Trade.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class TradeFire : BaseFire<TradeModel>(
    collectionName = "trade",
    clazz = TradeModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

