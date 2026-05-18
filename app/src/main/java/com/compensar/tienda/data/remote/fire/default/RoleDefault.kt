package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.domain.model.RoleModel

object RoleDefault {

    fun getAll(): List<RoleModel> {
        return listOf(
            RoleModel(register = 1, name = "Admin"),
            RoleModel(register = 2, name = "Vendedor"),
            RoleModel(register = 3, name = "Comprador")
        )
    }
}