package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.domain.model.AddressModel

class AddressFire : BaseFire<AddressModel>(
    collectionName = "address",
    clazz = AddressModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)