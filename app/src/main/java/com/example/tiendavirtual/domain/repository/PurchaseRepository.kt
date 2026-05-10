package com.example.tiendavirtual.domain.repository

import com.example.tiendavirtual.data.local.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {

    fun getAll(): Flow<List<PurchaseEntity>>

    suspend fun getByRegister(register: Long): PurchaseEntity?

    fun getByUser(idUser: Long): Flow<List<PurchaseEntity>>

    fun getByProduct(idProduct: Long): Flow<List<PurchaseEntity>>

    fun getByDate(date: String): Flow<List<PurchaseEntity>>

    suspend fun syncFromApi()

    suspend fun insert(purchase: PurchaseEntity): Long

    suspend fun update(purchase: PurchaseEntity)

    suspend fun delete(purchase: PurchaseEntity)

    suspend fun deleteAll()
}