package com.compensar.tienda.domain.repository.impl

import com.compensar.tienda.data.local.dao.CategoryDao
import com.compensar.tienda.data.local.entity.CategoryEntity
import com.compensar.tienda.data.mapper.toDto
import com.compensar.tienda.data.mapper.toEntity
import com.compensar.tienda.data.remote.api.CategoryApi
import com.compensar.tienda.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepoImpl(
    private val categoryDao: CategoryDao,
    private val categoryApi: CategoryApi
) : CategoryRepository {

    override fun getAll(): Flow<List<CategoryEntity>> {
        return categoryDao.getAll()
    }

    override suspend fun getByRegister(register: Long): CategoryEntity? {
        return categoryDao.getByRegister(register)
    }

    override suspend fun syncFromApi() {
        val categories = categoryApi.getAll()
        categories.forEach { dto ->
            categoryDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(category: CategoryEntity): Long {
        val savedCategory = categoryApi.insert(category.toDto())
        return categoryDao.insert(savedCategory.toEntity())
    }

    override suspend fun update(category: CategoryEntity) {
        val updatedCategory = categoryApi.update(category.register, category.toDto())
        categoryDao.update(updatedCategory.toEntity())
    }

    override suspend fun delete(category: CategoryEntity) {
        categoryApi.delete(category.register)
        categoryDao.delete(category)
    }

    override suspend fun deleteAll() {
        categoryDao.deleteAll()
    }
}