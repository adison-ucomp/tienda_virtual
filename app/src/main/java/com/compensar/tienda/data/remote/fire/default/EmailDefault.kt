package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.domain.model.EmailModel

object EmailDefault {

    fun getAll(): List<EmailModel> {
        return listOf(
            EmailModel(register = 1, param = "host", value = ""),
            EmailModel(register = 2, param = "port", value = ""),
            EmailModel(register = 3, param = "username", value = ""),
            EmailModel(register = 4, param = "password", value = ""),
            EmailModel(register = 5, param = "security", value = ""),
            EmailModel(register = 6, param = "from", value = ""),
            EmailModel(register = 7, param = "auth", value = ""),
            EmailModel(register = 8, param = "timeout", value = "")
        )
    }
}
