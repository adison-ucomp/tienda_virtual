package com.compensar.tienda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.compensar.tienda.data.local.entity.SpecifyEntity
import kotlinx.coroutines.flow.Flow

// Especificaciones
@Dao
interface SpecifyDao {

    @Query("SELECT * FROM specify ORDER BY register ASC")
    fun getAll(): Flow<List<SpecifyEntity>>

    @Query("SELECT * FROM specify WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): SpecifyEntity?

    @Query("SELECT * FROM specify WHERE id_product = :idProduct ORDER BY register ASC")
    fun getByProduct(idProduct: Long): Flow<List<SpecifyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(specify: SpecifyEntity): Long

    @Update
    suspend fun update(specify: SpecifyEntity)

    @Delete
    suspend fun delete(specify: SpecifyEntity)

    @Query("DELETE FROM specify")
    suspend fun deleteAll()
}