package com.example.tienda.domain.repository.impl

import com.example.tienda.data.local.dao.RoleDao
import com.example.tienda.data.local.entity.RoleEntity
import com.example.tienda.data.mapper.toDto
import com.example.tienda.data.mapper.toEntity
import com.example.tienda.data.remote.api.RoleApi
import com.example.tienda.domain.repository.RoleRepository
import kotlinx.coroutines.flow.Flow

class RoleRepoImpl(
    private val roleDao: RoleDao,
    private val roleApi: RoleApi
) : RoleRepository {

    override fun getAll(): Flow<List<RoleEntity>> {
        return roleDao.getAll()
    }

    override suspend fun getByRegister(register: Long): RoleEntity? {
        return roleDao.getByRegister(register)
    }

    override suspend fun syncFromApi() {
        val roles = roleApi.getAll()
        roles.forEach { dto ->
            roleDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(role: RoleEntity): Long {
        val savedRole = roleApi.insert(role.toDto())
        return roleDao.insert(savedRole.toEntity())
    }

    override suspend fun update(role: RoleEntity) {
        val updatedRole = roleApi.update(role.register, role.toDto())
        roleDao.update(updatedRole.toEntity())
    }

    override suspend fun delete(role: RoleEntity) {
        roleApi.delete(role.register)
        roleDao.delete(role)
    }

    override suspend fun deleteAll() {
        roleDao.deleteAll()
    }
}