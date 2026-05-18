package com.example.tienda.domain.case

import com.example.tienda.data.local.entity.RoleEntity
import com.example.tienda.domain.repository.RoleRepository
import kotlinx.coroutines.flow.Flow

data class RoleCase(
    val createRole: CreateRoleCase,
    val getRoles: GetRolesCase,
    val getRoleByRegister: GetRoleByRegisterCase,
    val updateRole: UpdateRoleCase,
    val deleteRole: DeleteRoleCase,
    val deleteAllRoles: DeleteAllRolesCase,
    val syncRolesFromApi: SyncRolesFromApiCase
)

class CreateRoleCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(role: RoleEntity): Long {
        return roleRepository.insert(role)
    }
}

class GetRolesCase(
    private val roleRepository: RoleRepository
) {
    operator fun invoke(): Flow<List<RoleEntity>> {
        return roleRepository.getAll()
    }
}

class GetRoleByRegisterCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(register: Long): RoleEntity? {
        return roleRepository.getByRegister(register)
    }
}

class UpdateRoleCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(role: RoleEntity) {
        roleRepository.update(role)
    }
}

class DeleteRoleCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(role: RoleEntity) {
        roleRepository.delete(role)
    }
}

class DeleteAllRolesCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke() {
        roleRepository.deleteAll()
    }
}

class SyncRolesFromApiCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke() {
        roleRepository.syncFromApi()
    }
}