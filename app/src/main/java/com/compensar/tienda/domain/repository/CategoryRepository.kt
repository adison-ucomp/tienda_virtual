package com.compensar.tienda.domain.repository

import com.compensar.tienda.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getAll(): Flow<List<CategoryEntity>>

    suspend fun getByRegister(register: Long): CategoryEntity?

    suspend fun syncFromApi()

    suspend fun insert(category: CategoryEntity): Long

    suspend fun update(category: CategoryEntity)

    suspend fun delete(category: CategoryEntity)

    suspend fun deleteAll()
}