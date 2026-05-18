package com.compensar.tienda.domain.case

import com.compensar.tienda.data.local.entity.SpecifyEntity
import com.compensar.tienda.domain.repository.SpecifyRepository
import kotlinx.coroutines.flow.Flow

data class SpecifyCase(
    val createSpecify: CreateSpecifyCase,
    val getSpecifications: GetSpecificationsCase,
    val getSpecifyByRegister: GetSpecifyByRegisterCase,
    val getSpecificationsByProduct: GetSpecificationsByProductCase,
    val updateSpecify: UpdateSpecifyCase,
    val deleteSpecify: DeleteSpecifyCase,
    val deleteAllSpecifications: DeleteAllSpecificationsCase,
    val syncSpecificationsFromApi: SyncSpecificationsFromApiCase
)

class CreateSpecifyCase(
    private val specifyRepository: SpecifyRepository
) {
    suspend operator fun invoke(specify: SpecifyEntity): Long {
        return specifyRepository.insert(specify)
    }
}

class GetSpecificationsCase(
    private val specifyRepository: SpecifyRepository
) {
    operator fun invoke(): Flow<List<SpecifyEntity>> {
        return specifyRepository.getAll()
    }
}

class GetSpecifyByRegisterCase(
    private val specifyRepository: SpecifyRepository
) {
    suspend operator fun invoke(register: Long): SpecifyEntity? {
        return specifyRepository.getByRegister(register)
    }
}

class GetSpecificationsByProductCase(
    private val specifyRepository: SpecifyRepository
) {
    operator fun invoke(idProduct: Long): Flow<List<SpecifyEntity>> {
        return specifyRepository.getByProduct(idProduct)
    }
}

class UpdateSpecifyCase(
    private val specifyRepository: SpecifyRepository
) {
    suspend operator fun invoke(specify: SpecifyEntity) {
        specifyRepository.update(specify)
    }
}

class DeleteSpecifyCase(
    private val specifyRepository: SpecifyRepository
) {
    suspend operator fun invoke(specify: SpecifyEntity) {
        specifyRepository.delete(specify)
    }
}

class DeleteAllSpecificationsCase(
    private val specifyRepository: SpecifyRepository
) {
    suspend operator fun invoke() {
        specifyRepository.deleteAll()
    }
}

class SyncSpecificationsFromApiCase(
    private val specifyRepository: SpecifyRepository
) {
    suspend operator fun invoke() {
        specifyRepository.syncFromApi()
    }
}