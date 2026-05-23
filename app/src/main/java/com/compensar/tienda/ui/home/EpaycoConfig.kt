package com.compensar.tienda.ui.home

import com.compensar.tienda.BuildConfig
import java.net.URLEncoder

/**
 * Objeto singleton [EpaycoConfig].
 *
 * Responsable de la logica asociada al pantalla o helper del flujo de inicio/autenticacion/compra.
 */
object EpaycoConfig {
    const val CHECKOUT_URL = "https://checkout.epayco.co/checkout.js"

    val PUBLIC_KEY: String
        get() = BuildConfig.EPAYCO_PUBLIC_KEY

    val TEST_MODE: Boolean
        get() = BuildConfig.EPAYCO_TEST_MODE

    val MIN_AMOUNT: Double
        get() = BuildConfig.EPAYCO_MIN_AMOUNT.toDoubleOrNull() ?: 1000.0

    val MAX_AMOUNT: Double?
        get() = BuildConfig.EPAYCO_MAX_AMOUNT.toDoubleOrNull()

    val API_BASE_URL: String
        get() = BuildConfig.API_BASE_URL.trim().trimEnd('/') + "/"

    val SERVICE_URL: String
        get() = BuildConfig.EPAYCO_SERVICE_URL.trim().trimEnd('/')

    val CRON_MINUTES: Long
        get() = BuildConfig.EPAYCO_CRON_MINUTES.toLongOrNull()?.coerceAtLeast(1L) ?: 5L

    val CONFIRMATION_URL: String
        get() = API_BASE_URL + "api/epayco"

    val RESPONSE_URL: String
        get() = API_BASE_URL + "api/epayco"

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    fun validationLookupUrl(reference: String): String {
        val encoded = URLEncoder.encode(reference, "UTF-8")
        return API_BASE_URL + "api/epayco?lookup=1&reference=" + encoded
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    fun directValidationUrl(reference: String): String {
        val encoded = URLEncoder.encode(reference, "UTF-8")
        return "$SERVICE_URL/validation/v1/reference/$encoded"
    }

    /**
     * Valida reglas de negocio antes de continuar el flujo.
     */
    fun validateAmount(total: Double): String? {
        if (total < MIN_AMOUNT) {
            return "El valor mínimo permitido para pagar con ePayco es $ ${String.format("%,.0f", MIN_AMOUNT)}."
        }

        val maximum = MAX_AMOUNT
        if (maximum != null && maximum > 0.0 && total > maximum) {
            return "El valor máximo permitido para pagar con ePayco es $ ${String.format("%,.0f", maximum)}."
        }

        return null
    }
}

