package com.compensar.tienda.data.mapper

import com.compensar.tienda.data.local.entity.GatewayEntity
import com.compensar.tienda.data.remote.dto.GatewayDto

fun GatewayDto.toEntity(): GatewayEntity {
    return GatewayEntity(
        register = register ?: 0,
        name = name
    )
}

fun GatewayEntity.toDto(): GatewayDto {
    return GatewayDto(
        register = register,
        name = name
    )
}