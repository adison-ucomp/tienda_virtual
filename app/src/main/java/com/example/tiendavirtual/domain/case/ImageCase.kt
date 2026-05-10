package com.example.tiendavirtual.domain.case

import com.example.tiendavirtual.data.local.entity.ImageEntity
import com.example.tiendavirtual.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow

data class ImageCase(
    val createImage: CreateImageCase,
    val getImages: GetImagesCase,
    val getImageByRegister: GetImageByRegisterCase,
    val getImagesByProduct: GetImagesByProductCase,
    val updateImage: UpdateImageCase,
    val deleteImage: DeleteImageCase,
    val deleteAllImages: DeleteAllImagesCase,
    val syncImagesFromApi: SyncImagesFromApiCase
)

class CreateImageCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(image: ImageEntity): Long {
        return imageRepository.insert(image)
    }
}

class GetImagesCase(
    private val imageRepository: ImageRepository
) {
    operator fun invoke(): Flow<List<ImageEntity>> {
        return imageRepository.getAll()
    }
}

class GetImageByRegisterCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(register: Long): ImageEntity? {
        return imageRepository.getByRegister(register)
    }
}

class GetImagesByProductCase(
    private val imageRepository: ImageRepository
) {
    operator fun invoke(idProduct: Long): Flow<List<ImageEntity>> {
        return imageRepository.getByProduct(idProduct)
    }
}

class UpdateImageCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(image: ImageEntity) {
        imageRepository.update(image)
    }
}

class DeleteImageCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(image: ImageEntity) {
        imageRepository.delete(image)
    }
}

class DeleteAllImagesCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke() {
        imageRepository.deleteAll()
    }
}

class SyncImagesFromApiCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke() {
        imageRepository.syncFromApi()
    }
}