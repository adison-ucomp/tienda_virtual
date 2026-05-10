package com.example.tiendavirtual.domain.case

import com.example.tiendavirtual.data.local.entity.ShopEntity
import com.example.tiendavirtual.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

data class ShopCase(
    val createShop: CreateShopCase,
    val getShops: GetShopsCase,
    val getShopByRegister: GetShopByRegisterCase,
    val getShopsBySeller: GetShopsBySellerCase,
    val updateShop: UpdateShopCase,
    val deleteShop: DeleteShopCase,
    val deleteAllShops: DeleteAllShopsCase,
    val syncShopsFromApi: SyncShopsFromApiCase
)

class CreateShopCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shop: ShopEntity): Long {
        return shopRepository.insert(shop)
    }
}

class GetShopsCase(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<List<ShopEntity>> {
        return shopRepository.getAll()
    }
}

class GetShopByRegisterCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(register: Long): ShopEntity? {
        return shopRepository.getByRegister(register)
    }
}

class GetShopsBySellerCase(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(idSeller: Long): Flow<List<ShopEntity>> {
        return shopRepository.getBySeller(idSeller)
    }
}

class UpdateShopCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shop: ShopEntity) {
        shopRepository.update(shop)
    }
}

class DeleteShopCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shop: ShopEntity) {
        shopRepository.delete(shop)
    }
}

class DeleteAllShopsCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke() {
        shopRepository.deleteAll()
    }
}

class SyncShopsFromApiCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke() {
        shopRepository.syncFromApi()
    }
}