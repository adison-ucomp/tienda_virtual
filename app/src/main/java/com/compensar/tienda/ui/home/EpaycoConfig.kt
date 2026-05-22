package com.compensar.tienda.ui.home

import com.compensar.tienda.BuildConfig

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

    val CONFIRMATION_URL: String
        get() = API_BASE_URL + "api/epayco"

    val RESPONSE_URL: String
        get() = API_BASE_URL + "api/epayco"

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
