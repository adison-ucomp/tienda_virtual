package com.example.tiendavirtual.domain.case

import com.example.tiendavirtual.data.local.entity.CategoryEntity
import com.example.tiendavirtual.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

data class CategoryCase(
    val createCategory: CreateCategoryCase,
    val getCategories: GetCategoriesCase,
    val getCategoryByRegister: GetCategoryByRegisterCase,
    val updateCategory: UpdateCategoryCase,
    val deleteCategory: DeleteCategoryCase,
    val deleteAllCategories: DeleteAllCategoriesCase,
    val syncCategoriesFromApi: SyncCategoriesFromApiCase
)

class CreateCategoryCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: CategoryEntity): Long {
        return categoryRepository.insert(category)
    }
}

class GetCategoriesCase(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<CategoryEntity>> {
        return categoryRepository.getAll()
    }
}

class GetCategoryByRegisterCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(register: Long): CategoryEntity? {
        return categoryRepository.getByRegister(register)
    }
}

class UpdateCategoryCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: CategoryEntity) {
        categoryRepository.update(category)
    }
}

class DeleteCategoryCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: CategoryEntity) {
        categoryRepository.delete(category)
    }
}

class DeleteAllCategoriesCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() {
        categoryRepository.deleteAll()
    }
}

class SyncCategoriesFromApiCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() {
        categoryRepository.syncFromApi()
    }
}