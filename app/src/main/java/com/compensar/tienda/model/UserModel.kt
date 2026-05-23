package com.compensar.tienda.model

/**
 * Modelo de datos para la entidad User.
 * @property register Identificador unico del registro.
 * @property names Nombres del usuario.
 * @property srnms Apellidos del usuario.
 * @property email Correo electronico asociado.
 * @property password Contrasena almacenada (hash o valor procesado).
 * @property storefire URL del recurso almacenado en Firebase Storage.
 * @property idRole Identificador de referencia a otra entidad.
 * @property biometric Indica si la autenticacion biometrica esta habilitada.
 */
data class UserModel(
    val register: Long = 0,
    val names: String? = null,
    val srnms: String? = null,
    val email: String? = null,
    val password: String? = null,
    val storefire: String? = null,
    val idRole: Long = 0,
    val biometric: Boolean = false
)

