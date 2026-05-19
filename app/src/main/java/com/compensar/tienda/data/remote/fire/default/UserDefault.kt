package com.compensar.tienda.data.remote.fire.default

import com.compensar.tienda.domain.model.UserModel
import java.security.MessageDigest

object UserDefault {

    fun getAll(): List<UserModel> {
        return listOf(
            UserModel(
                register = 1,
                names = "Admin",
                srnms = "Admin",
                email = "admin@gmail.com",
                password = encryptPassword("Admin"),
                storefire = "Url",
                idRole = 1
            ),
            UserModel(
                register = 2,
                names = "Adison",
                srnms = "Jimenez",
                email = "adisonzenemij@gmail.com",
                password = encryptPassword("Adison"),
                storefire = "Url",
                idRole = 2
            ),
            UserModel(
                register = 3,
                names = "Daniela",
                srnms = "Fajardo",
                email = "danif1701@gmail.com",
                password = encryptPassword("Daniela"),
                storefire = "Url",
                idRole = 3
            )
        )
    }

    private fun encryptPassword(password: String): String {
        val salt = "com.compensar.tienda.user.password"
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }
}