package com.compensar.tienda.firestore

import com.compensar.tienda.model.CategoryModel

/**
 * Acceso Firestore para la entidad Category.
 *
 * Extiende [BaseFire] para reutilizar operaciones CRUD sobre su coleccion.
 */
class CategoryFire : BaseFire<CategoryModel>(
    collectionName = "category",
    clazz = CategoryModel::class.java,
    getRegister = { it.register },
    withRegister = { data, register -> data.copy(register = register) }
)

