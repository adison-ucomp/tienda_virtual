package com.example.tienda.domain.repository

import com.example.tienda.data.local.entity.GatewayEntity
import kotlinx.coroutines.flow.Flow

interface GatewayRepository {

    fun getAll(): Flow<List<GatewayEntity>>

    suspend fun getByRegister(register: Long): GatewayEntity?

    suspend fun syncFromApi()

    suspend fun insert(gateway: GatewayEntity): Long

    suspend fun update(gateway: GatewayEntity)

    suspend fun delete(gateway: GatewayEntity)

    suspend fun deleteAll()
}