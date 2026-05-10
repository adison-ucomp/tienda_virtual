package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.RoleEntity
import com.example.tiendavirtual.data.remote.dto.RoleDto

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