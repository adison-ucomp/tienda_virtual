package com.example.tienda.data.mapper

import com.example.tienda.data.local.entity.AddressEntity
import com.example.tienda.data.remote.dto.AddressDto

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