package com.compensar.tienda.domain.repository.impl

import com.compensar.tienda.data.local.dao.ProductDao
import com.compensar.tienda.data.local.entity.ProductEntity
import com.compensar.tienda.data.mapper.toDto
import com.compensar.tienda.data.mapper.toEntity
import com.compensar.tienda.data.remote.api.ProductApi
import com.compensar.tienda.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ProductRepoImpl(
    private val productDao: ProductDao,
    private val productApi: ProductApi
) : ProductRepository {

    override fun getAll(): Flow<List<ProductEntity>> {
        return productDao.getAll()
    }

    override suspend fun getByRegister(register: Long): ProductEntity? {
        return productDao.getByRegister(register)
    }

    override fun getByCategory(idCategory: Long): Flow<List<ProductEntity>> {
        return productDao.getByCategory(idCategory)
    }

    override fun getByShop(idShop: Long): Flow<List<ProductEntity>> {
        return productDao.getByShop(idShop)
    }

    override fun searchByName(name: String): Flow<List<ProductEntity>> {
        return productDao.searchByName(name)
    }

    override suspend fun syncFromApi() {
        val products = productApi.getAll()
        products.forEach { dto ->
            productDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(product: ProductEntity): Long {
        val savedProduct = productApi.insert(product.toDto())
        return productDao.insert(savedProduct.toEntity())
    }

    override suspend fun update(product: ProductEntity) {
        val updatedProduct = productApi.update(product.register, product.toDto())
        productDao.update(updatedProduct.toEntity())
    }

    override suspend fun delete(product: ProductEntity) {
        productApi.delete(product.register)
        productDao.delete(product)
    }

    override suspend fun deleteAll() {
        productDao.deleteAll()
    }
}