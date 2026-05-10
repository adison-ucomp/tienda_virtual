package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.RoleEntity
import kotlinx.coroutines.flow.Flow

// Roles
@Dao
interface RoleDao {

    @Query("SELECT * FROM role ORDER BY register ASC")
    fun getAll(): Flow<List<RoleEntity>>

    @Query("SELECT * FROM role WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): RoleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: RoleEntity): Long

    @Update
    suspend fun update(role: RoleEntity)

    @Delete
    suspend fun delete(role: RoleEntity)

    @Query("DELETE FROM role")
    suspend fun deleteAll()
}