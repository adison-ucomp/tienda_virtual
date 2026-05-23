package com.compensar.tienda.firestore

import com.compensar.tienda.model.ModuleModel

/**
 * Acceso Firestore para la entidad Module.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class ModuleFire : BaseFire<ModuleModel>(
    collectionName = "module",
    clazz = ModuleModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

