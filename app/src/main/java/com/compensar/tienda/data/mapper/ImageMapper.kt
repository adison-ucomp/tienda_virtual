package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.ImageEntity
import com.compensar.tienda.data.remote.dto.ImageDto

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