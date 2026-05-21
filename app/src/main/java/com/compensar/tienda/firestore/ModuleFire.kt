package com.compensar.tienda.firestore

import com.compensar.tienda.model.ModuleModel

class ModuleFire : BaseFire<ModuleModel>(
    collectionName = "module",
    clazz = ModuleModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
