package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.AddressEntity
import com.compensar.tienda.data.remote.dto.AddressDto

fun AddressDto.toEntity(): AddressEntity {
    return AddressEntity(
        register = register ?: 0,
        address = address,
        idUser = idUser ?: 0
    )
}

fun AddressEntity.toDto(): AddressDto {
    return AddressDto(
        register = register,
        address = address,
        idUser = idUser
    )
}