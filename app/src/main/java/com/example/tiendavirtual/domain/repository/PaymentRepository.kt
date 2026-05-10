package com.example.tiendavirtual.domain.repository

import com.example.tiendavirtual.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun getAll(): Flow<List<PaymentEntity>>

    suspend fun getByRegister(register: Long): PaymentEntity?

    suspend fun syncFromApi()

    suspend fun insert(payment: PaymentEntity): Long

    suspend fun update(payment: PaymentEntity)

    suspend fun delete(payment: PaymentEntity)

    suspend fun deleteAll()
}