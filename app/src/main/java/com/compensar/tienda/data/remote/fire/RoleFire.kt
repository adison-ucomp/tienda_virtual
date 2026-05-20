package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.model.RoleModel

class RoleFire : BaseFire<RoleModel>(
    collectionName = "role",
    clazz = RoleModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
