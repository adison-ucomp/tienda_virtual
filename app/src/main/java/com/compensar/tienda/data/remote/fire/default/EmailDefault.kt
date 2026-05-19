package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.domain.model.EmailModel

object EmailDefault {

    fun getAll(): List<EmailModel> {
        return listOf(
            EmailModel(register = 1, param = "host", value = "smtp.gmail.com"),
            EmailModel(register = 2, param = "port", value = "587"),
            EmailModel(register = 3, param = "username", value = "adisonzenemij@gmail.com"),
            EmailModel(register = 4, param = "password", value = "sgmwkvekgccudawz"),
            EmailModel(register = 5, param = "security", value = "TLS"),
            EmailModel(register = 6, param = "from", value = "adisonzenemij@gmail.com"),
            EmailModel(register = 7, param = "auth", value = "true"),
            EmailModel(register = 8, param = "timeout", value = "10000")
        )
    }
}
