package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

// Usuarios
@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY register ASC")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): UserEntity?

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE id_role = :idRole ORDER BY register ASC")
    fun getByRole(idRole: Long): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}