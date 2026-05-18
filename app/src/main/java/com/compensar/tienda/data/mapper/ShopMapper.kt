package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.ShopEntity
import com.compensar.tienda.data.remote.dto.ShopDto

fun ShopDto.toEntity(): ShopEntity {
    return ShopEntity(
        register = register ?: 0,
        name = name,
        idSeller = idSeller ?: 0
    )
}

fun ShopEntity.toDto(): ShopDto {
    return ShopDto(
        register = register,
        name = name,
        idSeller = idSeller
    )
}