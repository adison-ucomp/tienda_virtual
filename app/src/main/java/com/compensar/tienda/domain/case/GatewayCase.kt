package com.compensar.tienda.domain.case

import com.compensar.tienda.data.local.entity.GatewayEntity
import com.compensar.tienda.domain.repository.GatewayRepository
import kotlinx.coroutines.flow.Flow

data class GatewayCase(
    val createGateway: CreateGatewayCase,
    val getGateways: GetGatewaysCase,
    val getGatewayByRegister: GetGatewayByRegisterCase,
    val updateGateway: UpdateGatewayCase,
    val deleteGateway: DeleteGatewayCase,
    val deleteAllGateways: DeleteAllGatewaysCase,
    val syncGatewaysFromApi: SyncGatewaysFromApiCase
)

class CreateGatewayCase(
    private val gatewayRepository: GatewayRepository
) {
    suspend operator fun invoke(gateway: GatewayEntity): Long {
        return gatewayRepository.insert(gateway)
    }
}

class GetGatewaysCase(
    private val gatewayRepository: GatewayRepository
) {
    operator fun invoke(): Flow<List<GatewayEntity>> {
        return gatewayRepository.getAll()
    }
}

class GetGatewayByRegisterCase(
    private val gatewayRepository: GatewayRepository
) {
    suspend operator fun invoke(register: Long): GatewayEntity? {
        return gatewayRepository.getByRegister(register)
    }
}

class UpdateGatewayCase(
    private val gatewayRepository: GatewayRepository
) {
    suspend operator fun invoke(gateway: GatewayEntity) {
        gatewayRepository.update(gateway)
    }
}

class DeleteGatewayCase(
    private val gatewayRepository: GatewayRepository
) {
    suspend operator fun invoke(gateway: GatewayEntity) {
        gatewayRepository.delete(gateway)
    }
}

class DeleteAllGatewaysCase(
    private val gatewayRepository: GatewayRepository
) {
    suspend operator fun invoke() {
        gatewayRepository.deleteAll()
    }
}

class SyncGatewaysFromApiCase(
    private val gatewayRepository: GatewayRepository
) {
    suspend operator fun invoke() {
        gatewayRepository.syncFromApi()
    }
}