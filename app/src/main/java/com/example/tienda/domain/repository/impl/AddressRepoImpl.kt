package com.example.tienda.domain.repository.impl

import com.example.tienda.data.local.dao.AddressDao
import com.example.tienda.data.local.entity.AddressEntity
import com.example.tienda.data.mapper.toDto
import com.example.tienda.data.mapper.toEntity
import com.example.tienda.data.remote.api.AddressApi
import com.example.tienda.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow

class AddressRepoImpl(
    private val addressDao: AddressDao,
    private val addressApi: AddressApi
) : AddressRepository {

    override fun getAll(): Flow<List<AddressEntity>> {
        return addressDao.getAll()
    }

    override suspend fun getByRegister(register: Long): AddressEntity? {
        return addressDao.getByRegister(register)
    }

    override fun getByUser(idUser: Long): Flow<List<AddressEntity>> {
        return addressDao.getByUser(idUser)
    }

    override suspend fun syncFromApi() {
        val addresses = addressApi.getAll()
        addresses.forEach { dto ->
            addressDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(address: AddressEntity): Long {
        val savedAddress = addressApi.insert(address.toDto())
        return addressDao.insert(savedAddress.toEntity())
    }

    override suspend fun update(address: AddressEntity) {
        val updatedAddress = addressApi.update(address.register, address.toDto())
        addressDao.update(updatedAddress.toEntity())
    }

    override suspend fun delete(address: AddressEntity) {
        addressApi.delete(address.register)
        addressDao.delete(address)
    }

    override suspend fun deleteAll() {
        addressDao.deleteAll()
    }
}