package com.compensar.tienda.domain.repository

import com.compensar.tienda.data.local.entity.SellerEntity
import kotlinx.coroutines.flow.Flow

interface SellerRepository {

    fun getAll(): Flow<List<SellerEntity>>

    suspend fun getByRegister(register: Long): SellerEntity?

    suspend fun getByUser(idUser: Long): SellerEntity?

    suspend fun syncFromApi()

    suspend fun insert(seller: SellerEntity): Long

    suspend fun update(seller: SellerEntity)

    suspend fun delete(seller: SellerEntity)

    suspend fun deleteAll()
}