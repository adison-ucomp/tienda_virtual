package com.example.tienda.data.mapper

import com.example.tienda.data.local.entity.GatewayEntity
import com.example.tienda.data.remote.dto.GatewayDto

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