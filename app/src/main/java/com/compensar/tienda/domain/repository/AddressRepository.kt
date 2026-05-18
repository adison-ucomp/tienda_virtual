package com.compensar.tienda.domain.repository

import com.compensar.tienda.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    fun getAll(): Flow<List<AddressEntity>>

    suspend fun getByRegister(register: Long): AddressEntity?

    fun getByUser(idUser: Long): Flow<List<AddressEntity>>

    suspend fun syncFromApi()

    suspend fun insert(address: AddressEntity): Long

    suspend fun update(address: AddressEntity)

    suspend fun delete(address: AddressEntity)

    suspend fun deleteAll()
}