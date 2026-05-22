package com.compensar.tienda.ui.home

import com.compensar.tienda.BuildConfig

object EpaycoConfig {
    const val CHECKOUT_URL = "https://checkout.epayco.co/checkout.js"

    val PUBLIC_KEY: String
        get() = BuildConfig.EPAYCO_PUBLIC_KEY

    val TEST_MODE: Boolean
        get() = BuildConfig.EPAYCO_TEST_MODE
}
