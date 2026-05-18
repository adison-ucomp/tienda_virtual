package com.example.tienda.data.mapper

import com.example.tienda.data.local.entity.SpecifyEntity
import com.example.tienda.data.remote.dto.SpecifyDto

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