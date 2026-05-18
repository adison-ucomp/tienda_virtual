package com.compensar.tienda.domain.repository.impl

import com.compensar.tienda.data.local.dao.ImageDao
import com.compensar.tienda.data.local.entity.ImageEntity
import com.compensar.tienda.data.mapper.toDto
import com.compensar.tienda.data.mapper.toEntity
import com.compensar.tienda.data.remote.api.ImageApi
import com.compensar.tienda.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow

class ImageRepoImpl(
    private val imageDao: ImageDao,
    private val imageApi: ImageApi
) : ImageRepository {

    override fun getAll(): Flow<List<ImageEntity>> {
        return imageDao.getAll()
    }

    override suspend fun getByRegister(register: Long): ImageEntity? {
        return imageDao.getByRegister(register)
    }

    override fun getByProduct(idProduct: Long): Flow<List<ImageEntity>> {
        return imageDao.getByProduct(idProduct)
    }

    override suspend fun syncFromApi() {
        val images = imageApi.getAll()
        images.forEach { dto ->
            imageDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(image: ImageEntity): Long {
        val savedImage = imageApi.insert(image.toDto())
        return imageDao.insert(savedImage.toEntity())
    }

    override suspend fun update(image: ImageEntity) {
        val updatedImage = imageApi.update(image.register, image.toDto())
        imageDao.update(updatedImage.toEntity())
    }

    override suspend fun delete(image: ImageEntity) {
        imageApi.delete(image.register)
        imageDao.delete(image)
    }

    override suspend fun deleteAll() {
        imageDao.deleteAll()
    }
}