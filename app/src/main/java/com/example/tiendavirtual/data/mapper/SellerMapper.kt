package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.SellerEntity
import com.example.tiendavirtual.data.remote.dto.SellerDto

fun SellerDto.toEntity(): SellerEntity {
    return SellerEntity(
        register = register ?: 0,
        company = company,
        nit = nit,
        address = address,
        idUser = idUser ?: 0
    )
}

fun SellerEntity.toDto(): SellerDto {
    return SellerDto(
        register = register,
        company = company,
        nit = nit,
        address = address,
        idUser = idUser
    )
}