package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.RoleEntity
import com.compensar.tienda.data.remote.dto.RoleDto

fun RoleDto.toEntity(): RoleEntity {
    return RoleEntity(
        register = register ?: 0,
        name = name
    )
}

fun RoleEntity.toDto(): RoleDto {
    return RoleDto(
        register = register,
        name = name
    )
}