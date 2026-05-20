package com.compensar.tienda.default

import com.compensar.tienda.model.AddressModel

class AddressFire : BaseFire<AddressModel>(
    collectionName = "address",
    clazz = AddressModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
