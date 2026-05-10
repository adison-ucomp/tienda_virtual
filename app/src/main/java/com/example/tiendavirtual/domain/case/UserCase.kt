package com.example.tiendavirtual.domain.case

import com.example.tiendavirtual.data.local.entity.UserEntity
import com.example.tiendavirtual.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

data class UserCase(
    val createUser: CreateUserCase,
    val getUsers: GetUsersCase,
    val getUserByRegister: GetUserByRegisterCase,
    val getUserByEmail: GetUserByEmailCase,
    val getUsersByRole: GetUsersByRoleCase,
    val updateUser: UpdateUserCase,
    val deleteUser: DeleteUserCase,
    val deleteAllUsers: DeleteAllUsersCase,
    val syncUsersFromApi: SyncUsersFromApiCase
)

class CreateUserCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity): Long {
        return userRepository.insert(user)
    }
}

class GetUsersCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<UserEntity>> {
        return userRepository.getAll()
    }
}

class GetUserByRegisterCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(register: Long): UserEntity? {
        return userRepository.getByRegister(register)
    }
}

class GetUserByEmailCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): UserEntity? {
        return userRepository.getByEmail(email)
    }
}

class GetUsersByRoleCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(idRole: Long): Flow<List<UserEntity>> {
        return userRepository.getByRole(idRole)
    }
}

class UpdateUserCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity) {
        userRepository.update(user)
    }
}

class DeleteUserCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity) {
        userRepository.delete(user)
    }
}

class DeleteAllUsersCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.deleteAll()
    }
}

class SyncUsersFromApiCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.syncFromApi()
    }
}