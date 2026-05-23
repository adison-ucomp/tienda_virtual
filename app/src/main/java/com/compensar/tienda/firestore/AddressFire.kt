package com.compensar.tienda.firestore

import com.compensar.tienda.model.AddressModel

/**
 * Acceso Firestore para la entidad Address.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class AddressFire : BaseFire<AddressModel>(
    collectionName = "address",
    clazz = AddressModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

