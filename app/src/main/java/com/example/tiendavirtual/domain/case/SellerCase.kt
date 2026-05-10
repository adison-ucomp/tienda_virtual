package com.example.tiendavirtual.domain.case

import com.example.tiendavirtual.data.local.entity.SellerEntity
import com.example.tiendavirtual.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow

data class SellerCase(
    val createSeller: CreateSellerCase,
    val getSellers: GetSellersCase,
    val getSellerByRegister: GetSellerByRegisterCase,
    val getSellerByUser: GetSellerByUserCase,
    val updateSeller: UpdateSellerCase,
    val deleteSeller: DeleteSellerCase,
    val deleteAllSellers: DeleteAllSellersCase,
    val syncSellersFromApi: SyncSellersFromApiCase
)

class CreateSellerCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke(seller: SellerEntity): Long {
        return sellerRepository.insert(seller)
    }
}

class GetSellersCase(
    private val sellerRepository: SellerRepository
) {
    operator fun invoke(): Flow<List<SellerEntity>> {
        return sellerRepository.getAll()
    }
}

class GetSellerByRegisterCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke(register: Long): SellerEntity? {
        return sellerRepository.getByRegister(register)
    }
}

class GetSellerByUserCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke(idUser: Long): SellerEntity? {
        return sellerRepository.getByUser(idUser)
    }
}

class UpdateSellerCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke(seller: SellerEntity) {
        sellerRepository.update(seller)
    }
}

class DeleteSellerCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke(seller: SellerEntity) {
        sellerRepository.delete(seller)
    }
}

class DeleteAllSellersCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke() {
        sellerRepository.deleteAll()
    }
}

class SyncSellersFromApiCase(
    private val sellerRepository: SellerRepository
) {
    suspend operator fun invoke() {
        sellerRepository.syncFromApi()
    }
}