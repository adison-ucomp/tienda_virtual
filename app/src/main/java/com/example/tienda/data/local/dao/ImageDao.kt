package com.example.tienda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tienda.data.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

// Imagenes
@Dao
interface ImageDao {

    @Query("SELECT * FROM image ORDER BY register ASC")
    fun getAll(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM image WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): ImageEntity?

    @Query("SELECT * FROM image WHERE id_product = :idProduct ORDER BY register ASC")
    fun getByProduct(idProduct: Long): Flow<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity): Long

    @Update
    suspend fun update(image: ImageEntity)

    @Delete
    suspend fun delete(image: ImageEntity)

    @Query("DELETE FROM image")
    suspend fun deleteAll()
}