package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.model.EmailModel

/**
 * El modelo de configuración SMTP ya no se inicializa en Firestore.
 * El envío de correos se delega al backend PHP.
 */
object EmailDefault {
    fun getAll(): List<EmailModel> = emptyList()
}
