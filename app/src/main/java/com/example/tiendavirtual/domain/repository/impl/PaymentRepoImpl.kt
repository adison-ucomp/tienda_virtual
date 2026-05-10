package com.example.tiendavirtual.domain.repository.impl

import com.example.tiendavirtual.data.local.dao.PaymentDao
import com.example.tiendavirtual.data.local.entity.PaymentEntity
import com.example.tiendavirtual.data.mapper.toDto
import com.example.tiendavirtual.data.mapper.toEntity
import com.example.tiendavirtual.data.remote.api.PaymentApi
import com.example.tiendavirtual.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class PaymentRepoImpl(
    private val paymentDao: PaymentDao,
    private val paymentApi: PaymentApi
) : PaymentRepository {

    override fun getAll(): Flow<List<PaymentEntity>> {
        return paymentDao.getAll()
    }

    override suspend fun getByRegister(register: Long): PaymentEntity? {
        return paymentDao.getByRegister(register)
    }

    override suspend fun syncFromApi() {
        val payments = paymentApi.getAll()
        payments.forEach { dto ->
            paymentDao.insert(dto.toEntity())
        }
    }

    override suspend fun insert(payment: PaymentEntity): Long {
        val savedPayment = paymentApi.insert(payment.toDto())
        return paymentDao.insert(savedPayment.toEntity())
    }

    override suspend fun update(payment: PaymentEntity) {
        val updatedPayment = paymentApi.update(payment.register, payment.toDto())
        paymentDao.update(updatedPayment.toEntity())
    }

    override suspend fun delete(payment: PaymentEntity) {
        paymentApi.delete(payment.register)
        paymentDao.delete(payment)
    }

    override suspend fun deleteAll() {
        paymentDao.deleteAll()
    }
}