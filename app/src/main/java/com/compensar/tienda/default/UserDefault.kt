package com.compensar.tienda.default

import com.compensar.tienda.model.UserModel
import java.security.MessageDigest

object UserDefault {

    fun getAll(): List<UserModel> {
        return listOf(
            UserModel(
                register = 1,
                names = "Admin",
                srnms = "Admin",
                email = "test@adisonjimenez.dev",
                password = encryptPassword("Admin"),
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fuser%2F1_1779207007566.jpg?alt=media&token=7b8bc578-7de0-4787-bf0f-1d450c48a144",
                idRole = 1
            ),
            UserModel(
                register = 2,
                names = "Adison",
                srnms = "Jimenez",
                email = "adisonzenemij@gmail.com",
                password = encryptPassword("Adison"),
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fuser%2F2_1779208115377.jpg?alt=media&token=370089a3-12ee-4e60-aa75-8ec91f6c3c1e",
                idRole = 2
            ),
            UserModel(
                register = 3,
                names = "Daniela",
                srnms = "Fajardo",
                email = "danif1701@gmail.com",
                password = encryptPassword("Daniela"),
                storefire = "https://firebasestorage.googleapis.com/v0/b/engineering-code.firebasestorage.app/o/images%2Fuser%2F3_1779208125396.jpg?alt=media&token=52f52d63-848c-40dd-b272-44d5946fff9c",
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
