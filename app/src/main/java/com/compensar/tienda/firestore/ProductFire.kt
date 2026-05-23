package com.compensar.tienda.firestore

import com.compensar.tienda.model.ProductModel

/**
 * Acceso Firestore para la entidad Product.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class ProductFire : BaseFire<ProductModel>(
    collectionName = "product",
    clazz = ProductModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

