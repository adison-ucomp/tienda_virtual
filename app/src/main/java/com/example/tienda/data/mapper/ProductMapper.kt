package com.example.tienda.data.mapper

import com.example.tienda.data.local.entity.ProductEntity
import com.example.tienda.data.remote.dto.ProductDto

fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        register = register ?: 0,
        name = name,
        detail = detail,
        urlImage = urlImage,
        idCategory = idCategory ?: 0,
        idShop = idShop ?: 0
    )
}

fun ProductEntity.toDto(): ProductDto {
    return ProductDto(
        register = register,
        name = name,
        detail = detail,
        urlImage = urlImage,
        idCategory = idCategory,
        idShop = idShop
    )
}