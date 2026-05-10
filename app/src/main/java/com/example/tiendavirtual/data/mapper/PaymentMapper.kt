package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.PaymentEntity
import com.example.tiendavirtual.data.remote.dto.PaymentDto

fun PaymentDto.toEntity(): PaymentEntity {
    return PaymentEntity(
        register = register ?: 0,
        name = name
    )
}

fun PaymentEntity.toDto(): PaymentDto {
    return PaymentDto(
        register = register,
        name = name
    )
}