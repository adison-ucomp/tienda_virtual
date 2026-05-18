package com.example.tienda.domain.repository

import com.example.tienda.data.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    fun getAll(): Flow<List<ImageEntity>>

    suspend fun getByRegister(register: Long): ImageEntity?

    fun getByProduct(idProduct: Long): Flow<List<ImageEntity>>

    suspend fun syncFromApi()

    suspend fun insert(image: ImageEntity): Long

    suspend fun update(image: ImageEntity)

    suspend fun delete(image: ImageEntity)

    suspend fun deleteAll()
}