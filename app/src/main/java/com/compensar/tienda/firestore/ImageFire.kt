package com.compensar.tienda.firestore

import com.compensar.tienda.model.ImageModel

class ImageFire : BaseFire<ImageModel>(
    collectionName = "image",
    clazz = ImageModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)
