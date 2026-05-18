package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.SpecifyEntity
import com.compensar.tienda.data.remote.dto.SpecifyDto

fun SpecifyDto.toEntity(): SpecifyEntity {
    return SpecifyEntity(
        register = register ?: 0,
        name = name,
        detail = detail,
        idProduct = idProduct ?: 0
    )
}

fun SpecifyEntity.toDto(): SpecifyDto {
    return SpecifyDto(
        register = register,
        name = name,
        detail = detail,
        idProduct = idProduct
    )
}