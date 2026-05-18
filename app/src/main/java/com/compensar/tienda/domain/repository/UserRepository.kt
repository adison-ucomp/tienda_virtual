package com.compensar.tienda.domain.repository

import com.compensar.tienda.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAll(): Flow<List<UserEntity>>

    suspend fun getByRegister(register: Long): UserEntity?

    suspend fun getByEmail(email: String): UserEntity?

    fun getByRole(idRole: Long): Flow<List<UserEntity>>

    suspend fun syncFromApi()

    suspend fun insert(user: UserEntity): Long

    suspend fun update(user: UserEntity)

    suspend fun delete(user: UserEntity)

    suspend fun deleteAll()
}