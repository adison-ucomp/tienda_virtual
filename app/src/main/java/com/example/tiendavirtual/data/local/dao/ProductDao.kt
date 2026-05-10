package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

// Productos
@Dao
interface ProductDao {

    @Query("SELECT * FROM product ORDER BY register ASC")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): ProductEntity?

    @Query("SELECT * FROM product WHERE id_category = :idCategory ORDER BY register ASC")
    fun getByCategory(idCategory: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE id_shop = :idShop ORDER BY register ASC")
    fun getByShop(idShop: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%' ORDER BY register ASC")
    fun searchByName(name: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM product")
    suspend fun deleteAll()
}