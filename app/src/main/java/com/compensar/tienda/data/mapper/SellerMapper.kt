package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.SellerEntity
import com.compensar.tienda.data.remote.dto.SellerDto

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