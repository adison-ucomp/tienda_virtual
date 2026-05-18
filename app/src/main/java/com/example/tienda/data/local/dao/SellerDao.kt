package com.example.tienda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tienda.data.local.entity.SellerEntity
import kotlinx.coroutines.flow.Flow

// Vendedores
@Dao
interface SellerDao {

    @Query("SELECT * FROM seller ORDER BY register ASC")
    fun getAll(): Flow<List<SellerEntity>>

    @Query("SELECT * FROM seller WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): SellerEntity?

    @Query("SELECT * FROM seller WHERE id_user = :idUser LIMIT 1")
    suspend fun getByUser(idUser: Long): SellerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seller: SellerEntity): Long

    @Update
    suspend fun update(seller: SellerEntity)

    @Delete
    suspend fun delete(seller: SellerEntity)

    @Query("DELETE FROM seller")
    suspend fun deleteAll()
}