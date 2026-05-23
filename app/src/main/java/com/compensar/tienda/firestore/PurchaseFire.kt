package com.compensar.tienda.firestore

import com.compensar.tienda.model.PurchaseModel

/**
 * Acceso Firestore para la entidad Purchase.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class PurchaseFire : BaseFire<PurchaseModel>(
    collectionName = "purchase",
    clazz = PurchaseModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

