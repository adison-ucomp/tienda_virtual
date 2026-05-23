package com.compensar.tienda.firestore

import com.compensar.tienda.model.RoleModel

/**
 * Acceso Firestore para la entidad Role.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class RoleFire : BaseFire<RoleModel>(
    collectionName = "role",
    clazz = RoleModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

