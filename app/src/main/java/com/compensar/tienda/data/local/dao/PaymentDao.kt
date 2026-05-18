package com.compensar.tienda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.compensar.tienda.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

// Medios de Pago
@Dao
interface PaymentDao {

    @Query("SELECT * FROM payment ORDER BY register ASC")
    fun getAll(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payment WHERE register = :register LIMIT 1")
    suspend fun getByRegister(register: Long): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity): Long

    @Update
    suspend fun update(payment: PaymentEntity)

    @Delete
    suspend fun delete(payment: PaymentEntity)

    @Query("DELETE FROM payment")
    suspend fun deleteAll()
}