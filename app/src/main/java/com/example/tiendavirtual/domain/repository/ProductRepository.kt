package com.example.tiendavirtual.domain.repository

import com.example.tiendavirtual.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAll(): Flow<List<ProductEntity>>

    suspend fun getByRegister(register: Long): ProductEntity?

    fun getByCategory(idCategory: Long): Flow<List<ProductEntity>>

    fun getByShop(idShop: Long): Flow<List<ProductEntity>>

    fun searchByName(name: String): Flow<List<ProductEntity>>

    suspend fun syncFromApi()

    suspend fun insert(product: ProductEntity): Long

    suspend fun update(product: ProductEntity)

    suspend fun delete(product: ProductEntity)

    suspend fun deleteAll()
}