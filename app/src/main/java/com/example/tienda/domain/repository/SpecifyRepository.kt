package com.example.tienda.domain.repository

import com.example.tienda.data.local.entity.SpecifyEntity
import kotlinx.coroutines.flow.Flow

interface SpecifyRepository {

    fun getAll(): Flow<List<SpecifyEntity>>

    suspend fun getByRegister(register: Long): SpecifyEntity?

    fun getByProduct(idProduct: Long): Flow<List<SpecifyEntity>>

    suspend fun syncFromApi()

    suspend fun insert(specify: SpecifyEntity): Long

    suspend fun update(specify: SpecifyEntity)

    suspend fun delete(specify: SpecifyEntity)

    suspend fun deleteAll()
}