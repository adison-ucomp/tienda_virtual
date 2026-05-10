package com.example.tiendavirtual.domain.repository.impl

import com.example.tiendavirtual.data.local.dao.UserDao
import com.example.tiendavirtual.data.local.entity.UserEntity
import com.example.tiendavirtual.data.mapper.toDto
import com.example.tiendavirtual.data.mapper.toEntity
import com.example.tiendavirtual.data.remote.api.UserApi
import com.example.tiendavirtual.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepoImpl(
    private val userDao: UserDao,
    private val userApi: UserApi
) : UserRepository {

    override fun getAll(): Flow<List<UserEntity>> {
        return userDao.getAll()
    }

    override suspend fun getByRegister(register: Long): UserEntity? {
        return userDao.getByRegister(register)
    }

    override suspend fun getByEmail(email: String): UserEntity? {
        return userDao.getByEmail(email)
    }

    override fun getByRole(idRole: Long): Flow<List<UserEntity>> {
        return userDao.getByRole(idRole)
    }

    override suspend fun syncFromApi() {
        val users = userApi.getAll()
        users.forEach { dto ->
            userDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(user: UserEntity): Long {
        val savedUser = userApi.insert(user.toDto())
        return userDao.insert(savedUser.toEntity())
    }

    override suspend fun update(user: UserEntity) {
        val updatedUser = userApi.update(user.register, user.toDto())
        userDao.update(updatedUser.toEntity())
    }

    override suspend fun delete(user: UserEntity) {
        userApi.delete(user.register)
        userDao.delete(user)
    }

    override suspend fun deleteAll() {
        userDao.deleteAll()
    }
}