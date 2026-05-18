package com.compensar.tienda.domain.repository.impl

import com.compensar.tienda.data.local.dao.GatewayDao
import com.compensar.tienda.data.local.entity.GatewayEntity
import com.compensar.tienda.data.mapper.toDto
import com.compensar.tienda.data.mapper.toEntity
import com.compensar.tienda.data.remote.api.GatewayApi
import com.compensar.tienda.domain.repository.GatewayRepository
import kotlinx.coroutines.flow.Flow

class GatewayRepoImpl(
    private val gatewayDao: GatewayDao,
    private val gatewayApi: GatewayApi
) : GatewayRepository {

    override fun getAll(): Flow<List<GatewayEntity>> {
        return gatewayDao.getAll()
    }

    override suspend fun getByRegister(register: Long): GatewayEntity? {
        return gatewayDao.getByRegister(register)
    }

    override suspend fun syncFromApi() {
        val gateways = gatewayApi.getAll()
        gateways.forEach { dto ->
            gatewayDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(gateway: GatewayEntity): Long {
        val savedGateway = gatewayApi.insert(gateway.toDto())
        return gatewayDao.insert(savedGateway.toEntity())
    }

    override suspend fun update(gateway: GatewayEntity) {
        val updatedGateway = gatewayApi.update(gateway.register, gateway.toDto())
        gatewayDao.update(updatedGateway.toEntity())
    }

    override suspend fun delete(gateway: GatewayEntity) {
        gatewayApi.delete(gateway.register)
        gatewayDao.delete(gateway)
    }

    override suspend fun deleteAll() {
        gatewayDao.deleteAll()
    }
}