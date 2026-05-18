package com.example.tienda.domain.repository

import com.example.tienda.data.local.entity.RoleEntity
import kotlinx.coroutines.flow.Flow

interface RoleRepository {

    fun getAll(): Flow<List<RoleEntity>>

    suspend fun getByRegister(register: Long): RoleEntity?

    suspend fun syncFromApi()

    suspend fun insert(role: RoleEntity): Long

    suspend fun update(role: RoleEntity)

    suspend fun delete(role: RoleEntity)

    suspend fun deleteAll()
}