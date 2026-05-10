package com.example.tiendavirtual.domain.repository

import com.example.tiendavirtual.data.local.entity.ShopEntity
import kotlinx.coroutines.flow.Flow

interface ShopRepository {

    fun getAll(): Flow<List<ShopEntity>>

    suspend fun getByRegister(register: Long): ShopEntity?

    fun getBySeller(idSeller: Long): Flow<List<ShopEntity>>

    suspend fun syncFromApi()

    suspend fun insert(shop: ShopEntity): Long

    suspend fun update(shop: ShopEntity)

    suspend fun delete(shop: ShopEntity)

    suspend fun deleteAll()
}