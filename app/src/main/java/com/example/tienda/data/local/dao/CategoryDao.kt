package com.example.tienda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tienda.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

// Categorias
@Dao
interface CategoryDao {

    @Query("SELECT * FROM category ORDER BY register ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM category")
    suspend fun deleteAll()
}