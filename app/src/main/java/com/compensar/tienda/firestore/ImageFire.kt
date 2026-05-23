package com.compensar.tienda.firestore

import com.compensar.tienda.model.ImageModel

/**
 * Acceso Firestore para la entidad Image.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class ImageFire : BaseFire<ImageModel>(
    collectionName = "image",
    clazz = ImageModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

