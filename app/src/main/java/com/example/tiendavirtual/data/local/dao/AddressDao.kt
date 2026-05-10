package com.example.tiendavirtual.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiendavirtual.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

// Direcciones de Envio
@Dao
interface AddressDao {

    @Query("SELECT * FROM address_send ORDER BY register ASC")
    fun getAll(): Flow<List<AddressEntity>>

    @Query("SELECT * FROM address_send WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): AddressEntity?

    @Query("SELECT * FROM address_send WHERE id_user = :idUser ORDER BY register ASC")
    fun getByUser(idUser: Long): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: AddressEntity): Long

    @Update
    suspend fun update(address: AddressEntity)

    @Delete
    suspend fun delete(address: AddressEntity)

    @Query("DELETE FROM address_send")
    suspend fun deleteAll()
}