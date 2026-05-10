package com.example.tiendavirtual.domain.case

import com.example.tiendavirtual.data.local.entity.PaymentEntity
import com.example.tiendavirtual.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

data class PaymentCase(
    val createPayment: CreatePaymentCase,
    val getPayments: GetPaymentsCase,
    val getPaymentByRegister: GetPaymentByRegisterCase,
    val updatePayment: UpdatePaymentCase,
    val deletePayment: DeletePaymentCase,
    val deleteAllPayments: DeleteAllPaymentsCase,
    val syncPaymentsFromApi: SyncPaymentsFromApiCase
)

class CreatePaymentCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(payment: PaymentEntity): Long {
        return paymentRepository.insert(payment)
    }
}

class GetPaymentsCase(
    private val paymentRepository: PaymentRepository
) {
    operator fun invoke(): Flow<List<PaymentEntity>> {
        return paymentRepository.getAll()
    }
}

class GetPaymentByRegisterCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(register: Long): PaymentEntity? {
        return paymentRepository.getByRegister(register)
    }
}

class UpdatePaymentCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(payment: PaymentEntity) {
        paymentRepository.update(payment)
    }
}

class DeletePaymentCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(payment: PaymentEntity) {
        paymentRepository.delete(payment)
    }
}

class DeleteAllPaymentsCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke() {
        paymentRepository.deleteAll()
    }
}

class SyncPaymentsFromApiCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke() {
        paymentRepository.syncFromApi()
    }
}