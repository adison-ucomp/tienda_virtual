package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.ShopEntity
import kotlinx.coroutines.flow.Flow

// Tiendas
@Dao
interface ShopDao {

    @Query("SELECT * FROM shop ORDER BY register ASC")
    fun getAll(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shop WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): ShopEntity?

    @Query("SELECT * FROM shop WHERE id_seller = :idSeller ORDER BY register ASC")
    fun getBySeller(idSeller: Long): Flow<List<ShopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shop: ShopEntity): Long

    @Update
    suspend fun update(shop: ShopEntity)

    @Delete
    suspend fun delete(shop: ShopEntity)

    @Query("DELETE FROM shop")
    suspend fun deleteAll()
}