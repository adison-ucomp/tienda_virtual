package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.PurchaseEntity
import com.example.tiendavirtual.data.remote.dto.PurchaseDto

fun PurchaseDto.toEntity(): PurchaseEntity {
    return PurchaseEntity(
        register = register ?: 0,
        date = date ?: "",
        hour = hour,
        amount = amount,
        value = value,
        total = total,
        idProduct = idProduct ?: 0,
        idMethod = idMethod ?: 0,
        idGangway = idGangway ?: 0,
        idUser = idUser ?: 0
    )
}

fun PurchaseEntity.toDto(): PurchaseDto {
    return PurchaseDto(
        register = register,
        date = date,
        hour = hour,
        amount = amount,
        value = value,
        total = total,
        idProduct = idProduct,
        idMethod = idMethod,
        idGangway = idGangway,
        idUser = idUser
    )
}