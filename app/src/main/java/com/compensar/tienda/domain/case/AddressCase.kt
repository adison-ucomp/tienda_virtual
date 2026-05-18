package com.compensar.tienda.domain.case

import com.compensar.tienda.data.local.entity.AddressEntity
import com.compensar.tienda.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow

data class AddressUse(
    val createAddress: CreateAddressCase,
    val getAddresses: GetAddressesCase,
    val getAddressByRegister: GetAddressByRegisterCase,
    val getAddressesByUser: GetAddressesByUserCase,
    val updateAddress: UpdateAddressCase,
    val deleteAddress: DeleteAddressCase,
    val deleteAllAddresses: DeleteAllAddressesCase,
    val syncAddressesFromApi: SyncAddressesFromApiCase
)

class CreateAddressCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(address: AddressEntity): Long {
        return addressRepository.insert(address)
    }
}

class GetAddressesCase(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(): Flow<List<AddressEntity>> {
        return addressRepository.getAll()
    }
}

class GetAddressByRegisterCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(register: Long): AddressEntity? {
        return addressRepository.getByRegister(register)
    }
}

class GetAddressesByUserCase(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(idUser: Long): Flow<List<AddressEntity>> {
        return addressRepository.getByUser(idUser)
    }
}

class UpdateAddressCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(address: AddressEntity) {
        addressRepository.update(address)
    }
}

class DeleteAddressCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(address: AddressEntity) {
        addressRepository.delete(address)
    }
}

class DeleteAllAddressesCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke() {
        addressRepository.deleteAll()
    }
}

class SyncAddressesFromApiCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke() {
        addressRepository.syncFromApi()
    }
}