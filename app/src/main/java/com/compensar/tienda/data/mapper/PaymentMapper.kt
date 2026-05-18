package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.PaymentEntity
import com.compensar.tienda.data.remote.dto.PaymentDto

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