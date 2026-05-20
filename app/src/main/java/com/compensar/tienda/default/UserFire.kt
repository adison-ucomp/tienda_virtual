package com.compensar.tienda.default

import com.compensar.tienda.model.UserModel

class UserFire : BaseFire<UserModel>(
    collectionName = "user",
    clazz = UserModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
