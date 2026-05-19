package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.EmailModel

class EmailFire : BaseFire<EmailModel>(
    collectionName = "email",
    clazz = EmailModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
