package com.compensar.tienda.domain.repository.impl

import com.compensar.tienda.data.local.dao.ShopDao
import com.compensar.tienda.data.local.entity.ShopEntity
import com.compensar.tienda.data.mapper.toDto
import com.compensar.tienda.data.mapper.toEntity
import com.compensar.tienda.data.remote.api.ShopApi
import com.compensar.tienda.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

class ShopRepoImpl(
    private val shopDao: ShopDao,
    private val shopApi: ShopApi
) : ShopRepository {

    override fun getAll(): Flow<List<ShopEntity>> {
        return shopDao.getAll()
    }

    override suspend fun getByRegister(register: Long): ShopEntity? {
        return shopDao.getByRegister(register)
    }

    override fun getBySeller(idSeller: Long): Flow<List<ShopEntity>> {
        return shopDao.getBySeller(idSeller)
    }

    override suspend fun syncFromApi() {
        val shops = shopApi.getAll()
        shops.forEach { dto ->
            shopDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(shop: ShopEntity): Long {
        val savedShop = shopApi.insert(shop.toDto())
        return shopDao.insert(savedShop.toEntity())
    }

    override suspend fun update(shop: ShopEntity) {
        val updatedShop = shopApi.update(shop.register, shop.toDto())
        shopDao.update(updatedShop.toEntity())
    }

    override suspend fun delete(shop: ShopEntity) {
        shopApi.delete(shop.register)
        shopDao.delete(shop)
    }

    override suspend fun deleteAll() {
        shopDao.deleteAll()
    }
}