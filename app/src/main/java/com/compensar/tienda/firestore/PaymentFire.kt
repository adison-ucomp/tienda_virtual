package com.compensar.tienda.firestore

import com.compensar.tienda.model.PaymentModel

/**
 * Acceso Firestore para la entidad Payment.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class PaymentFire : BaseFire<PaymentModel>(
    collectionName = "payment",
    clazz = PaymentModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

