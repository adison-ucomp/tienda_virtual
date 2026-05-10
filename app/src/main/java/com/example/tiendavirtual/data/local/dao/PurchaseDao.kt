package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

// Compras
@Dao
interface PurchaseDao {

    @Query("SELECT * FROM purchase ORDER BY register DESC")
    fun getAll(): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchase WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): PurchaseEntity?

    @Query("SELECT * FROM purchase WHERE id_user = :idUser ORDER BY register DESC")
    fun getByUser(idUser: Long): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchase WHERE id_product = :idProduct ORDER BY register DESC")
    fun getByProduct(idProduct: Long): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchase WHERE date = :date ORDER BY register DESC")
    fun getByDate(date: String): Flow<List<PurchaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PurchaseEntity): Long

    @Update
    suspend fun update(purchase: PurchaseEntity)

    @Delete
    suspend fun delete(purchase: PurchaseEntity)

    @Query("DELETE FROM purchase")
    suspend fun deleteAll()
}