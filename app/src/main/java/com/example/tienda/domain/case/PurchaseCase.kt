package com.example.tienda.domain.case

import com.example.tienda.data.local.entity.PurchaseEntity
import com.example.tienda.domain.repository.PurchaseRepository
import kotlinx.coroutines.flow.Flow

data class PurchaseCase(
    val createPurchase: CreatePurchaseCase,
    val getPurchases: GetPurchasesCase,
    val getPurchaseByRegister: GetPurchaseByRegisterCase,
    val getPurchasesByUser: GetPurchasesByUserCase,
    val getPurchasesByProduct: GetPurchasesByProductCase,
    val getPurchasesByDate: GetPurchasesByDateCase,
    val updatePurchase: UpdatePurchaseCase,
    val deletePurchase: DeletePurchaseCase,
    val deleteAllPurchases: DeleteAllPurchasesCase,
    val syncPurchasesFromApi: SyncPurchasesFromApiCase
)

class CreatePurchaseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(purchase: PurchaseEntity): Long {
        return purchaseRepository.insert(purchase)
    }
}

class GetPurchasesCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(): Flow<List<PurchaseEntity>> {
        return purchaseRepository.getAll()
    }
}

class GetPurchaseByRegisterCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(register: Long): PurchaseEntity? {
        return purchaseRepository.getByRegister(register)
    }
}

class GetPurchasesByUserCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(idUser: Long): Flow<List<PurchaseEntity>> {
        return purchaseRepository.getByUser(idUser)
    }
}

class GetPurchasesByProductCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(idProduct: Long): Flow<List<PurchaseEntity>> {
        return purchaseRepository.getByProduct(idProduct)
    }
}

class GetPurchasesByDateCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(date: String): Flow<List<PurchaseEntity>> {
        return purchaseRepository.getByDate(date)
    }
}

class UpdatePurchaseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(purchase: PurchaseEntity) {
        purchaseRepository.update(purchase)
    }
}

class DeletePurchaseCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(purchase: PurchaseEntity) {
        purchaseRepository.delete(purchase)
    }
}

class DeleteAllPurchasesCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke() {
        purchaseRepository.deleteAll()
    }
}

class SyncPurchasesFromApiCase(
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke() {
        purchaseRepository.syncFromApi()
    }
}