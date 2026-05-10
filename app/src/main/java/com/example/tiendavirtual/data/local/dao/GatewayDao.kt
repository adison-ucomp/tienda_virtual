package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.GatewayEntity
import kotlinx.coroutines.flow.Flow

// Pasarelas de Pago
@Dao
interface GatewayDao {

    @Query("SELECT * FROM gateway ORDER BY register ASC")
    fun getAll(): Flow<List<GatewayEntity>>

    @Query("SELECT * FROM gateway WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): GatewayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gateway: GatewayEntity): Long

    @Update
    suspend fun update(gateway: GatewayEntity)

    @Delete
    suspend fun delete(gateway: GatewayEntity)

    @Query("DELETE FROM gateway")
    suspend fun deleteAll()
}