package com.compensar.tienda.firestore

import com.compensar.tienda.model.UserModel

/**
 * Acceso Firestore para la entidad User.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class UserFire : BaseFire<UserModel>(
    collectionName = "user",
    clazz = UserModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

