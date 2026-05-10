package com.example.tiendavirtual.data.mapper

import com.example.tiendavirtual.data.local.entity.GatewayEntity
import com.example.tiendavirtual.data.remote.dto.GatewayDto

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