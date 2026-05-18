package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.UserModel

class UserFire : BaseFire<UserModel>(
    collectionName = "user",
    clazz = UserModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)