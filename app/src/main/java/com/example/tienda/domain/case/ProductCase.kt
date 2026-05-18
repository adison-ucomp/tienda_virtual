package com.example.tienda.domain.case

import com.example.tienda.data.local.entity.ProductEntity
import com.example.tienda.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

data class ProductCase(
    val createProduct: CreateProductCase,
    val getProducts: GetProductsCase,
    val getProductByRegister: GetProductByRegisterCase,
    val getProductsByCategory: GetProductsByCategoryCase,
    val getProductsByShop: GetProductsByShopCase,
    val searchProductsByName: SearchProductsByNameCase,
    val updateProduct: UpdateProductCase,
    val deleteProduct: DeleteProductCase,
    val deleteAllProducts: DeleteAllProductsCase,
    val syncProductsFromApi: SyncProductsFromApiCase
)

class CreateProductCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: ProductEntity): Long {
        return productRepository.insert(product)
    }
}

class GetProductsCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<ProductEntity>> {
        return productRepository.getAll()
    }
}

class GetProductByRegisterCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(register: Long): ProductEntity? {
        return productRepository.getByRegister(register)
    }
}

class GetProductsByCategoryCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(idCategory: Long): Flow<List<ProductEntity>> {
        return productRepository.getByCategory(idCategory)
    }
}

class GetProductsByShopCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(idShop: Long): Flow<List<ProductEntity>> {
        return productRepository.getByShop(idShop)
    }
}

class SearchProductsByNameCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(name: String): Flow<List<ProductEntity>> {
        return productRepository.searchByName(name)
    }
}

class UpdateProductCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: ProductEntity) {
        productRepository.update(product)
    }
}

class DeleteProductCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: ProductEntity) {
        productRepository.delete(product)
    }
}

class DeleteAllProductsCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke() {
        productRepository.deleteAll()
    }
}

class SyncProductsFromApiCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke() {
        productRepository.syncFromApi()
    }
}