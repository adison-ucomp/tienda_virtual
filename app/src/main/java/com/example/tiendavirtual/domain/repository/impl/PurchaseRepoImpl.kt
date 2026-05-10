package com.example.tiendavirtual.domain.repository.impl

import com.example.tiendavirtual.data.local.dao.PurchaseDao
import com.example.tiendavirtual.data.local.entity.PurchaseEntity
import com.example.tiendavirtual.data.mapper.toDto
import com.example.tiendavirtual.data.mapper.toEntity
import com.example.tiendavirtual.data.remote.api.PurchaseApi
import com.example.tiendavirtual.domain.repository.PurchaseRepository
import kotlinx.coroutines.flow.Flow

class PurchaseRepoImpl(
    private val purchaseDao: PurchaseDao,
    private val purchaseApi: PurchaseApi
) : PurchaseRepository {

    override fun getAll(): Flow<List<PurchaseEntity>> {
        return purchaseDao.getAll()
    }

    override suspend fun getByRegister(register: Long): PurchaseEntity? {
        return purchaseDao.getByRegister(register)
    }

    override fun getByUser(idUser: Long): Flow<List<PurchaseEntity>> {
        return purchaseDao.getByUser(idUser)
    }

    override fun getByProduct(idProduct: Long): Flow<List<PurchaseEntity>> {
        return purchaseDao.getByProduct(idProduct)
    }

    override fun getByDate(date: String): Flow<List<PurchaseEntity>> {
        return purchaseDao.getByDate(date)
    }

    override suspend fun syncFromApi() {
        val purchases = purchaseApi.getAll()
        purchases.forEach { dto ->
            purchaseDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(purchase: PurchaseEntity): Long {
        val savedPurchase = purchaseApi.insert(purchase.toDto())
        return purchaseDao.insert(savedPurchase.toEntity())
    }

    override suspend fun update(purchase: PurchaseEntity) {
        val updatedPurchase = purchaseApi.update(purchase.register, purchase.toDto())
        purchaseDao.update(updatedPurchase.toEntity())
    }

    override suspend fun delete(purchase: PurchaseEntity) {
        purchaseApi.delete(purchase.register)
        purchaseDao.delete(purchase)
    }

    override suspend fun deleteAll() {
        purchaseDao.deleteAll()
    }
}