package com.compensar.tienda.firestore

import com.compensar.tienda.model.ShopModel

/**
 * Acceso Firestore para la entidad Shop.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class ShopFire : BaseFire<ShopModel>(
    collectionName = "shop",
    clazz = ShopModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

