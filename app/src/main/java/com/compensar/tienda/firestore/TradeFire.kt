package com.compensar.tienda.firestore

import com.compensar.tienda.model.TradeModel

class TradeFire : BaseFire<TradeModel>(
    collectionName = "trade",
    clazz = TradeModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
