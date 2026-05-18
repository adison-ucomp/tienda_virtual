package com.compensar.tienda.domain.repository.impl

import com.compensar.tienda.data.local.dao.SpecifyDao
import com.compensar.tienda.data.local.entity.SpecifyEntity
import com.compensar.tienda.data.mapper.toDto
import com.compensar.tienda.data.mapper.toEntity
import com.compensar.tienda.data.remote.api.SpecifyApi
import com.compensar.tienda.domain.repository.SpecifyRepository
import kotlinx.coroutines.flow.Flow

class SpecifyRepoImpl(
    private val specifyDao: SpecifyDao,
    private val specifyApi: SpecifyApi
) : SpecifyRepository {

    override fun getAll(): Flow<List<SpecifyEntity>> {
        return specifyDao.getAll()
    }

    override suspend fun getByRegister(register: Long): SpecifyEntity? {
        return specifyDao.getByRegister(register)
    }

    override fun getByProduct(idProduct: Long): Flow<List<SpecifyEntity>> {
        return specifyDao.getByProduct(idProduct)
    }

    override suspend fun syncFromApi() {
        val specifications = specifyApi.getAll()
        specifications.forEach { dto ->
            specifyDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(specify: SpecifyEntity): Long {
        val savedSpecify = specifyApi.insert(specify.toDto())
        return specifyDao.insert(savedSpecify.toEntity())
    }

    override suspend fun update(specify: SpecifyEntity) {
        val updatedSpecify = specifyApi.update(specify.register, specify.toDto())
        specifyDao.update(updatedSpecify.toEntity())
    }

    override suspend fun delete(specify: SpecifyEntity) {
        specifyApi.delete(specify.register)
        specifyDao.delete(specify)
    }

    override suspend fun deleteAll() {
        specifyDao.deleteAll()
    }
}