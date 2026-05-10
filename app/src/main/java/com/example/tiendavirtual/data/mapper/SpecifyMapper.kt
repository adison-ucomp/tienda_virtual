package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.SpecifyEntity
import com.example.tiendavirtual.data.remote.dto.SpecifyDto

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