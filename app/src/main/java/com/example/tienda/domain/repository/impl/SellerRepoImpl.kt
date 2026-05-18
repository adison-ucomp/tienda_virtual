package com.example.tienda.domain.repository.impl

import com.example.tienda.data.local.dao.SellerDao
import com.example.tienda.data.local.entity.SellerEntity
import com.example.tienda.data.mapper.toDto
import com.example.tienda.data.mapper.toEntity
import com.example.tienda.data.remote.api.SellerApi
import com.example.tienda.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow

class SellerRepoImpl(
    private val sellerDao: SellerDao,
    private val sellerApi: SellerApi
) : SellerRepository {

    override fun getAll(): Flow<List<SellerEntity>> {
        return sellerDao.getAll()
    }

    override suspend fun getByRegister(register: Long): SellerEntity? {
        return sellerDao.getByRegister(register)
    }

    override suspend fun getByUser(idUser: Long): SellerEntity? {
        return sellerDao.getByUser(idUser)
    }

    override suspend fun syncFromApi() {
        val sellers = sellerApi.getAll()
        sellers.forEach { dto ->
            sellerDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(seller: SellerEntity): Long {
        val savedSeller = sellerApi.insert(seller.toDto())
        return sellerDao.insert(savedSeller.toEntity())
    }

    override suspend fun update(seller: SellerEntity) {
        val updatedSeller = sellerApi.update(seller.register, seller.toDto())
        sellerDao.update(updatedSeller.toEntity())
    }

    override suspend fun delete(seller: SellerEntity) {
        sellerApi.delete(seller.register)
        sellerDao.delete(seller)
    }

    override suspend fun deleteAll() {
        sellerDao.deleteAll()
    }
}