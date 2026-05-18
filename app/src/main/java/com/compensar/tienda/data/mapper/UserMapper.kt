package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.UserEntity
import com.compensar.tienda.data.remote.dto.UserDto

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        register = register ?: 0,
        names = names,
        srnms = srnms,
        email = email,
        password = password,
        idRole = idRole ?: 0
    )
}

fun UserEntity.toDto(): UserDto {
    return UserDto(
        register = register,
        names = names,
        srnms = srnms,
        email = email,
        password = password,
        idRole = idRole
    )
}