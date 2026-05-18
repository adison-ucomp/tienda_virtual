package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.CategoryEntity
import com.compensar.tienda.data.remote.dto.CategoryDto

fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        register = register ?: 0,
        name = name
    )
}

fun CategoryEntity.toDto(): CategoryDto {
    return CategoryDto(
        register = register,
        name = name
    )
}