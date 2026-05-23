package com.compensar.tienda.default

import com.compensar.tienda.model.RoleModel

/**
 * Roles base de la aplicacion.
 */
object RoleDefault {

    /**
     * Retorna los roles iniciales del sistema.
     */
    fun getAll(): List<RoleModel> {
        return listOf(
            RoleModel(register = 1, name = "Admin"),
            RoleModel(register = 2, name = "Vendedor"),
            RoleModel(register = 3, name = "Comprador")
        )
    }
}
