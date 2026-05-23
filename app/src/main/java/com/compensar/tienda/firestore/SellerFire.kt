package com.compensar.tienda.firestore

import com.compensar.tienda.model.SellerModel

/**
 * Acceso Firestore para la entidad Seller.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class SellerFire : BaseFire<SellerModel>(
    collectionName = "seller",
    clazz = SellerModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

