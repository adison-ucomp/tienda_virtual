package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.CategoryEntity
import com.example.tiendavirtual.data.remote.dto.CategoryDto

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