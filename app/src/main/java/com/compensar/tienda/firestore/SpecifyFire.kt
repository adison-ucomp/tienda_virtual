package com.compensar.tienda.firestore

import com.compensar.tienda.model.SpecifyModel

/**
 * Acceso Firestore para la entidad Specify.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class SpecifyFire : BaseFire<SpecifyModel>(
    collectionName = "specify",
    clazz = SpecifyModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

