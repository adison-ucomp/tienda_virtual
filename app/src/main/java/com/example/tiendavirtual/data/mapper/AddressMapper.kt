package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.AddressEntity
import com.example.tiendavirtual.data.remote.dto.AddressDto

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