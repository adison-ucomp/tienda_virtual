package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.ImageEntity
import com.example.tiendavirtual.data.remote.dto.ImageDto

fun ImageDto.toEntity(): ImageEntity {
    return ImageEntity(
        register = register ?: 0,
        urlImage = urlImage,
        idProduct = idProduct ?: 0
    )
}

fun ImageEntity.toDto(): ImageDto {
    return ImageDto(
        register = register,
        urlImage = urlImage,
        idProduct = idProduct
    )
}