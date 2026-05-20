package com.compensar.tienda.firestore

import com.compensar.tienda.model.PaymentModel

class PaymentFire : BaseFire<PaymentModel>(
    collectionName = "payment",
    clazz = PaymentModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
